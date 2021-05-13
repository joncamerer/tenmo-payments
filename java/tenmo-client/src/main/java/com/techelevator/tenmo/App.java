package com.techelevator.tenmo;

import com.techelevator.tenmo.models.*;
import com.techelevator.tenmo.services.*;
import com.techelevator.view.ConsoleService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

public class App {

private static final String API_BASE_URL = "http://localhost:8080/";
    
    private static final String MENU_OPTION_EXIT = "Exit";
    private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
	private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
	private static final String[] LOGIN_MENU_OPTIONS = { LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
	private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
	private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
	private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS, MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS, MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	
    private AuthenticatedUser currentUser;
    private ConsoleService console;
    private AuthenticationService authenticationService;
    private AccountService accountService;
    private UserService userService;
    private TransferService transferService;

    public static void main(String[] args) {
    	App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL),
						  new AccountService(API_BASE_URL), new UserService(API_BASE_URL),
						  new TransferService(API_BASE_URL));
    	app.run();
    }

    public App(ConsoleService console, AuthenticationService authenticationService,
			   AccountService accountService, UserService userService, TransferService transferService) {
		this.console = console;
		this.authenticationService = authenticationService;
		this.accountService = accountService;
		this.userService = userService;
		this.transferService = transferService;
	}

	public void run() {
		System.out.println("*********************");
		System.out.println("* Welcome to TEnmo! *");
		System.out.println("*********************");
		
		registerAndLogin();
		mainMenu();
	}

	private void mainMenu() {
		while(true) {
			String choice = (String)console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			String token = currentUser.getToken();

			if(MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
				viewCurrentBalance(token);
			} else if(MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
				viewTransferHistory(token);
			} else if(MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
				viewPendingRequests();
			} else if(MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
				sendBucks(token);
			} else if(MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
				requestBucks();
			} else if(MAIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else {
				// the only other option on the main menu is to exit
				exitProgram();
			}
		}
	}

	private void viewCurrentBalance(String token) {
		BigDecimal currentBalance = accountService.getCurrentBalance(token,
				currentUser.getUser().getId());

		System.out.println("Your current account balance is: $" + currentBalance);
		System.out.println();
		String returnToMenu = console.getUserInput(
				"Press enter to return to main menu");
	}

	private void viewTransferHistory(String token) {
		Transfer[] transfers = transferService.getAllTransfersByUserID(token,
				currentUser.getUser().getId());

		//Display transfer summaries
		displayTransferHeader();
		displayTransferSummaries(token, transfers);
		System.out.println();

		//Prompt for and validate proposed transfer Id for detail view
		Integer choiceId = null;
		boolean isValidId = false;

		while (!isValidId){
			choiceId = console.getUserInputInteger(
					"Please enter transfer ID to view details (0 to cancel)");

			if (choiceId == 0) {
				return;
			}

			Transfer proposedTransfer = transferService.getTransferByTransferID(token, choiceId);

			if (proposedTransfer == null){
				System.out.println();
				System.out.println("*** Invalid user ID ***");
				System.out.println();
			} else {
				isValidId = true;
			}
		}

		//Display transfer detail view
		viewTransferDetails(token, choiceId);
		System.out.println();
		String returnToMenu = console.getUserInput(
				"Press enter to return to main menu");
	}

	private void viewTransferDetails(String token, int choiceId){
    	Transfer transfer = transferService.getTransferByTransferID(token, choiceId);
		System.out.println();
    	System.out.println("-------------------------------------------");
		System.out.println("Transfer Details");
		System.out.println("-------------------------------------------");
		System.out.println("Id: " + transfer.getTransferId());
		System.out.println("From: " + userService.getUsernameByAccountId(token, transfer.getFromAccountId()));
		System.out.println("To: " + userService.getUsernameByAccountId(token, transfer.getToAccountId()));
		System.out.println("Type: " + transferService.getTransferType(token, transfer.getTransferId()));
		System.out.println("Status: " + transferService.getTransferStatus(token, transfer.getTransferId()));
		System.out.println("Amount: $" + transfer.getAmount());
	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub
		
	}

	private void sendBucks(String token) {
		//Display all users
		displayUsersHeader();
		printUsers(token);

		//Select user to transfer to
		int currentUserId = currentUser.getUser().getId();
		long fromAccount = accountService.getAccountByUserId(token, currentUserId).getAccountId();
		boolean isValidId = false;
		Long toAccount = null;

		while (!isValidId) {
			int getToUserId = console.getUserInputInteger("Enter ID of user you are sending to (0 to cancel)");
			//0 to exit
			if (getToUserId == 0) {
				return;
			}
			//Validate proposed userId
			try {
				toAccount = accountService.getAccountByUserId(token, getToUserId).getAccountId();
				isValidId = true;
			} catch (NullPointerException e) {
				System.out.println("*** Please select a valid user ID ***");
				System.out.println();
			}
		}

		//Set amount to Transfer
		BigDecimal amount = null;
		boolean isFormatted = false;

		while (!isFormatted) {
			try {
				amount = new BigDecimal(console.getUserInput("Enter amount"));
				if (amount.compareTo(BigDecimal.ZERO) == 0) {
					System.out.println("*** Cannot do transfer for $0.00 ***");
					return;
				}
				isFormatted = true;
			} catch (NumberFormatException e) {
				System.out.println("*** Amount must be a number ***");
			}
		}

		//Verify transfer is valid amount
		if (isValidAmount(amount)){
			//Create transfer
			Transfer thisTransfer = transferService.createSendingTransfer(currentUser.getToken(),
					fromAccount, toAccount, amount);
		} else {
			System.out.println("*** Insufficient Funds for Requested Transfer ***");
		}

		//Display currentUser's updated balance
		viewCurrentBalance(token);
	}

	private void requestBucks() {
		// TODO Auto-generated method stub
		
	}
	
	private void exitProgram() {
		System.exit(0);
	}

	private void registerAndLogin() {
		while(!isAuthenticated()) {
			String choice = (String)console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
			if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
				register();
			} else {
				// the only other option on the login menu is to exit
				exitProgram();
			}
		}
	}

	private boolean isAuthenticated() {
		return currentUser != null;
	}

	private void register() {
		System.out.println("Please register a new user account");
		boolean isRegistered = false;
        while (!isRegistered) //will keep looping until user is registered
        {
            UserCredentials credentials = collectUserCredentials();
            try {
            	authenticationService.register(credentials);
            	isRegistered = true;
            	System.out.println("Registration successful. You can now login.");
            } catch(AuthenticationServiceException e) {
            	System.out.println("REGISTRATION ERROR: "+e.getMessage());
				System.out.println("Please attempt to register again.");
            }
        }
	}

	private void login() {
		System.out.println("Please log in");
		currentUser = null;
		while (currentUser == null) //will keep looping until user is logged in
		{
			UserCredentials credentials = collectUserCredentials();
		    try {
				currentUser = authenticationService.login(credentials);
			} catch (AuthenticationServiceException e) {
				System.out.println("LOGIN ERROR: "+e.getMessage());
				System.out.println("Please attempt to login again.");
			}
		}
	}
	
	private UserCredentials collectUserCredentials() {
		String username = console.getUserInput("Username");
		String password = console.getUserInput("Password");
		return new UserCredentials(username, password);
	}

	private void printUsers(String token)
	{
		User[] users = userService.listAllUsers(token);
		// List all users
		for (User user : users) {
			System.out.printf("%-5s %-20s", user.getId(), user.getUsername());
			System.out.println();
		}
		System.out.println();

	}

	private void displayUsersHeader(){
		System.out.println("-------------------------------------------");
		System.out.println("Users");
		System.out.printf("%-5s %-20s", "ID", "Name");
		System.out.println();
		System.out.println("-------------------------------------------");
	}

	private void displayTransferHeader(){
		System.out.println("-------------------------------------------");
		System.out.println("Transfers");
		System.out.printf("%-10s %-25s %-10s", "ID", "From/To", "Amount");
		System.out.println();
		System.out.println("-------------------------------------------");
	}

	private void displayTransferSummaries(String token, Transfer[] transfers) {
		for (Transfer transfer : transfers){
			//Determine To/From formatting
			int currentUserId = currentUser.getUser().getId();
			Long currentUserAccountId = accountService.getAccountByUserId(token, currentUserId).getAccountId();
			String toName = userService.getUsernameByAccountId(token, transfer.getToAccountId());
			String fromName = userService.getUsernameByAccountId(token, transfer.getFromAccountId());

			String fromTo = (currentUserAccountId.equals(transfer.getFromAccountId())) ?
					"To: " + toName : "From: " + fromName;

			System.out.printf("%-10s %-25s %-10s", transfer.getTransferId() + ": ", fromTo, "$ " + transfer.getAmount());
			System.out.println();
		}
	}

	private boolean isValidAmount(BigDecimal amount) {
    	BigDecimal currentBalance = accountService.getAccountByUserId(currentUser.getToken(),
				currentUser.getUser().getId()).getBalance();
    	if (currentBalance.compareTo(amount) >= 0) {
			return true;
		}
    	return false;
	}
}

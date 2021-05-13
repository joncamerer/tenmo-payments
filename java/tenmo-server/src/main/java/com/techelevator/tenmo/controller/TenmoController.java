package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.dao.TransferDAO;
import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.exception.AccountNotFoundException;
import com.techelevator.tenmo.exception.TransferNotFoundException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class TenmoController {

    @Autowired
    private AccountDAO accountDAO;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private TransferDAO transferDAO;

    @ApiOperation("Returns all users in the system")
    @RequestMapping(path = "/users", method = RequestMethod.GET)
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    @ApiOperation("Returns an account balance by users id")
    @RequestMapping(path = "/users/{id}/balance", method = RequestMethod.GET)
    public BigDecimal getBalance(@ApiParam(value = "User ID") @PathVariable Long id) throws AccountNotFoundException {
        return accountDAO.getCurrentBalance(id);
    }

    @ApiOperation("Returns an account object by users id")
    @RequestMapping(path = "/users/{id}/account", method = RequestMethod.GET)
    public Account getAccountByUserID(@ApiParam(value = "User ID") @PathVariable int id) throws AccountNotFoundException {
        return accountDAO.getAccountByUserId(id);
    }

    @ApiOperation("Returns all transfers by user id")
    @RequestMapping(path = "/users/{id}/transfers", method = RequestMethod.GET)
    public List<Transfer> getAllTransfersByUserID(@ApiParam(value = "User ID") @PathVariable int id){
        return transferDAO.getAllTransfersByUserId(id);
    }

    @ApiOperation("Returns username by account id")
    @RequestMapping(path = "/accounts/{id}/user", method = RequestMethod.GET)
    public String getUsernameByAccountId(@ApiParam(value = "Account ID") @PathVariable int id){
        return userDAO.getUsernameByAccountId(id);
    }

    @ApiOperation("Creates an new transfer from passed in transfer object")
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/accounts/{id}/transfer", method = RequestMethod.POST)
    public void createTransfer(@ApiParam(value = "Transfer Object") @Valid @RequestBody Transfer transfer,
                               @ApiParam(value = "Account ID") @PathVariable int id) throws AccountNotFoundException, TransferNotFoundException{
        //Create transfer in database
        transferDAO.create(transfer, id);

        //Update balances for both accounts
        if (transferDAO.getTransferStatusFromStatusID(transfer.getStatusId()).equals("Approved")){
            updateBalancesFromTransfer(transfer);
        }
    }

    @ApiOperation("Returns transfer by transfer id")
    @RequestMapping(path = "/transfers/{id}", method = RequestMethod.GET)
    public Transfer getTransferByTransferID(@ApiParam(value = "Transfer ID") @PathVariable int id) throws TransferNotFoundException{
        return transferDAO.getTransferByTransferID(id);
    }

    @ApiOperation("Returns transfer type by transfer id")
    @RequestMapping(path = "/transfers/{id}/type", method = RequestMethod.GET)
    public String getTransferTypeByTransferID(@ApiParam(value = "Transfer ID") @PathVariable int id) throws TransferNotFoundException{
        return transferDAO.getTransferType(id);
    }

    @ApiOperation("Returns transfer status by transfer id")
    @RequestMapping(path = "/transfers/{id}/status", method = RequestMethod.GET)
    public String getTransferStatusByTransferID(@ApiParam(value = "Transfer ID") @PathVariable int id) throws TransferNotFoundException{
        return transferDAO.getTransferStatus(id);
    }

    private void updateBalancesFromTransfer(Transfer transfer) throws AccountNotFoundException{
        Long toAccountId = transfer.getToAccountId();
        Long fromAccountId = transfer.getFromAccountId();
        BigDecimal amount = transfer.getAmount();

        accountDAO.addToBalance(toAccountId, amount);
        accountDAO.subtractFromBalance(fromAccountId, amount);
    }
}

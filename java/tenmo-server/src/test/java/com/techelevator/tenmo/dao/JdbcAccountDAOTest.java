package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.dao.JdbcAccountDAO;
import com.techelevator.tenmo.dao.JdbcTransferDAO;
import com.techelevator.tenmo.dao.JdbcUserDAO;
import com.techelevator.tenmo.exception.AccountNotFoundException;
import com.techelevator.tenmo.model.Account;
import org.junit.jupiter.api.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.math.BigDecimal;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class JdbcAccountDAOTest {
    private static SingleConnectionDataSource dataSource;
    private static JdbcAccountDAO accountDAO;
    private static JdbcTransferDAO transferDAO;
    private static JdbcUserDAO userDAO;
    private static final int ACCOUNT_ID = 2200;
    private static final int USER_ID = 1100;
    private static final BigDecimal STARTING_BALANCE = new BigDecimal("1000.63");


    @BeforeAll
    public static void setupDataSource(){
        dataSource = new SingleConnectionDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/tenmo");
        dataSource.setUsername("postgres");
        dataSource.setPassword("postgres1");
        dataSource.setAutoCommit(false);

    }

    @AfterAll
    public static void destoryDataSource(){
        dataSource.destroy();
    }

    @BeforeEach
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        String sqlInsertUser = "INSERT INTO users(user_id, username, password_hash) VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlInsertUser, USER_ID, "TEST", "TEST");
        String sqlInsertAccount = "INSERT INTO accounts(account_id, user_id, balance) VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlInsertAccount, ACCOUNT_ID, USER_ID, STARTING_BALANCE);

        userDAO = new JdbcUserDAO(jdbcTemplate);
        transferDAO = new JdbcTransferDAO(jdbcTemplate);
        accountDAO = new JdbcAccountDAO(jdbcTemplate);
    }

    @AfterEach
    public void rollback() throws SQLException{
           dataSource.getConnection().rollback();
    }

    @Test
    void getCurrentBalanceTest_Send_In_ID_Return_Balance() throws AccountNotFoundException {
        BigDecimal actual = accountDAO.getCurrentBalance((long)USER_ID);
        System.out.println(actual);
        BigDecimal expected = STARTING_BALANCE;

        assertEquals(actual, expected);
    }

    @Test
    void getAccountByUserId() throws AccountNotFoundException {
        Account expected = getAccount(ACCOUNT_ID, USER_ID, STARTING_BALANCE);
        Account actual = accountDAO.getAccountByUserId(USER_ID);

        assertAccountsAreEqual(expected, actual);
    }

    @Test
    void addToBalance() throws AccountNotFoundException {
        BigDecimal balanceToAdd = new BigDecimal("150.00");
        //Add to Account
        accountDAO.addToBalance(ACCOUNT_ID, balanceToAdd);

        assertEquals(accountDAO.getCurrentBalance((long)USER_ID), STARTING_BALANCE.add(balanceToAdd));
    }

    @Test
    void subtractFromBalance() throws AccountNotFoundException {
        BigDecimal balanceToAdd = new BigDecimal("150.00");
        //Add to Account
        accountDAO.subtractFromBalance(ACCOUNT_ID, balanceToAdd);

        assertEquals(accountDAO.getCurrentBalance((long)USER_ID), STARTING_BALANCE.subtract(balanceToAdd));
    }

    private Account getAccount(long accountId, long userId, BigDecimal balance){
        Account thisAccount = new Account();

        thisAccount.setAccountId(accountId);
        thisAccount.setUserId(userId);
        thisAccount.setBalance(balance);

        return thisAccount;
    }

    private void assertAccountsAreEqual(Account expected, Account actual){
        assertEquals(expected.getAccountId(), actual.getAccountId());
        assertEquals(expected.getUserId(), actual.getUserId());
        assertEquals(expected.getBalance(), actual.getBalance());
    }
}
package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.TransferNotFoundException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.junit.jupiter.api.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JdbcTransferDAOTest {
    private static SingleConnectionDataSource dataSource;
    private static JdbcAccountDAO accountDAO;
    private static JdbcTransferDAO transferDAO;
    private static JdbcUserDAO userDAO;
    private static final int ACCOUNT_ID = 2200;
    private static final int ACCOUNT_TWO_ID = 2201;
    private static final int USER_ID = 1100;
    private static final int USER_TWO_ID = 1101;
    private static final int TRANSFER_ID = 3300;
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

        //Insert test users
        String sqlInsertUser = "INSERT INTO users(user_id, username, password_hash) VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlInsertUser, USER_ID, "TEST", "TEST");
        String sqlInsertNextUser = "INSERT INTO users(user_id, username, password_hash) VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlInsertNextUser, USER_TWO_ID, "TESTY", "TESTWORD");

        //Insert test accounts
        String sqlInsertAccount = "INSERT INTO accounts(account_id, user_id, balance) VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlInsertAccount, ACCOUNT_ID, USER_ID, STARTING_BALANCE);
        String sqlInsertNextAccount = "INSERT INTO accounts(account_id, user_id, balance) VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlInsertNextAccount, ACCOUNT_TWO_ID, USER_TWO_ID, STARTING_BALANCE);

        String sqlInsertTransfer = "INSERT INTO transfers (transfer_id, transfer_type_id, transfer_status_id, " +
                "account_from, account_to, amount) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlInsertTransfer, TRANSFER_ID, 2, 2, ACCOUNT_ID,
                ACCOUNT_TWO_ID, new BigDecimal("50.00"));


        userDAO = new JdbcUserDAO(jdbcTemplate);
        transferDAO = new JdbcTransferDAO(jdbcTemplate);
        accountDAO = new JdbcAccountDAO(jdbcTemplate);
    }

    @AfterEach
    public void rollback() throws SQLException {
        dataSource.getConnection().rollback();
    }


    @Test
    void getAllTransfersByUserIdTest_returns_correct_transfers() {
        List<Transfer> actual = transferDAO.getAllTransfersByUserId(USER_ID);

        assertEquals(actual.size(), 1);
    }

    @Test
    void getTransferByTransferIDTest_return_correct_transfer() throws TransferNotFoundException {
        Transfer expected = getTransfer(TRANSFER_ID, 2, 2, ACCOUNT_ID,
                ACCOUNT_TWO_ID, new BigDecimal("50.00"));
        Transfer actual = transferDAO.getTransferByTransferID(TRANSFER_ID);

        assertTransfersAreEqual(expected, actual);
    }

    @Test
    void getTransferStatusTest_return_approved_for_approved_transfer() throws TransferNotFoundException {
        String expected = "Approved";
        String actual = transferDAO.getTransferStatus(TRANSFER_ID);

        assertEquals(expected, actual);
    }

    @Test
    void getTransferTypeTest_return_approved_for_approved_transfer() throws TransferNotFoundException {
        String expected = "Send";
        String actual = transferDAO.getTransferType(TRANSFER_ID);

        assertEquals(expected, actual);
    }

    @Test
    void createTest_adds_a_new_transfer() {
        int startingSize = transferDAO.getAllTransfersByUserId(USER_ID).size();

        long testTransferId = 3301;
        Transfer expected = getTransfer(testTransferId, 2, 2, ACCOUNT_ID,
                ACCOUNT_TWO_ID, new BigDecimal("50.00"));

        transferDAO.create(expected, ACCOUNT_TWO_ID);
        int finalSize = transferDAO.getAllTransfersByUserId(USER_ID).size();

        assertEquals(startingSize, finalSize-1);
    }

    private Transfer getTransfer(long transferId, long typeId, long statusId, long fromAccountId,
                                 long toAccountId, BigDecimal amount){
        Transfer thisTransfer = new Transfer();

        thisTransfer.setTransferId(transferId);
        thisTransfer.setTypeId(typeId);
        thisTransfer.setStatusId(statusId);
        thisTransfer.setFromAccountId(fromAccountId);
        thisTransfer.setToAccountId(toAccountId);
        thisTransfer.setAmount(amount);

        return thisTransfer;
    }

    private void assertTransfersAreEqual(Transfer expected, Transfer actual){

        assertEquals(expected.getTransferId(), actual.getTransferId());
        assertEquals(expected.getTypeId(), actual.getTypeId());
        assertEquals(expected.getStatusId(), actual.getStatusId());
        assertEquals(expected.getFromAccountId(), actual.getFromAccountId());
        assertEquals(expected.getToAccountId(), actual.getToAccountId());
        assertEquals(expected.getAmount(), actual.getAmount());
    }
}
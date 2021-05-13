package com.techelevator.tenmo.models;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class TransferTest {

    private Transfer transfer = new Transfer();
    private final long TRANSFER_ID = 3001;
    private final long TYPE_ID = 2;
    private final long STATUS_ID = 2;
    private final long FROM_ID = 1001;
    private final long TO_ID = 1002;
    private final BigDecimal AMOUNT = new BigDecimal("150.00");

    @Before
    public void setUp(){

        transfer.setTransferId(TRANSFER_ID);
        transfer.setTypeId(TYPE_ID);
        transfer.setStatusId(STATUS_ID);
        transfer.setFromAccountId(FROM_ID);
        transfer.setToAccountId(TO_ID);
        transfer.setAmount(AMOUNT);

    }

    @Test
    public void getTransferIdTest_returns_transfer_id() {
        long actual = transfer.getTransferId();

        assertEquals(TRANSFER_ID, actual);
    }

    @Test
    public void getTypeIdTest_returns_type_id() {
        long actual = transfer.getTypeId();

        assertEquals(TYPE_ID, actual);
    }

    @Test
    public void getStatusIdTest_returns_status_id() {
        long actual = transfer.getStatusId();

        assertEquals(STATUS_ID, actual);
    }

    @Test
    public void getFromAccountIdTest_returns_from_id() {
        long actual = transfer.getFromAccountId();

        assertEquals(FROM_ID, actual);
    }

    @Test
    public void getToAccountIdTest_returns_to_id() {
        long actual = transfer.getToAccountId();

        assertEquals(TO_ID, actual);
    }

    @Test
    public void getAmountTest_returns_amount() {
        BigDecimal actual = transfer.getAmount();

        assertEquals(AMOUNT, actual);
    }

    @Test
    public void transferConstructorTest_four_parameters_builds_object() {
        Transfer transfer = new Transfer(TYPE_ID, FROM_ID, TO_ID, AMOUNT);

        assertNotNull(transfer);
    }

    @Test
    public void transferConstructorTest_five_parameters_builds_object() {
        Transfer transfer = new Transfer(TYPE_ID, STATUS_ID, FROM_ID, TO_ID, AMOUNT);

        assertNotNull(transfer);
    }
}
package com.techelevator.tenmo.models;

import io.cucumber.java.bs.A;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class AccountTest {

    private Account account = new Account();
    private final long ACCOUNT_ID = 2001;
    private final long USER_ID = 1001;
    private final BigDecimal BALANCE = new BigDecimal("1000.00");


    @Before
    public void setup(){

        account.setAccountId(ACCOUNT_ID);
        account.setUserId(USER_ID);
        account.setBalance(BALANCE);

    }

    @Test
    public void getAccountIdTest_returns_account_id() {
        long actual = account.getAccountId();

        assertEquals(ACCOUNT_ID, actual);
    }

    @Test
    public void getUserIdTest_returns_user_id() {
        long actual = account.getUserId();

        assertEquals(USER_ID, actual);
    }

    @Test
    public void getBalanceTest_returns_balance() {
       BigDecimal actual = account.getBalance();

       assertEquals(BALANCE, actual);
    }
}
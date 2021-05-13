package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.AccountNotFoundException;
import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;

public interface AccountDAO {

    BigDecimal getCurrentBalance(Long id) throws AccountNotFoundException;

    Account getAccountByUserId(int id) throws AccountNotFoundException;

    void addToBalance (long accountNumber, BigDecimal balance) throws AccountNotFoundException;

    void subtractFromBalance (long accountNumber, BigDecimal balance) throws AccountNotFoundException;
}

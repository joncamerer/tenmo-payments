package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.AccountNotFoundException;
import com.techelevator.tenmo.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component

public class JdbcAccountDAO implements AccountDAO{
    private JdbcTemplate jdbcTemplate;

    public JdbcAccountDAO (JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }



    @Override
    public BigDecimal getCurrentBalance(Long id) throws AccountNotFoundException {
       String sql = "SELECT balance FROM accounts WHERE user_id = ?";

       BigDecimal balance = jdbcTemplate.queryForObject(sql, BigDecimal.class, id);
       if (balance != null) {
           return balance;
       }
       throw new AccountNotFoundException();
    }

    @Override
    public Account getAccountByUserId(int id) throws AccountNotFoundException{
        String sqlGetAccountByUserID = "SELECT account_id, user_id, balance FROM accounts WHERE user_id = ?";
        Account newAccount = null;
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetAccountByUserID, id);
        if (results.next()) {
            newAccount = mapRowToAccount(results);
        }
        if (newAccount != null) {
            return newAccount;
        }
        throw new AccountNotFoundException();
    }


    @Override
    public void addToBalance (long accountNumber, BigDecimal balance) throws AccountNotFoundException{
        String sqlAddToBalance = "UPDATE accounts SET balance = ? WHERE account_id = ?";
        BigDecimal newBalance = getAccountBalance(accountNumber).add(balance);
        jdbcTemplate.update(sqlAddToBalance, newBalance, accountNumber);

    }

    @Override
    public void subtractFromBalance (long accountNumber, BigDecimal balance) throws AccountNotFoundException{
        String sqlAddToBalance = "UPDATE accounts SET balance = ? WHERE account_id = ?";
        BigDecimal newBalance = getAccountBalance(accountNumber).subtract(balance);
        jdbcTemplate.update(sqlAddToBalance, newBalance, accountNumber);
    }

    private BigDecimal getAccountBalance (long accountNumber) throws AccountNotFoundException{
        String sqlGetAccountBalance = "SELECT balance FROM accounts WHERE account_id = ?";
        BigDecimal balance = jdbcTemplate.queryForObject(sqlGetAccountBalance, BigDecimal.class, accountNumber);
        if (balance != null){
            return balance;
        }
        throw new AccountNotFoundException();
    }

    private Account mapRowToAccount(SqlRowSet results){
        Account thisAccount  = new Account();
        thisAccount.setAccountId(results.getLong("account_id"));
        thisAccount.setUserId(results.getLong("user_id"));
        thisAccount.setBalance(results.getBigDecimal("balance"));
        return thisAccount;
    }
}

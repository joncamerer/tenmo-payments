package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.AccountNotFoundException;
import com.techelevator.tenmo.exception.TransferNotFoundException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDAO implements TransferDAO {
    private JdbcTemplate jdbcTemplate;

    public JdbcTransferDAO(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Transfer> getAllTransfersByUserId(int id){
        String sqlGetAllTransfers = "SELECT transfer_id, transfer_type_id, transfer_status_id, " +
                "account_from, account_to, amount FROM transfers WHERE account_from = " +
                "(SELECT account_id FROM accounts WHERE user_id = ?) OR account_to = " +
                "(SELECT account_id FROM accounts WHERE user_id = ?)";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetAllTransfers, id, id);

        return getListFromResults(results);
    }

    @Override
    public Transfer getTransferByTransferID(int id) throws TransferNotFoundException{
        String sqlGetTransfer = "SELECT transfer_id, transfer_type_id, transfer_status_id, " +
                "account_from, account_to, amount FROM transfers WHERE transfer_id = ?";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetTransfer, id);
        if (results.next()) {
                return mapRowToTransfer(results);
        }
        throw new TransferNotFoundException();
    }

    @Override
    public String getTransferStatus(long transferId) throws TransferNotFoundException{
        String sqlGetTransferStatus = "SELECT transfer_status_desc " +
                "FROM transfer_statuses JOIN transfers ON " +
                "transfer_statuses.transfer_status_id = transfers.transfer_status_id " +
                "WHERE transfers.transfer_id = ?";

        String status = jdbcTemplate.queryForObject(sqlGetTransferStatus, String.class,
                Integer.parseInt(String.valueOf(transferId)));
        if (status != null) {
            return status;
        }
        throw new TransferNotFoundException();
    }

    @Override
    public String getTransferStatusFromStatusID(long statusId) throws TransferNotFoundException {
        String sqlGetStatus = "SELECT transfer_status_desc FROM transfer_statuses " +
                "WHERE transfer_status_id = ?";

        String status = jdbcTemplate.queryForObject(sqlGetStatus, String.class,
                statusId);
        if (status != null) {
            return status;
        }
        throw new TransferNotFoundException();
    }

    @Override
    public String getTransferType(long transferId) throws TransferNotFoundException{
        String sqlGetTransferType = "SELECT transfer_type_desc " +
                "FROM transfer_types JOIN transfers ON " +
                "transfer_types.transfer_type_id = transfers.transfer_type_id " +
                "WHERE transfers.transfer_id = ?";
        String type = jdbcTemplate.queryForObject(sqlGetTransferType, String.class,
                Integer.parseInt(String.valueOf(transferId)));
        if (type != null) {
            return type;
        }
        throw new TransferNotFoundException();
    }

    @Override
    public boolean create(Transfer transfer, int toUserId) {
        String sqlCreateTransfer = "INSERT INTO transfers (transfer_type_id, transfer_status_id, " +
                "account_from, account_to, amount) VALUES (?, ?, ?, ?, ?)";

        try {
            jdbcTemplate.update(sqlCreateTransfer, transfer.getTypeId(), transfer.getStatusId(),
                    transfer.getFromAccountId(), toUserId, transfer.getAmount());
            return true;
        } catch (RestClientResponseException e) {
            System.out.println(e.getRawStatusCode() + ": " + e.getMessage());
        } catch (ResourceAccessException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

    private Transfer mapRowToTransfer(SqlRowSet results) {
        Transfer thisTransfer = new Transfer();

        thisTransfer.setTransferId(results.getLong("transfer_id"));
        thisTransfer.setTypeId(results.getLong("transfer_type_id"));
        thisTransfer.setStatusId(results.getLong("transfer_status_id"));
        thisTransfer.setFromAccountId(results.getLong("account_from"));
        thisTransfer.setToAccountId(results.getLong("account_to"));
        thisTransfer.setAmount(results.getBigDecimal("amount"));

        return thisTransfer;
    }

    private List<Transfer> getListFromResults(SqlRowSet results) {
        List<Transfer> transfers = new ArrayList<>();

        while (results.next()) {
            Transfer thisTransfer = mapRowToTransfer(results);
            transfers.add(thisTransfer);
        }

        return transfers;
    }

}

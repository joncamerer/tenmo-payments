package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.AccountNotFoundException;
import com.techelevator.tenmo.exception.TransferNotFoundException;
import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDAO {

    boolean create(Transfer transfer, int toUserId);

    List<Transfer> getAllTransfersByUserId(int id);

    String getTransferStatus(long statusId) throws TransferNotFoundException;

    String getTransferStatusFromStatusID(long statusId) throws TransferNotFoundException;

    String getTransferType(long typeId) throws TransferNotFoundException;

    Transfer getTransferByTransferID(int id) throws TransferNotFoundException;

}

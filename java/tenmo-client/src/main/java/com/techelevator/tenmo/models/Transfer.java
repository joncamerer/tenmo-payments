package com.techelevator.tenmo.models;

import java.math.BigDecimal;

public class Transfer {
    Long transferId;
    Long typeId;
    Long statusId;
    Long fromAccountId;
    Long toAccountId;
    BigDecimal amount;

    public Transfer(){

    }

    public Transfer(Long typeId, Long fromAccountId, Long toAccountId,
                    BigDecimal amount) {
        this.typeId = typeId;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
    }

    //Constructor with status ID
    public Transfer(Long typeId, Long statusId, Long fromAccountId, Long toAccountId,
                    BigDecimal amount) {
        this.typeId = typeId;
        this.statusId = statusId;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
    }

    public Long getTransferId() {
        return transferId;
    }

    public void setTransferId(Long transferId) {
        this.transferId = transferId;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public Long getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(Long fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public Long getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(Long toAccountId) {
        this.toAccountId = toAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

}

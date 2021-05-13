package com.techelevator.tenmo.exception;

public class TransferNotFoundException extends Exception{

    private static final long serialVersionUID = 1L;

    public TransferNotFoundException () {super("\nTransfer Not Found\n");}
}

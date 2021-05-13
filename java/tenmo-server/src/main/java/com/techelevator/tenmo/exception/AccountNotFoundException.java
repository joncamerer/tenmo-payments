package com.techelevator.tenmo.exception;

public class AccountNotFoundException extends Exception{

    private static final long serialVersionUID = 1L;

    public AccountNotFoundException () {super("\nAccount Not Found\n");}

}

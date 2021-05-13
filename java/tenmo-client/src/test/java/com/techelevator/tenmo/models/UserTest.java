package com.techelevator.tenmo.models;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserTest {

    private User user = new User();
    private final int USER_ID = 1001;
    private final String USERNAME = "TESTY";

    @Before
    public void setUp(){

        user.setId(USER_ID);
        user.setUsername(USERNAME);

    }

    @Test
    public void getIdTest_returns_user_id() {
        int actual = user.getId();

        assertEquals(USER_ID, actual);
    }

    @Test
    public void getUsernameTest_returns_username() {
        String actual = user.getUsername();

        assertEquals(USERNAME, actual);
    }

    @Test
    public void toStringTest_builds_string() {
        String expected = USER_ID + ": " + USERNAME;
        String actual = user.toString();

        assertEquals(expected, actual);
    }
}
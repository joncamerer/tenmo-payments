package com.techelevator.tenmo.services;

import com.techelevator.tenmo.models.Account;
import com.techelevator.tenmo.models.AuthenticatedUser;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class AccountService {

    private String BASE_URL;
    private RestTemplate restTemplate = new RestTemplate();

    public AccountService(String url) {
        this.BASE_URL = url;
    }

    public BigDecimal getCurrentBalance(String token, long userId) {

        return restTemplate.exchange(BASE_URL + "/users/" + userId + "/balance",
                                     HttpMethod.GET, makeAuthEntity(token), BigDecimal.class).getBody();
    }

    public Account getAccountByUserId(String token, int id){

        return restTemplate.exchange(BASE_URL+ "/users/" + id + "/account", HttpMethod.GET,
                makeAuthEntity(token), Account.class).getBody();

    }

    private HttpEntity makeAuthEntity(String token){
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        return new HttpEntity(headers);
    }

}

package com.techelevator.tenmo.services;

import com.techelevator.tenmo.models.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class UserService {
    private String BASE_URL;
    private RestTemplate restTemplate = new RestTemplate();

    public UserService(String url) {
        this.BASE_URL = url;
    }

    public User[] listAllUsers(String token){

        return restTemplate.exchange(BASE_URL + "/users", HttpMethod.GET,
                makeAuthEntity(token), User[].class).getBody();
    }

    public String getUsernameByAccountId(String token, long id){
        return restTemplate.exchange(BASE_URL + "accounts/" + id + "/user",
                HttpMethod.GET, makeAuthEntity(token), String.class).getBody();
    }

    private HttpEntity makeAuthEntity(String token){
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        return new HttpEntity(headers);
    }

}

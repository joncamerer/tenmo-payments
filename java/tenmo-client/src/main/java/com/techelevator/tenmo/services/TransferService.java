package com.techelevator.tenmo.services;

import com.techelevator.tenmo.models.Transfer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class TransferService {
    private String BASE_URL;
    private RestTemplate restTemplate = new RestTemplate();

    public TransferService(String url) {
        this.BASE_URL = url;
    }

    public Transfer[] getAllTransfersByUserID(String token, long userId){

        return restTemplate.exchange(BASE_URL + "/users/" + userId + "/transfers",
                HttpMethod.GET, makeAuthEntity(token), Transfer[].class).getBody();
    }

    public Transfer getTransferByTransferID(String token, int transferId){
        return restTemplate.exchange(BASE_URL + "/transfers/" + transferId, HttpMethod.GET, makeAuthEntity(token), Transfer.class).getBody();
    }

    public String getTransferType(String token, Long transferId){
        return restTemplate.exchange(BASE_URL + "/transfers/" + transferId + "/type", HttpMethod.GET, makeAuthEntity(token), String.class).getBody();
    }

    public String getTransferStatus(String token, Long transferId){
        return restTemplate.exchange(BASE_URL + "/transfers/" + transferId + "/status", HttpMethod.GET, makeAuthEntity(token), String.class).getBody();
    }


    public Transfer createSendingTransfer(String token, Long fromUser, Long toUser, BigDecimal amount) {
        Transfer thisTransfer = makeTransfer(2L, 2L, fromUser, toUser, amount);

        try {
            thisTransfer = restTemplate.postForObject(BASE_URL + "/accounts/" + toUser + "/transfer",
                    makeTransferHttpEntity(token, thisTransfer), Transfer.class);
        } catch (RestClientResponseException e) {
            System.out.println(e.getRawStatusCode() + ": " + e.getMessage());
        } catch (ResourceAccessException e) {
            System.out.println(e.getMessage());
        }

        return thisTransfer;
    }

    private Transfer makeTransfer(Long typeId, Long statusId, Long fromUser, Long toUser, BigDecimal amount) {
        Transfer thisTransfer = new Transfer();

        thisTransfer.setTypeId(typeId);
        thisTransfer.setStatusId(statusId);
        thisTransfer.setFromAccountId(fromUser);
        thisTransfer.setToAccountId(toUser);
        thisTransfer.setAmount(amount);

        return thisTransfer;
    }

    private HttpEntity<Transfer> makeTransferHttpEntity(String token, Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new HttpEntity<>(transfer, headers);
    }

    private HttpEntity makeAuthEntity(String token){
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        return new HttpEntity(headers);
    }

}

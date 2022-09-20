package com.twinkles.email;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.twinkles.dtos.MailRequest;
import com.twinkles.dtos.MailResponse;

import java.util.concurrent.CompletableFuture;

public interface EmailService {
    CompletableFuture<MailResponse> sendSimpleMail(MailRequest mailRequest) throws UnirestException;
}

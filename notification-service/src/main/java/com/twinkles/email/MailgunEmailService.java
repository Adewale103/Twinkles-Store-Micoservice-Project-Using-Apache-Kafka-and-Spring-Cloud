package com.twinkles.email;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.twinkles.dtos.MailRequest;
import com.twinkles.dtos.MailResponse;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service("mailgun_sender")
@NoArgsConstructor
public class MailgunEmailService implements EmailService {

    private final String DOMAIN = System.getenv("DOMAIN");
    private final String PRIVATE_KEY = System.getenv("MAILGUN_PRIMARY_KEY");



    @Override
    public CompletableFuture<MailResponse> sendSimpleMail(MailRequest mailRequest) throws UnirestException {
        log.info("DOMAIN -> {}", DOMAIN);
        log.info("API KEY -> {}", PRIVATE_KEY);
        log.info(mailRequest.getBody());
        HttpResponse<String> request = Unirest.post("https://api.mailgun.net/v3/" + DOMAIN + "/messages")
                .basicAuth("api", PRIVATE_KEY)
                .queryString("from", mailRequest.getSender())
                .queryString("to", mailRequest.getReceiver())
                .queryString("subject", mailRequest.getSubject())
                .queryString("text", mailRequest.getBody())
                .asString();
        MailResponse mailResponse = request.getStatus() == 200 ? new MailResponse(true) : new MailResponse(false);

        return CompletableFuture.completedFuture(mailResponse);
    }
}

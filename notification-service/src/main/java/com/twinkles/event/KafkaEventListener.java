package com.twinkles.event;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.twinkles.dtos.MailRequest;
import com.twinkles.email.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaEventListener {

    private final EmailService emailService;

    public KafkaEventListener( @Qualifier("mailgun_sender") EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "notificationTopic")
    private void handleNotification(OrderPlacedEvent orderPlacedEvent) throws UnirestException {
        MailRequest mailRequest = MailRequest.builder()
                .body("Hello, Your order has successfully been made. Your order number is "+orderPlacedEvent.getOrderNumber())
                .sender("letsBuy@gmail.com")
                .receiver(orderPlacedEvent.getEmailAddress())
                .subject("Order placed at letsBuy")
                .build();
        emailService.sendSimpleMail(mailRequest);
        log.info("Notification sent for {}",orderPlacedEvent.getOrderNumber());
    }
}

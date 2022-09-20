package com.twinkles.dtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MailRequest {
    private String sender;
    private String receiver;
    private String body;
    private String subject;
}

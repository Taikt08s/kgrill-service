package com.swd392.group2.kgrill_service.service;

import com.swd392.group2.kgrill_model.enums.EmailTemplateName;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;

public interface EmailService {
     void sendEmail(
            String to,
            String username,
            EmailTemplateName emailTemplateName,
            String confirmationUrl,
            String activationCode,
            String subject
    ) throws MessagingException, UnsupportedEncodingException;
}

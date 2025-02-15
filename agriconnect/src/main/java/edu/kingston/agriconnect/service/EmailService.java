package edu.kingston.agriconnect.service;

import edu.kingston.agriconnect.model.enums.EmailTemplateName;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;

//@Service
@RequiredArgsConstructor
public class EmailService {

//    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final String subject = "Welcome to Agriconnect!";

    public void sendEmail(
        String to,
        String username,
        EmailTemplateName templateName,
        String subject,
        String confirmationUrl,
        String ActivationCode
        ) {

    }
}

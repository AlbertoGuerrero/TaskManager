package com.microservice.notification.service.service;

import com.microservice.commons.kafka.Notification;
import jakarta.mail.MessagingException;

public interface NotificationService {
    void sendMail(Notification notification) throws MessagingException;
}

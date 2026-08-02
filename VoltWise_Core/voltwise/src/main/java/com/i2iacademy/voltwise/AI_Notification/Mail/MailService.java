package com.i2iacademy.voltwise.AI_Notification.Mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Senkron gönderim yapıyoruz — bu YANLIŞ DEĞİL çünkü bu metod zaten
     * aiAdvisoryExecutor thread pool'unda çalışıyor olacak (Kafka consumer
     * thread'inde DEĞİL). Kritik yoldan zaten ayrılmış durumdayız.
     * Yine de bir mail gönderimi başarısız olursa AI tavsiyesi üretme akışını
     * çökertmemeli — o yüzden burada da try-catch ile yutup logluyoruz.
     */
    public boolean sendAdvisoryEmail(String toAddress, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toAddress);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            log.error("Mail gönderimi başarısız oldu, hedef: {}", toAddress, e);
            return false;
        }
    }

}

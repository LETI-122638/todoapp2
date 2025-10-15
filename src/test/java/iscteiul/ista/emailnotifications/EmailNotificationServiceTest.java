package iscteiul.ista.emailnotifications;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class EmailNotificationServiceTest {
    @Test
    void testSendSimpleEmail() {
        JavaMailSender mailSender = Mockito.mock(JavaMailSender.class);
        EmailNotificationService service = new EmailNotificationService(mailSender);
        service.sendSimpleEmail("test@example.com", "Subject", "Body");
        verify(mailSender, times(1)).send(Mockito.any(SimpleMailMessage.class));
    }
}


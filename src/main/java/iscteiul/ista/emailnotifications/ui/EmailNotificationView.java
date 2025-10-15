package iscteiul.ista.emailnotifications.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import iscteiul.ista.base.ui.MainLayout;
import iscteiul.ista.emailnotifications.EmailNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@PageTitle("Enviar Email")
@Route(value = "enviar-email", layout = MainLayout.class)
@Component
public class EmailNotificationView extends VerticalLayout {
    public EmailNotificationView(@Autowired EmailNotificationService emailService) {
        TextField toField = new TextField("Destinatário (email)");
        TextField subjectField = new TextField("Assunto");
        TextArea messageField = new TextArea("Mensagem");
        Button sendButton = new Button("Enviar", event -> {
            try {
                emailService.sendSimpleEmail(
                    toField.getValue(),
                    subjectField.getValue(),
                    messageField.getValue()
                );
                Notification.show("Email enviado com sucesso!");
            } catch (Exception e) {
                Notification.show("Erro ao enviar email: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
            }
        });
        add(toField, subjectField, messageField, sendButton);
        setMaxWidth("400px");
        setAlignItems(Alignment.STRETCH);
    }
}


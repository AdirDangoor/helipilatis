package com.helipilatis.helipilatis.client;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Route("instructor-message")
public class InstructorMessageView extends BaseView {

    private final RestTemplate restTemplate;

    @Autowired
    public InstructorMessageView(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        initializeView();
    }

    private void initializeView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        TextArea messageArea = new TextArea("Message");
        messageArea.setWidthFull();
        messageArea.setHeight("300px");

        Button sendButton = new Button("Send Message", event -> sendMessageToAllUsers(messageArea.getValue()));
        sendButton.getStyle()
                .set("background-color", "#007BFF")
                .set("color", "white");

        VerticalLayout layout = new VerticalLayout(messageArea, sendButton);
        layout.setWidth("50%");
        layout.setAlignItems(Alignment.CENTER);
        add(layout);
    }

    private void sendMessageToAllUsers(String message) {
        try {
            String url = "http://localhost:8080/api/instructor/send-message";
            ResponseEntity<String> response = restTemplate.postForEntity(url, message, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                Notification.show("Message sent successfully", 3000, Notification.Position.MIDDLE);
            } else {
                Notification.show("Error sending message", 3000, Notification.Position.MIDDLE);
            }
        } catch (Exception ex) {
            logger.severe("Error sending message: " + ex.getMessage());
            Notification.show("Error sending message", 3000, Notification.Position.MIDDLE);
        }
    }
}
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

    @Autowired
    public InstructorMessageView(RestTemplate restTemplate) {
        super();
        try {
            this.restTemplate = restTemplate;
            initializeView();
        } catch (Exception ex) {
            logger.severe("Error initializing InstructorMessageView: " + ex.getMessage());
            Notification.show("Error initializing view", 3000, Notification.Position.MIDDLE);
        }
    }

    private void initializeView() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            Notification.show("instructor not logged in", 3000, Notification.Position.MIDDLE);
            getUI().ifPresent(ui -> ui.navigate("")); // Redirect to login page
            return; // Stop further processing
        }
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        TextArea messageArea = new TextArea("Please type your message here:");
        messageArea.setWidthFull();
        messageArea.setHeight("300px");
        messageArea.getStyle()
                .set("background-color", "rgba(255, 255, 255, 0.8)") // White transparent background
                .set("border-radius", "8px") // Optional: rounded corners
                .set("padding", "10px"); // Optional: padding inside the textarea

        add(messageArea);

        Button sendButton = new Button("Send Message", event -> {
            String message = messageArea.getValue();
            if (message.length() > 25) {
                Notification.show("Message cannot exceed 25 characters", 3000, Notification.Position.MIDDLE);
            } else {
                sendMessageToAllUsers(message);
            }
        });
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
            ResponseEntity<String> response = apiRequests.sendMessageToAllUsers(message);

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
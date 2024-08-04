package com.helipilatis.helipilatis.client;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.helipilatis.helipilatis.databaseModels.User;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Route("inbox")
public class InboxView extends BaseView {

    private static final Logger logger = Logger.getLogger("InboxView");

    public InboxView(RestTemplate restTemplate) {
        super();
        try {
            this.restTemplate = restTemplate;
            initializeView();
        } catch (Exception e) {
            logger.severe("Error initializing InboxView: " + e.getMessage());
            Notification.show("Error initializing InboxView", 3000, Notification.Position.MIDDLE);
        }
    }

    private void initializeView() {
        setSizeFull();
        setAlignItems(Alignment.STRETCH);
        setJustifyContentMode(JustifyContentMode.START);

        Long userId = getCurrentUserId();
        if (userId != null) {
            List<String> inboxMessages = fetchInboxMessages(userId);
            displayInboxMessages(inboxMessages);
        } else {
            Notification.show("User not logged in", 3000, Notification.Position.MIDDLE);
        }
    }

    private List<String> fetchInboxMessages(Long userId) {
        String url = "http://localhost:8080/api/mailbox/inbox/" + URLEncoder.encode(userId.toString(), StandardCharsets.UTF_8);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            String inboxMessages = response.getBody();
            logger.info("Inbox messages: " + inboxMessages);
            return inboxMessages != null ? Arrays.asList(inboxMessages.split("\\R")) : List.of();
        } else {
            logger.severe("Error fetching inbox messages: " + response.getStatusCode());
            Notification.show("Error fetching inbox messages", 3000, Notification.Position.MIDDLE);
            return List.of();
        }
    }

    private void displayInboxMessages(List<String> inboxMessages) {
        Collections.reverse(inboxMessages);
        for (String message : inboxMessages) {
            Span messageSpan = new Span(message);
            messageSpan.getStyle()
                    .set("background-color", "#f0f0f0")
                    .set("padding", "10px")
                    .set("margin", "5px 0")
                    .set("border-radius", "5px");
            add(messageSpan);
        }
    }
}
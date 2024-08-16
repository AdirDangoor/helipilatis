package com.helipilatis.helipilatis.client;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

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
        try {
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
        } catch (Exception e) {
            logger.severe("Error in initializeView: " + e.getMessage());
            Notification.show("Error initializing view", 3000, Notification.Position.MIDDLE);
        }
    }

    private List<String> fetchInboxMessages(Long userId) {
        try {
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
        } catch (Exception e) {
            logger.severe("Error in fetchInboxMessages: " + e.getMessage());
            Notification.show("Error fetching inbox messages", 3000, Notification.Position.MIDDLE);
            return List.of();
        }
    }

    private void displayInboxMessages(List<String> inboxMessages) {
        try {
            Collections.reverse(inboxMessages);

            Div inboxContainer = createInboxContainer();
            H1 title = createTitle();
            VerticalLayout messageLayout = createMessageLayout();

            addMessagesToLayout(inboxMessages, messageLayout);

            inboxContainer.add(title, messageLayout);
            add(inboxContainer);
        } catch (Exception e) {
            logger.severe("Error in displayInboxMessages: " + e.getMessage());
            Notification.show("Error displaying inbox messages", 3000, Notification.Position.MIDDLE);
        }
    }

    private Div createInboxContainer() {
        try {
            Div inboxContainer = new Div();
            inboxContainer.getStyle()
                    .set("width", "80%")
                    .set("max-width", "600px")
                    .set("margin", "0 auto") // Center the container horizontally
                    .set("padding", "20px")
                    .set("border-radius", "8px")
                    .set("background-color", "rgba(255, 255, 255, 0.8)")
                    .set("text-align", "center");
            return inboxContainer;
        } catch (Exception e) {
            logger.severe("Error in createInboxContainer: " + e.getMessage());
            Notification.show("Error creating inbox container", 3000, Notification.Position.MIDDLE);
            return new Div();
        }
    }

    private H1 createTitle() {
        try {
            H1 title = new H1("Inbox");
            title.getStyle()
                    .set("color", "#003366")
                    .set("font-size", "2em")
                    .set("text-align", "center")
                    .set("margin-bottom", "20px");
            return title;
        } catch (Exception e) {
            logger.severe("Error in createTitle: " + e.getMessage());
            Notification.show("Error creating title", 3000, Notification.Position.MIDDLE);
            return new H1();
        }
    }

    private VerticalLayout createMessageLayout() {
        try {
            VerticalLayout messageLayout = new VerticalLayout();
            messageLayout.getStyle()
                    .set("align-items", "center"); // Center items in the layout
            return messageLayout;
        } catch (Exception e) {
            logger.severe("Error in createMessageLayout: " + e.getMessage());
            Notification.show("Error creating message layout", 3000, Notification.Position.MIDDLE);
            return new VerticalLayout();
        }
    }

    private void addMessagesToLayout(List<String> inboxMessages, VerticalLayout messageLayout) {
        try {
            for (String message : inboxMessages) {
                Span messageSpan = new Span(message);
                messageSpan.getStyle()
                        .set("background-color", "#3A8A8A") // Transparent blue background
                        .set("padding", "10px")
                        .set("color", "white")
                        .set("margin", "5px 0")
                        .set("border-radius", "15px") // Rounded corners for chat bubble effect
                        .set("max-width", "100%") // Fit the width to the container
                        .set("display", "block"); // Ensure it occupies the full width

                messageLayout.add(messageSpan);
            }
        } catch (Exception e) {
            logger.severe("Error in addMessagesToLayout: " + e.getMessage());
            Notification.show("Error adding messages to layout", 3000, Notification.Position.MIDDLE);
        }
    }
}
package com.helipilatis.helipilatis.client;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.dom.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.function.Consumer;
import java.util.logging.Logger;

@Route("shop")
public class ShopView extends BaseView {

        @Autowired
        private RestTemplate restTemplate;

        public ShopView() {
                // Set up the main layout
                setSizeFull();
                setAlignItems(Alignment.CENTER);
                setJustifyContentMode(JustifyContentMode.CENTER);

                // Set background image
                String imagePath = "images/shop_background.jpg"; // Replace with your image path
                getElement().getStyle()
                        .set("background-image", "url('" + imagePath + "')")
                        .set("background-size", "cover")
                        .set("background-position", "center");

                // Add global styles
                Element styles = new Element("style");
                styles.setText(
                        ".content-container {" +
                                "    background-color: rgba(255, 255, 255, 0.8);" +
                                "    border-radius: 10px;" +
                                "    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);" +
                                "    padding: 2em;" +
                                "    max-width: 600px;" +
                                "    width: 100%;" +
                                "    display: flex;" +
                                "    flex-direction: column;" +
                                "    align-items: center;" +
                                "}" +
                                ".shop-title {" +
                                "    color: #003366;" +
                                "    margin-bottom: 1em;" +
                                "    text-align: center;" +
                                "}" +
                                ".shop-button {" +
                                "    margin: 0.2em;" +
                                "    width: 220px;" +
                                "    height: 60px;" +
                                "    background-color: #4A9B9B;" +
                                "    color: white;" +
                                "    border-radius: 0;" +
                                "    transition: background-color 0.3s ease;" +
                                "    display: flex;" +
                                "    justify-content: center;" +
                                "    align-items: center;" +
                                "    text-align: center;" +
                                "    padding: 0;" +
                                "    font-size: 14px;" +
                                "}" +
                                ".shop-button:hover {" +
                                "    background-color: #3A8A8A;" +
                                "}"
                );
                getElement().appendChild(styles);

                // Create a container for content
                Div contentContainer = new Div();
                contentContainer.addClassName("content-container");

                // Title
                H1 title = new H1("Welcome to Tickets Shop");
                title.addClassName("shop-title");

                // Buttons for navigation
                Button ticket1 = new Button("Buy 1 Ticket (80₪)", event -> purchaseTicket(1));

                // Add components to the container
                contentContainer.add(title, ticket1);

                // Add the container to the main layout
                add(contentContainer);
        }


        private void purchaseTicket(int numberOfTickets) {
                getCurrentUserId(userId -> {
                        if (userId == null) {
                                Notification.show("User not logged in", 3000, Notification.Position.MIDDLE);
                                return;
                        }

                        String url = "http://localhost:8080/api/shop/purchaseTicket?userId=" + userId + "&ticket=" + numberOfTickets;
                        ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);

                        if (response.getStatusCode() == HttpStatus.OK) {
                                Notification.show("Purchased " + numberOfTickets + " ticket(s) successfully");
                        } else {
                                Notification.show("Purchase failed", 3000, Notification.Position.MIDDLE);
                        }
                });
        }

        private void getCurrentUserId(Consumer<Long> callback) {
                VaadinSession session = VaadinSession.getCurrent();
                if (session != null) {
                        Long userId = (Long) session.getAttribute("userId");
                        logger.info("userId: " + userId);
                        callback.accept(userId);
                } else {
                        logger.severe("VaadinSession is null");
                        callback.accept(null);
                }
        }
}
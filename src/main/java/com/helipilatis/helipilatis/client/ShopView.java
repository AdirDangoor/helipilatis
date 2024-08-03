package com.helipilatis.helipilatis.client;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.helipilatis.helipilatis.databaseModels.TicketType;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.dom.Element;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

@Route("shop")
public class ShopView extends BaseView {

        public ShopView(RestTemplate restTemplate) {
                super();
                this.restTemplate = restTemplate;
                initializeView();
        }

        public void initializeView() {
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

                // Add components to the container
                contentContainer.add(title);

                // Fetch and display ticket types
                List<TicketType> ticketTypes = fetchTicketTypes();
                for (TicketType ticketType : ticketTypes) {
                        Button ticketButton = new Button("Buy " + ticketType.getNumberOfTickets() + " (" + ticketType.getPrice() + "₪)", event -> purchaseTicket(ticketType.getId()));
                        ticketButton.getElement().getClassList().add("shop-button");
                        contentContainer.add(ticketButton);
                }

                // Add the container to the main layout
                add(contentContainer);
        }

        private List<TicketType> fetchTicketTypes() {
                try {
                        String url = "http://localhost:8080/api/shop/ticketTypes"; // Replace with your actual API endpoint

                        ResponseEntity<TicketType[]> response = restTemplate.getForEntity(url, TicketType[].class);
                        if (response.getStatusCode() == HttpStatus.OK) {
                                List<TicketType> ticketTypes = Arrays.asList(response.getBody());
                                // Print response
                                logger.info("fetchTicketTypes API response : " + ticketTypes);
                                return ticketTypes;
                        } else {
                                Notification.show("Failed to fetch ticket types", 3000, Notification.Position.MIDDLE);
                                return List.of();
                        }
                } catch (Exception e) {
                        logger.severe("Failed to fetch ticket types: " + e.getMessage());
                        return List.of();
                }
        }

        private void purchaseTicket(Long ticketTypeId) {
                getCurrentUserId(userId -> {
                        if (userId == null) {
                                Notification.show("User not logged in", 3000, Notification.Position.MIDDLE);
                                return;
                        }

                        ResponseEntity<String> response = apiRequests.purchaseTicket(userId, ticketTypeId);
                        
                        if (response.getStatusCode() == HttpStatus.OK) {
                                Notification.show("Purchased ticket successfully");
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
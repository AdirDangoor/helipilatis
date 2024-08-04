package com.helipilatis.helipilatis.client;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
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

        private Span ticketCountSpan;

        public ShopView(RestTemplate restTemplate) {
                super();
                try {
                        this.restTemplate = restTemplate;
                        initializeView();
                } catch (Exception ex) {
                        logger.severe("Error initializing ShopView: " + ex.getMessage());
                        Notification.show("Error initializing view", 3000, Notification.Position.MIDDLE);
                }
        }

        public void initializeView() {
                Long userId = getCurrentUserId();
                if (userId == null) {
                        Notification.show("User not logged in", 3000, Notification.Position.MIDDLE);
                        getUI().ifPresent(ui -> ui.navigate("")); // Redirect to login page
                        return; // Stop further processing
                }
                setupLayout();
                addGlobalStyles();
                Div contentContainer = createContentContainer();
                addTitle(contentContainer);
                addTicketCountSpan(contentContainer);
                addTicketButtons(contentContainer);
                add(contentContainer);
        }

        private void setupLayout() {
                setSizeFull();
                setAlignItems(Alignment.CENTER);
                setJustifyContentMode(JustifyContentMode.CENTER);
                String imagePath = "images/shop_background.jpg"; // Replace with your image path
                getElement().getStyle()
                        .set("background-image", "url('" + imagePath + "')")
                        .set("background-size", "cover")
                        .set("background-position", "center");
        }

        private void addGlobalStyles() {
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
        }

        private Div createContentContainer() {
                Div contentContainer = new Div();
                contentContainer.addClassName("content-container");
                return contentContainer;
        }

        private void addTitle(Div contentContainer) {
                H1 title = new H1("Welcome to Tickets Shop");
                title.addClassName("shop-title");
                contentContainer.add(title);
        }

        private void addTicketCountSpan(Div contentContainer) {
                ticketCountSpan = createTicketCountSpan();
                contentContainer.add(ticketCountSpan);
        }

        private void addTicketButtons(Div contentContainer) {
                List<TicketType> ticketTypes = fetchTicketTypes();
                for (TicketType ticketType : ticketTypes) {
                        Button ticketButton = new Button("Buy " + ticketType.getNumberOfTickets() + " (" + ticketType.getPrice() + "₪)", event -> purchaseTicket(ticketType.getId()));
                        ticketButton.getElement().getClassList().add("shop-button");
                        contentContainer.add(ticketButton);
                }
        }

        private Span createTicketCountSpan() {
                Long userId = getCurrentUserId();
                int ticketCount = 0;
                if (userId != null) {
                        try {
                                String url = "http://localhost:8080/api/user/tickets/" + userId;
                                ResponseEntity<Integer> response = restTemplate.getForEntity(url, Integer.class);
                                if (response.getStatusCode().is2xxSuccessful()) {
                                        ticketCount = response.getBody();
                                } else {
                                        Notification.show("Error fetching ticket count", 3000, Notification.Position.MIDDLE);
                                }
                        } catch (Exception ex) {
                                logger.severe("Error fetching ticket count: " + ex.getMessage());
                                Notification.show("Error fetching ticket count", 3000, Notification.Position.MIDDLE);
                        }
                }

                Span ticketCountSpan = new Span("Tickets: " + ticketCount);
                ticketCountSpan.getStyle()
                        .set("color", "black")
                        .set("font-weight", "bold");
                return ticketCountSpan;
        }

        private void refreshTicketCountSpan() {
                Span newTicketCountSpan = createTicketCountSpan();
                ticketCountSpan.setText(newTicketCountSpan.getText());
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
                Long userId = getCurrentUserId();
                if (userId == null) {
                        Notification.show("User not logged in", 3000, Notification.Position.MIDDLE);
                        return;
                }

                ResponseEntity<String> response = apiRequests.purchaseTicket(userId, ticketTypeId);

                if (response.getStatusCode() == HttpStatus.OK) {
                        Notification.show("Purchased ticket successfully");
                        refreshTicketCountSpan(); // Refresh the ticket count span
                } else {
                        Notification.show("Purchase failed", 3000, Notification.Position.MIDDLE);
                }
        }

}
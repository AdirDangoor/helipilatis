package com.helipilatis.helipilatis.client;

import com.helipilatis.helipilatis.server.requests.LoginRequest;
import com.helipilatis.helipilatis.server.requests.Ticket1Request;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.dom.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Route("shop")
public class ShopView extends VerticalLayout {

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
                Button ticket1 = new Button("Buy 1 Ticket (80₪)", event -> {
                        Ticket1Request ticket1Request = new Ticket1Request();
                        String url = "http://localhost:8080/api/shop";
                        ResponseEntity<String> response = restTemplate.postForEntity(url, ticket1Request, String.class);

                        if (response.getStatusCode() == HttpStatus.OK) {
                                Notification.show("Purchased 1 ticket successfully");
                                //getUI().ifPresent(ui -> ui.navigate("user"));
                        } else {
                                Notification.show("Purchase failed", 3000, Notification.Position.MIDDLE);
                        }
                });


                        //createStyledButton("Buy 1 Ticket (80₪)", 1);
//                Button ticket10 = createStyledButton("Buy 10 Tickets (700₪)", 10);
//                Button ticket20 = createStyledButton("Buy 20 Tickets (1300₪)", 20);
//                Button ticket30 = createStyledButton("Buy 30 Tickets (2000₪)", 30);
                //Button ticketPrivate = createStyledButton("Buy 10 Tickets for private session (1300₪)", 10);
                //Button ticketInstructors15 = createStyledButton("Buy 15 Tickets for instructors (1300₪)", 15);
                //Button ticketInstructors30 = createStyledButton("Buy 15 Tickets for instructors (1300₪)", 15);

                // Add components to the container
                contentContainer.add(title, ticket1); //, ticket10, ticket20, ticket30);

                // Add the container to the main layout
                add(contentContainer);


//                  ticket1.addClickListener(event -> {
//                          Ticket1Request ticket1Request = new Ticket1Request();
//                          String url = "http://localhost:8080/api/shop";
//                          ResponseEntity<String> response = restTemplate.postForEntity(url, ticket1Request, String.class);
//
//                          if (response.getStatusCode() == HttpStatus.OK) {
//                                  Notification.show("User Got 1 ticket successfully");
//                                  //getUI().ifPresent(ui -> ui.navigate("user"));
//                          }
//                          else {
//                                  Notification.show("Purchase failed", 3000, Notification.Position.MIDDLE);
//                          }
//                  });

        }


//        private Button createStyledButton(String text, int numberOfTickets) {
//                Object ShopService;
//                Button button = new Button(text, event -> ShopService.clientBoughtTicket(numberOfTickets));
//
//                button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//                button.addClassName("shop-button");
//                return button;
//        }
}

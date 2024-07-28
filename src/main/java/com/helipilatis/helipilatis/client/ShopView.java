package com.helipilatis.helipilatis.client;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.dom.Element;

@Route("shop")
public class ShopView extends VerticalLayout {

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
                                "    border-radius: 15px;" +
                                "    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);" +
                                "    padding: 2em;" +
                                "    max-width: 600px;" +
                                "    width: 100%;" +
                                "}" +
                                ".shop-title {" +
                                "    color: #003366;" + // Changed to red
                                "    margin-bottom: 1em;" +
                                "    text-align: center;" +
                                "}" +
                                ".shop-button {" +
                                "    margin: 0.5em 0;" +
                                "    width: 100%;" +
                                "    background-color: #4A9B9B;" +
                                "    color: white;" +
                                "    border-radius: 8px;" +
                                "    transition: background-color 0.3s ease;" +
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
                Button ticket1 = createStyledButton("Purchase 1 Ticket (80₪)", "login");
                Button ticket10 = createStyledButton("Purchase 10 Tickets (700₪)", "register");
                Button ticket20 = createStyledButton("Purchase 20 Tickets (1300₪)", "login");
                Button ticket30 = createStyledButton("Purchase 30 Tickets (2000₪)", "login");

                // Add components to the container
                contentContainer.add(title, ticket1, ticket10, ticket20, ticket30);

                // Add the container to the main layout
                add(contentContainer);
        }

        private Button createStyledButton(String text, String route) {
                Button button = new Button(text, event -> getUI().ifPresent(ui -> ui.navigate(route)));
                button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                button.addClassName("shop-button");
                return button;
        }
}
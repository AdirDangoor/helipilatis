package com.helipilatis.helipilatis.client;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.Route;


@Route("")
public class HomeView extends VerticalLayout {

    public HomeView() {
        // Center the content
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        // Title
        add(new H1(""));

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
        H1 title = new H1("Welcome to Heli Pilatis!");
        title.addClassName("shop-title");

        // Buttons for navigation
        Button loginButton = new Button("Login", event -> getUI().ifPresent(ui -> ui.navigate("login")));
        Button registerButton = new Button("Register", event -> getUI().ifPresent(ui -> ui.navigate("register")));

        add(loginButton, registerButton);

        contentContainer.add(title, loginButton, registerButton);

        // Add the container to the main layout
        add(contentContainer);
    }
}

package com.helipilatis.helipilatis.client;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.notification.Notification;

@Route("")
public class HomeView extends BaseView {

    public HomeView() {
        super();
        try {
            initializeLayout();
            addStyles();
            createContentContainer();
        } catch (Exception ex) {
            logger.severe("Error initializing HomeView: " + ex.getMessage());
            Notification.show("Error initializing view", 3000, Notification.Position.MIDDLE);
        }
    }

    private void initializeLayout() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
    }

    private void addStyles() {
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

    private void createContentContainer() {
        Div contentContainer = new Div();
        contentContainer.addClassName("content-container");

        H1 title = new H1("Welcome to Heli Pilates!");
        title.addClassName("shop-title");

        Button loginButton = new Button("Login", event -> getUI().ifPresent(ui -> ui.navigate("login")));
        Button registerButton = new Button("Register", event -> getUI().ifPresent(ui -> ui.navigate("register")));

        contentContainer.add(title, loginButton, registerButton);
        add(contentContainer);
    }
}
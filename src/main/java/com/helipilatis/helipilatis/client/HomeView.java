package com.helipilatis.helipilatis.client;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("")
public class HomeView extends VerticalLayout {

    public HomeView() {
        // Title
        add(new H1("Welcome to your new application"));

        // Buttons for navigation
        Button loginButton = new Button("Login", event -> getUI().ifPresent(ui -> ui.navigate("login")));
        Button registerButton = new Button("Register", event -> getUI().ifPresent(ui -> ui.navigate("register")));

        add(loginButton, registerButton);
    }
}
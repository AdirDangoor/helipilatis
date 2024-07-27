package com.helipilatis.helipilatis.client;

import com.helipilatis.helipilatis.server.requests.LoginRequest;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Route("login")
public class LoginView extends VerticalLayout {

    @Autowired
    private RestTemplate restTemplate;

    public LoginView() {
        // Title
        add(new H1("Login"));

        // Login form
        TextField username = new TextField("Username");
        PasswordField password = new PasswordField("Password");
        Button loginButton = new Button("Login");

        add(username, password, loginButton);

        loginButton.addClickListener(event -> {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setUsername(username.getValue());
            loginRequest.setPassword(password.getValue());

            String url = "http://localhost:8080/api/auth/login";
            ResponseEntity<String> response = restTemplate.postForEntity(url, loginRequest, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Notification.show("User authenticated successfully");
                getUI().ifPresent(ui -> ui.navigate("user"));
            } else {
                Notification.show("Authentication failed", 3000, Notification.Position.MIDDLE);
            }
        });
    }
}
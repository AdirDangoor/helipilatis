package com.helipilatis.helipilatis.client;

import com.helipilatis.helipilatis.server.requests.RegisterRequest;
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

@Route("register")
public class RegisterView extends VerticalLayout {

    @Autowired
    private RestTemplate restTemplate;

    public RegisterView() {
        // Title
        add(new H1("Register"));

        // Registration form
        TextField username = new TextField("Username");
        TextField email = new TextField("Email");
        PasswordField password = new PasswordField("Password");
        PasswordField confirmPassword = new PasswordField("Confirm Password");
        Button registerButton = new Button("Register");

        add(username, email, password, confirmPassword, registerButton);

        registerButton.addClickListener(event -> {
            RegisterRequest registerRequest = new RegisterRequest();
            registerRequest.setUsername(username.getValue());
            registerRequest.setEmail(email.getValue());
            registerRequest.setPassword(password.getValue());

            String url = "http://localhost:8080/api/auth/register";
            ResponseEntity<String> response = restTemplate.postForEntity(url, registerRequest, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Notification.show("User registered successfully");
                getUI().ifPresent(ui -> ui.navigate("user"));
            } else {
                Notification.show("Registration failed", 3000, Notification.Position.MIDDLE);
            }
        });
    }
}
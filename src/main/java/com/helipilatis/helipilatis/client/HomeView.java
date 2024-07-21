package com.helipilatis.helipilatis.client;

import com.helipilatis.helipilatis.server.login_and_register.LoginRequest;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import com.vaadin.flow.component.notification.Notification;
import org.springframework.http.HttpStatus;

@Route("")
public class HomeView extends VerticalLayout {


    @Autowired
    private RestTemplate restTemplate;

    public HomeView() {
        // Title
        add(new H1("Welcome to your new application"));

        // Login form
        VerticalLayout loginForm = new VerticalLayout();
        TextField loginUsername = new TextField("Username");
        PasswordField loginPassword = new PasswordField("Password");
        Button loginButton = new Button("Login");
        loginForm.add(new H1("Login"), loginUsername, loginPassword, loginButton);

        // Registration form
        VerticalLayout registrationForm = new VerticalLayout();
        TextField registerUsername = new TextField("Username");
        TextField registerEmail = new TextField("Email");
        PasswordField registerPassword = new PasswordField("Password");
        PasswordField confirmPassword = new PasswordField("Confirm Password");
        Button registerButton = new Button("Register");
        registrationForm.add(new H1("Register"), registerUsername, registerEmail, registerPassword, confirmPassword, registerButton);

        // Layout for forms
        HorizontalLayout formsLayout = new HorizontalLayout(loginForm, registrationForm);
        formsLayout.setSizeFull();

        add(formsLayout);


        loginButton.addClickListener(event -> {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setUsername(loginUsername.getValue());
            loginRequest.setPassword(loginPassword.getValue());

            // Assuming your backend is running on the same host and port
            String url = "http://localhost:8080/api/auth/login";
            ResponseEntity<String> response = restTemplate.postForEntity(url, loginRequest, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Notification.show("User authenticated successfully");
                getUI().ifPresent(ui -> ui.navigate("main"));
            } else {
                Notification.show("Authentication failed", 3000, Notification.Position.MIDDLE);
            }
        });


        // Example of handling registration
        registerButton.addClickListener(event -> {
            // Perform registration logic
            // Show success message or navigate
        });
    }
}
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
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.html.Div;

@Route("login")
public class LoginView extends VerticalLayout {

    @Autowired
    private RestTemplate restTemplate;

    public LoginView() {
        // Set the VerticalLayout to full size and center its content
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        // Set background image
        String imagePath = "images/shop_background.jpg"; // Replace with your image path
        getElement().getStyle()
                .set("background-image", "url('" + imagePath + "')")
                .set("background-size", "cover")
                .set("background-position", "center");
        // Create a container for the login form
        Div loginForm = new Div();
        loginForm.getStyle().set("width", "300px")
                .set("padding", "20px")
                .set("border", "1px solid #ccc")
                .set("border-radius", "5px")
                .set("background-color", "white");

        // Title
        H1 title = new H1("Login");
        title.getStyle().set("text-align", "center");

        // Login form elements
        TextField username = new TextField("Phone Number");
        username.setWidthFull();


        Button loginButton = new Button("Login");
        loginButton.setWidthFull();

        // Add components to the login form container
        loginForm.add(title, username, loginButton);

        // Add the login form container to the main layout
        add(loginForm);

        loginButton.addClickListener(event -> {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setUsername(username.getValue());

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
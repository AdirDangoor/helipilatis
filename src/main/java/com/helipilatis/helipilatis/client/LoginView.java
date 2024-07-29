package com.helipilatis.helipilatis.client;

import com.helipilatis.helipilatis.server.requests.LoginRequest;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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

    @Autowired
    private ApiRequests apiRequests;

    public LoginView() {
        // Title
        add(new H1("Login"));

        // Login form
        TextField phone = new TextField("Phone");
        Button loginButton = new Button("Login");

        add(phone, loginButton);

        loginButton.addClickListener(event -> {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setPhone(phone.getValue());

            ResponseEntity<String> response = apiRequests.login(loginRequest);
            if (response.getStatusCode() == HttpStatus.OK) {
                // Store userId in local storage
                String userId = response.getBody();
                getUI().ifPresent(ui -> ui.getPage().executeJs("localStorage.setItem('userId', $0);", userId));
                Notification.show("User authenticated successfully");
                getUI().ifPresent(ui -> ui.navigate("user"));
            } else {
                Notification.show("Authentication failed: " + response.getBody(), 3000, Notification.Position.MIDDLE);
            }
        });
    }
}
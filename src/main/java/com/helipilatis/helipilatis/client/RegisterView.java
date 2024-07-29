package com.helipilatis.helipilatis.client;

import com.helipilatis.helipilatis.server.requests.RegisterRequest;
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

@Route("register")
public class RegisterView extends VerticalLayout {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ApiRequests apiRequests;

    public RegisterView() {
        // Title
        add(new H1("Register"));

        // Register form
        TextField name = new TextField("Name");
        TextField phone = new TextField("Phone");
        Button registerButton = new Button("Register");

        add(name, phone, registerButton);

        registerButton.addClickListener(event -> {
            RegisterRequest registerRequest = new RegisterRequest();
            registerRequest.setName(name.getValue());
            registerRequest.setPhone(phone.getValue());

            ResponseEntity<String> response = apiRequests.register(registerRequest);
            if (response.getStatusCode() == HttpStatus.OK) {
                Notification.show("User registered successfully");
                getUI().ifPresent(ui -> ui.navigate("user"));
            } else {
                Notification.show("Registration failed: " + response.getBody(), 3000, Notification.Position.MIDDLE);
            }
        });
    }
}
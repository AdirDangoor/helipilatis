package com.helipilatis.helipilatis.client;

import com.helipilatis.helipilatis.server.requests.LoginRequest;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.html.Div;
import java.util.logging.Logger;
@Route("login")
public class LoginView extends BaseView {

    public LoginView() {
        super();
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
        TextField phone = new TextField("Phone Number");
        phone.setWidthFull();

        Button loginButton = new Button("Login");
        loginButton.setWidthFull();

        // Add components to the login form container
        loginForm.add(title, phone, loginButton);

        // Add the login form container to the main layout
        add(loginForm);

        loginButton.addClickListener(event -> {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setPhone(phone.getValue());

            ResponseEntity<String> response = apiRequests.login(loginRequest);
            if (response.getStatusCode() == HttpStatus.OK) {
                // Store userId in VaadinSession
                String userId = response.getBody();
                logger.info("userId: " + userId);
                VaadinSession session = VaadinSession.getCurrent();
                if (session != null) {
                    session.setAttribute("userId", Long.parseLong(userId));
                }
                Notification.show("User authenticated successfully");
                getUI().ifPresent(ui -> ui.navigate("user"));
            } else {
                Notification.show("Authentication failed: " + response.getBody(), 3000, Notification.Position.MIDDLE);
            }
        });
    }
}
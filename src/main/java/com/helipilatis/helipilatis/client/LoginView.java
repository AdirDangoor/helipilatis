package com.helipilatis.helipilatis.client;

import com.helipilatis.helipilatis.client.requests.LoginResponse;
import com.helipilatis.helipilatis.server.requests.LoginRequest;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.html.Div;
import java.util.logging.Logger;

@Route("login")
public class LoginView extends BaseView {

    private TextField phone;
    private Button loginButton;

    public LoginView() {
        super();
        try {
            initializeLayout();
            setBackgroundImage();
            createLoginForm();
        } catch (Exception ex) {
            logger.severe("Error initializing LoginView: " + ex.getMessage());
            Notification.show("Error initializing view", 3000, Notification.Position.MIDDLE);
        }
    }

    private void initializeLayout() {
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
    }

    private void setBackgroundImage() {
        String imagePath = "images/shop_background.jpg"; // Replace with your image path
        getElement().getStyle()
                .set("background-image", "url('" + imagePath + "')")
                .set("background-size", "cover")
                .set("background-position", "center");
    }

    private void createLoginForm() {
        Div loginForm = new Div();
        loginForm.getStyle().set("width", "300px")
                .set("padding", "20px")
                .set("border", "1px solid #ccc")
                .set("border-radius", "5px")
                .set("background-color", "rgba(255, 255, 255, 0.8)"); // Semi-transparent white

        H1 title = new H1("Login");
        title.getStyle()
                .set("text-align", "center")
                .set("color", "#003366") // Same color as shop-title
                .set("font-size", "2em");

        phone = new TextField("Phone Number");
        phone.setWidthFull();

        loginButton = new Button("Login");
        loginButton.setWidthFull();

        loginForm.add(title, phone, loginButton);
        add(loginForm);

        loginButton.addClickListener(event -> handleLoginButtonClick());
    }

    private void handleLoginButtonClick() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPhone(phone.getValue());

        ResponseEntity<LoginResponse> response = apiRequests.login(loginRequest);
        if (response.getStatusCode() == HttpStatus.OK) {
            LoginResponse loginResponse = response.getBody();
            if (loginResponse != null) {
                Long userId = loginResponse.getUserId();
                boolean isInstructor = loginResponse.isInstructor();
                logger.info("userId: " + userId + ", isInstructor: " + isInstructor);

                VaadinSession session = VaadinSession.getCurrent();
                if (session != null) {
                    session.setAttribute("userId", userId);
                    session.setAttribute("isInstructor", isInstructor);
                }

                if (isInstructor) {
                    Notification.show("Login successful - Instructor", 3000, Notification.Position.MIDDLE);
                    getUI().ifPresent(ui -> ui.navigate("instructor"));
                } else {
                    Notification.show("Login successful - User", 3000, Notification.Position.MIDDLE);
                    getUI().ifPresent(ui -> ui.navigate("user"));
                }
            }
        } else {
            Notification.show("Authentication failed: " + response.getBody(), 3000, Notification.Position.MIDDLE);
        }
    }
}
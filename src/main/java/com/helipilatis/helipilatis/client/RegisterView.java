package com.helipilatis.helipilatis.client;

import com.helipilatis.helipilatis.server.requests.RegisterRequest;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
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

@Route("register")
public class RegisterView extends BaseView {

    private final TextField phone = createStyledTextField("Phone");
    private final TextField name = createStyledTextField("Name");
    private final TextField age = createStyledTextField("Age");
    private final ComboBox<String> genderComboBox = createStyledComboBox("Gender", new String[]{"Man", "Woman", "Prefer not to say"});
    private final Button registerButton = new Button("Register");

    public RegisterView() {
        super();
        try{
            setupLayout();
            setupEventHandlers();
        } catch (Exception ex) {
            logger.severe("Error initializing RegisterView: " + ex.getMessage());
            Notification.show("Error initializing view", 3000, Notification.Position.MIDDLE);
        }
    }

    private void setupLayout() {
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        // Set background image
        setBackgroundImage("images/shop_background.jpg");

        // Create and add the registration form
        Div regForm = createRegistrationForm();
        add(regForm);
    }

    private void setBackgroundImage(String imagePath) {
        getElement().getStyle()
                .set("background-image", "url('" + imagePath + "')")
                .set("background-size", "cover")
                .set("background-position", "center");
    }

    private Div createRegistrationForm() {
        Div regForm = new Div();
        regForm.getStyle().set("width", "300px")
                .set("padding", "20px")
                .set("border", "1px solid #ccc")
                .set("border-radius", "5px")
                .set("background-color", "rgba(255, 255, 255, 0.8)"); // Semi-transparent white

        VerticalLayout formLayout = new VerticalLayout();
        formLayout.setWidth("100%");
        formLayout.setAlignItems(Alignment.STRETCH);

        H1 title = new H1("Register");
        title.getStyle()
                .set("text-align", "center")
                .set("color", "#003366") // Same color as shop-title
                .set("font-size", "2em");

        formLayout.add(title, phone, name, age, genderComboBox, registerButton);
        regForm.add(formLayout);

        return regForm;
    }

    private void setupEventHandlers() {
        registerButton.addClickListener(event -> handleRegisterButtonClick());
    }

    private void handleRegisterButtonClick() {
        try {
            RegisterRequest registerRequest = buildRegisterRequest();
            ResponseEntity<String> response = apiRequests.register(registerRequest);

            if (response.getStatusCode() == HttpStatus.OK) {
                handleSuccessfulRegistration(response.getBody());
            } else {
                Notification.show("Registration failed: " + response.getBody(), 3000, Notification.Position.MIDDLE);
            }
        } catch (NumberFormatException e) {
            Notification.show("Invalid age input. Please enter a valid number.", 3000, Notification.Position.MIDDLE);
        }
    }

    private RegisterRequest buildRegisterRequest() {
        int ageValue = Integer.parseInt(age.getValue());
        String phoneValue = phone.getValue();
        String nameValue = name.getValue();
        String genderValue = genderComboBox.getValue();

        if (phoneValue == null || phoneValue.isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be empty.");
        }

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setAge(ageValue);
        registerRequest.setPhone(phoneValue);
        registerRequest.setName(nameValue);
        registerRequest.setGender(genderValue);

        return registerRequest;
    }

    private void handleSuccessfulRegistration(String userId) {
        logger.info("userId: " + userId);
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            session.setAttribute("userId", Long.parseLong(userId));
        }
        Notification.show("User registered successfully");
        getUI().ifPresent(ui -> ui.navigate("user"));
    }

    private TextField createStyledTextField(String label) {
        TextField textField = new TextField(label);
        textField.getStyle().set("background-color", "white")
                .set("border", "1px solid #ccc")
                .set("border-radius", "4px")
                .set("padding", "8px");
        return textField;
    }

    private ComboBox<String> createStyledComboBox(String label, String[] items) {
        ComboBox<String> comboBox = new ComboBox<>(label);
        comboBox.setItems(items);
        comboBox.getStyle().set("background-color", "white")
                .set("border", "1px solid #ccc")
                .set("border-radius", "4px")
                .set("padding", "8px");
        return comboBox;
    }
}

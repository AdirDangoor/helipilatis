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


    public RegisterView() {
        super();

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        // Set background image
        String imagePath = "images/shop_background.jpg"; // Replace with your image path
        getElement().getStyle()
                .set("background-image", "url('" + imagePath + "')")
                .set("background-size", "cover")
                .set("background-position", "center");

        // Create a container for the registration form
        Div regForm = new Div();
        regForm.getStyle().set("width", "300px")
                .set("padding", "20px")
                .set("border", "1px solid #ccc")
                .set("border-radius", "5px")
                .set("background-color", "white");

        VerticalLayout formLayout = new VerticalLayout();
        formLayout.setWidth("100%"); // Use full width of the container
        formLayout.setAlignItems(Alignment.STRETCH);

        // Title
        H1 title = new H1("Register");
        title.getStyle().set("text-align", "center");

        // Registration form fields
        TextField phone = createStyledTextField("Phone");
        TextField name = createStyledTextField("Name");
        TextField age = createStyledTextField("Age");
        ComboBox<String> genderComboBox = createStyledComboBox("Gender", new String[]{"Man", "Woman", "Prefer not to say"});
        Button registerButton = new Button("Register");

        formLayout.add(title, phone, name, age, genderComboBox, registerButton);
        regForm.add(formLayout);
        add(regForm); // Add the registration form to the main layout

        registerButton.addClickListener(event -> {
            try {
                int ageValue = Integer.parseInt(age.getValue());
                String phoneValue = phone.getValue();
                String nameValue = name.getValue();
                String genderValue = genderComboBox.getValue();

                if (phoneValue == null || phoneValue.isEmpty()) {
                    Notification.show("Phone number cannot be empty.", 3000, Notification.Position.MIDDLE);
                    return;
                }

                RegisterRequest registerRequest = new RegisterRequest();
                registerRequest.setAge(ageValue);
                registerRequest.setPhone(phoneValue);
                registerRequest.setName(nameValue);
                registerRequest.setGender(genderValue);


                ResponseEntity<String> response = apiRequests.register(registerRequest);

                if (response.getStatusCode() == HttpStatus.OK) {
                    // Store userId in VaadinSession
                    String userId = response.getBody();
                    logger.info("userId: " + userId);
                    VaadinSession session = VaadinSession.getCurrent();
                    if (session != null) {
                        session.setAttribute("userId", Long.parseLong(userId));
                    }
                    Notification.show("User registered successfully");
                    getUI().ifPresent(ui -> ui.navigate("user"));
                } else {
                    Notification.show("Registration failed: " + response.getBody(), 3000, Notification.Position.MIDDLE);
                }
            } catch (NumberFormatException e) {
                Notification.show("Invalid age input. Please enter a valid number.", 3000, Notification.Position.MIDDLE);
            }
        });
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

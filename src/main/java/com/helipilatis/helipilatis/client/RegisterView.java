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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Route("register")
public class RegisterView extends VerticalLayout {

    @Autowired
    private RestTemplate restTemplate;

    public RegisterView() {
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
            RegisterRequest registerRequest = new RegisterRequest();
            registerRequest.setAge(Integer.parseInt(age.getValue()));
            registerRequest.setGender(genderComboBox.getValue());
            registerRequest.setName(name.getValue());
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

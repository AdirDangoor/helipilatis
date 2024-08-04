package com.helipilatis.helipilatis.client;

import com.helipilatis.helipilatis.databaseModels.PilatisClass;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.time.format.DateTimeFormatter;

@Route("my-classes")
public class MyClassesView extends BaseView {

    private final Logger logger = Logger.getLogger("MyClassesView");
    private Div contentBox;

    @Autowired
    public MyClassesView(RestTemplate restTemplate) {
        super();
        try {
            this.restTemplate = restTemplate;
            initializeView();
        } catch (Exception ex) {
            logger.severe("Error initializing MyClassesView: " + ex.getMessage());
            Notification.show("Error initializing view", 3000, Notification.Position.MIDDLE);
        }
    }

    private void initializeView() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            Notification.show("User not logged in", 3000, Notification.Position.MIDDLE);
            getUI().ifPresent(ui -> ui.navigate("")); // Redirect to login page
            return; // Stop further processing
        }
        setupBackground();
        checkInstructorStatus();
        initializeContentBox();
        displayUserClasses();
    }

    private void setupBackground() {
        String imagePath = "images/shop_background.jpg"; // Replace with your image path
        getElement().getStyle()
                .set("background-image", "url('" + imagePath + "')")
                .set("background-size", "cover")
                .set("background-position", "center");
    }

    private void checkInstructorStatus() {
        if (isInstructor()) {
            Notification.show("Instructors cannot view this page", 3000, Notification.Position.MIDDLE);
            getUI().ifPresent(ui -> ui.navigate("instructor"));
        }
    }

    private void initializeContentBox() {
        contentBox = new Div();
        contentBox.getStyle()
                .set("width", "600px")
                .set("max-width", "100%")
                .set("height", "100%") // Set height to 100%
                .set("padding", "20px")
                .set("border-radius", "8px")
                .set("background-color", "rgba(173, 216, 230, 0.5)") // Transparent light blue
                .set("color", "black") // Text color for the box
                .set("text-align", "center"); // Center text horizontally
        add(contentBox);
    }

    private void displayUserClasses() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            Notification.show("User not logged in", 3000, Notification.Position.MIDDLE);
            return;
        }
        List<PilatisClass> userClasses = fetchUserClasses(userId);
        contentBox.removeAll(); // Clear previous content

        addHeader("My Classes");

        if (userClasses.isEmpty()) {
            displayNoClassesMessage();
        } else {
            displayClassesGrid(userClasses);
        }
    }

    private void addHeader(String title) {
        H1 header = new H1(title);
        header.getStyle()
                .set("color", "white") // Title color
                .set("margin", "0"); // Remove default margin
        contentBox.add(header);
    }

    private void displayNoClassesMessage() {
        Div message = new Div();
        message.setText("You are not registered for any classes yet...");
        message.getStyle()
                .set("color", "black")
                .set("font-size", "20px")
                .set("text-align", "center")
                .set("margin-top", "20px");
        contentBox.add(message);
    }

    private void displayClassesGrid(List<PilatisClass> userClasses) {
        Grid<PilatisClass> grid = new Grid<>(PilatisClass.class, false);

        // Format date column
        grid.addColumn(pilatisClass -> pilatisClass.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                .setHeader("Date");

        grid.addColumn(PilatisClass::getStartTime).setHeader("Time");
        grid.addColumn(pilatisClass -> pilatisClass.getInstructor().getName()).setHeader("Instructor");
        grid.addColumn(pilatisClass -> pilatisClass.getSignedUsers().size() + "/" + pilatisClass.getMaxParticipants()).setHeader("Participants");
        grid.addComponentColumn(this::createCancelButton).setHeader("");

        grid.setItems(userClasses);

        // Wrap grid in a container div and adjust its size to fit the content
        Div gridContainer = new Div(grid);
        gridContainer.getStyle()
                .set("width", "auto")
                .set("max-width", "100%")
                .set("height", "100%") // Set height to 100%
                .set("overflow-x", "auto") // Allow horizontal scroll if needed
                .set("margin-top", "20px");

        contentBox.add(gridContainer);
    }

    private List<PilatisClass> fetchUserClasses(Long userId) {
        String url = "http://localhost:8080/api/calendar/user-classes/" + userId; // Replace with your actual API endpoint

        ResponseEntity<PilatisClass[]> response = restTemplate.getForEntity(url, PilatisClass[].class);
        logger.info("fetchUserClasses API response: " + Arrays.toString(response.getBody()));
        return List.of(response.getBody());
    }

    private Button createCancelButton(PilatisClass pilatisClass) {
        Button button = new Button("CANCEL");
        button.addClickListener(e -> {
            Long userId = getCurrentUserId();
            if (userId != null) {
                try {
                    ResponseEntity<String> response = apiRequests.userCancelClass(pilatisClass.getId(), userId);

                    if (response.getStatusCode().is2xxSuccessful()) {
                        Notification.show("Class cancelled successfully", 3000, Notification.Position.MIDDLE);
                        refreshClassesContainer();
                    } else {
                        Notification.show("Error cancelling class", 3000, Notification.Position.MIDDLE);
                    }
                } catch (Exception ex) {
                    logger.severe("Error cancelling class: " + ex.getMessage());
                    Notification.show("Error cancelling class", 3000, Notification.Position.MIDDLE);
                }
            } else {
                Notification.show("User not logged in", 3000, Notification.Position.MIDDLE);
            }
        });

        button.getStyle()
                .set("background-color", "#FF0000")
                .set("color", "white")
                .set("border-radius", "4px")
                .set("padding", "0.5em 1em");
        return button;
    }

    private void refreshClassesContainer() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            Notification.show("User not logged in", 3000, Notification.Position.MIDDLE);
            return;
        }
        List<PilatisClass> userClasses = fetchUserClasses(userId);
        contentBox.removeAll();
        addHeader("My Classes");
        if (userClasses.isEmpty()) {
            displayNoClassesMessage();
        } else {
            displayClassesGrid(userClasses);
        }
    }
}
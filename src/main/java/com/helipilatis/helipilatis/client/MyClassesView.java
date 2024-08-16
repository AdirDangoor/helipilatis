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
        try {
            Long userId = getCurrentUserId();
            if (userId == null) {
                Notification.show("User not logged in", 3000, Notification.Position.MIDDLE);
                getUI().ifPresent(ui -> ui.navigate("")); // Redirect to login page
                return; // Stop further processing
            }
            checkInstructorStatus();
            initializeContentBox();
            displayUserClasses();
        } catch (Exception ex) {
            logger.severe("Error in initializeView: " + ex.getMessage());
            Notification.show("Error initializing view", 3000, Notification.Position.MIDDLE);
        }
    }

    private void checkInstructorStatus() {
        try {
            if (isInstructor()) {
                Notification.show("Instructors cannot view this page", 3000, Notification.Position.MIDDLE);
                getUI().ifPresent(ui -> ui.navigate("instructor"));
            }
        } catch (Exception ex) {
            logger.severe("Error in checkInstructorStatus: " + ex.getMessage());
            Notification.show("Error checking instructor status", 3000, Notification.Position.MIDDLE);
        }
    }

    private void initializeContentBox() {
        try {
            contentBox = new Div();
            contentBox.getStyle()
                    .set("padding", "20px")
                    .set("border-radius", "8px")
                    .set("background-color", "rgba(255, 255, 255, 0.8)")
                    .set("color", "black") // Text color for the box
                    .set("text-align", "center") // Center text horizontally
                    .set("width", "auto")  // Fit width to content
                    .set("max-width", "600px") // Maximum width constraint
                    .set("height", "auto") // Fit height to content
                    .set("display", "flex") // Make the box a flex container
                    .set("flex-direction", "column") // Stack children vertically
                    .set("align-items", "center") // Center items horizontally
                    .set("justify-content", "center") // Center items vertically
                    .set("box-sizing", "border-box"); // Include padding/border in width/height calculation
            add(contentBox);
        } catch (Exception ex) {
            logger.severe("Error in initializeContentBox: " + ex.getMessage());
            Notification.show("Error initializing content box", 3000, Notification.Position.MIDDLE);
        }
    }

    private void displayUserClasses() {
        try {
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
        } catch (Exception ex) {
            logger.severe("Error in displayUserClasses: " + ex.getMessage());
            Notification.show("Error displaying user classes", 3000, Notification.Position.MIDDLE);
        }
    }

    private void addHeader(String title) {
        try {
            H1 header = new H1(title);
            header.getStyle()
                    .set("color", "#003366")
                    .set("text-align", "center")
                    .set("margin", "0")
                    .set("font-size", "2em");
            contentBox.add(header);
        } catch (Exception ex) {
            logger.severe("Error in addHeader: " + ex.getMessage());
            Notification.show("Error adding header", 3000, Notification.Position.MIDDLE);
        }
    }

    private void displayNoClassesMessage() {
        try {
            Div message = new Div();
            message.setText("You are not registered for any classes yet...");
            message.getStyle()
                    .set("color", "black")
                    .set("font-size", "1em")
                    .set("text-align", "center")
                    .set("width", "auto")
                    .set("height", "auto")
                    .set("align-items", "center")
                    .set("margin-top", "20px");
            contentBox.add(message);
        } catch (Exception ex) {
            logger.severe("Error in displayNoClassesMessage: " + ex.getMessage());
            Notification.show("Error displaying no classes message", 3000, Notification.Position.MIDDLE);
        }
    }

    private void displayClassesGrid(List<PilatisClass> userClasses) {
        try {
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
                    .set("width", "600px") // Fit width to container
                    .set("height", "auto") // Adjust height to fit content
                    .set("overflow-x", "auto") // Allow horizontal scroll if needed
                    .set("margin-top", "20px");

            contentBox.add(gridContainer);
        } catch (Exception ex) {
            logger.severe("Error in displayClassesGrid: " + ex.getMessage());
            Notification.show("Error displaying classes grid", 3000, Notification.Position.MIDDLE);
        }
    }

    private List<PilatisClass> fetchUserClasses(Long userId) {
        try {
            String url = "http://localhost:8080/api/calendar/user-classes/" + userId; // Replace with your actual API endpoint

            ResponseEntity<PilatisClass[]> response = restTemplate.getForEntity(url, PilatisClass[].class);
            logger.info("fetchUserClasses API response: " + Arrays.toString(response.getBody()));
            return List.of(response.getBody());
        } catch (Exception ex) {
            logger.severe("Error in fetchUserClasses: " + ex.getMessage());
            Notification.show("Error fetching user classes", 3000, Notification.Position.MIDDLE);
            return List.of();
        }
    }

    private Button createCancelButton(PilatisClass pilatisClass) {
        try {
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
        } catch (Exception ex) {
            logger.severe("Error in createCancelButton: " + ex.getMessage());
            Notification.show("Error creating cancel button", 3000, Notification.Position.MIDDLE);
            return new Button();
        }
    }

    private void refreshClassesContainer() {
        try {
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
        } catch (Exception ex) {
            logger.severe("Error in refreshClassesContainer: " + ex.getMessage());
            Notification.show("Error refreshing classes container", 3000, Notification.Position.MIDDLE);
        }
    }
}
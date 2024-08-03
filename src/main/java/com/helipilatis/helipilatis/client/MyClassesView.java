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
import java.util.function.Consumer;
import java.util.logging.Logger;

@Route("my-classes")
public class MyClassesView extends BaseView {

    private final RestTemplate restTemplate;
    private final Logger logger = Logger.getLogger(MyClassesView.class.getName());
    private Div contentBox;

    @Autowired
    public MyClassesView(RestTemplate restTemplate) {
        logger.info("[FUNCTION] MyClassesView constructor");
        this.restTemplate = restTemplate;
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        // Set full-page background image
        String imagePath = "images/shop_background.jpg"; // Replace with your image path
        getElement().getStyle()
                .set("background-image", "url('" + imagePath + "')")
                .set("background-size", "cover")
                .set("background-position", "center");

        // Check if the user is an instructor
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            Boolean isInstructor = (Boolean) session.getAttribute("isInstructor");
            if (isInstructor != null && isInstructor) {
                Notification.show("Instructors cannot view this page", 3000, Notification.Position.MIDDLE);
                getUI().ifPresent(ui -> ui.navigate("instructor"));
                return;
            }
        }

        // Add content box
        contentBox = new Div();
        contentBox.getStyle()
                .set("width", "600px")
                .set("max-width", "100%")
                .set("padding", "20px")
                .set("border-radius", "8px")
                .set("background-color", "rgba(173, 216, 230, 0.5)") // Transparent light blue
                .set("color", "black") // Text color for the box
                .set("text-align", "center"); // Center text horizontally
        add(contentBox);

        // Add header
        H1 header = new H1("My Classes");
        header.getStyle()
                .set("color", "white") // Title color
                .set("margin", "0"); // Remove default margin
        contentBox.add(header);

        // Initialize classes container
        VerticalLayout classesContainer = new VerticalLayout();
        classesContainer.setPadding(true);
        classesContainer.setSpacing(true);
        classesContainer.setWidthFull();
        contentBox.add(classesContainer);

        // Fetch and display the user's classes
        displayUserClasses();
    }

    private void displayUserClasses() {
        getCurrentUserId(userId -> {
            if (userId == null) {
                Notification.show("User not logged in", 3000, Notification.Position.MIDDLE);
                return;
            }
            List<PilatisClass> userClasses = fetchUserClasses(userId);
            contentBox.removeAll(); // Clear previous content

            H1 header = new H1("My Classes");
            header.getStyle()
                    .set("color", "white") // Title color
                    .set("margin", "0"); // Remove default margin
            contentBox.add(header);

            if (userClasses.isEmpty()) {
                // Display a message if there are no classes
                Div message = new Div();
                message.setText("You are not registered for any classes yet...");
                message.getStyle()
                        .set("color", "black")
                        .set("font-size", "20px")
                        .set("text-align", "center")
                        .set("margin-top", "20px");
                contentBox.add(message);
            } else {
                // Display the classes in a grid
                Grid<PilatisClass> grid = new Grid<>(PilatisClass.class, false);
                grid.addColumn(PilatisClass::getDate).setHeader("Date");
                grid.addColumn(PilatisClass::getStartTime).setHeader("Time");
                grid.addColumn(pilatisClass -> pilatisClass.getInstructor().getName()).setHeader("Instructor");
                grid.addColumn(pilatisClass -> pilatisClass.getSignedUsers().size() + "/" + pilatisClass.getMaxParticipants()).setHeader("Participants");
                grid.addComponentColumn(this::createCancelButton).setHeader("");

                grid.setItems(userClasses);
                contentBox.add(grid);
            }
        });
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
            VaadinSession session = VaadinSession.getCurrent();
            if (session != null) {
                Long userId = (Long) session.getAttribute("userId");
                if (userId != null) {
                    try {
                        ResponseEntity<String> response = apiRequests.cancelClassForUser(pilatisClass.getId(), userId);

                        if (response.getStatusCode().is2xxSuccessful()) {
                            Notification.show("Successfully cancelled", 3000, Notification.Position.MIDDLE);
                            refreshClassesContainer(); // Refresh classes instead of reloading the page
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
            } else {
                logger.severe("VaadinSession is null");
                Notification.show("Error: Session is null", 3000, Notification.Position.MIDDLE);
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
        getCurrentUserId(userId -> {
            if (userId == null) {
                Notification.show("User not logged in", 3000, Notification.Position.MIDDLE);
                return;
            }
            List<PilatisClass> userClasses = fetchUserClasses(userId);
            contentBox.removeAll();
            H1 header = new H1("My Classes");
            header.getStyle()
                    .set("color", "white") // Title color
                    .set("margin", "0"); // Remove default margin
            contentBox.add(header);
            Grid<PilatisClass> grid = new Grid<>(PilatisClass.class, false);
            grid.addColumn(PilatisClass::getDate).setHeader("Date");
            grid.addColumn(PilatisClass::getStartTime).setHeader("Time");
            grid.addColumn(pilatisClass -> pilatisClass.getInstructor().getName()).setHeader("Instructor");
            grid.addColumn(pilatisClass -> pilatisClass.getSignedUsers().size() + "/" + pilatisClass.getMaxParticipants()).setHeader("Participants");
            grid.addComponentColumn(this::createCancelButton).setHeader("");

            grid.setItems(userClasses);
            contentBox.add(grid);
        });
    }

    private void getCurrentUserId(Consumer<Long> callback) {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            Long userId = (Long) session.getAttribute("userId");
            logger.info("userId: " + userId);
            callback.accept(userId);
        } else {
            logger.severe("VaadinSession is null");
            callback.accept(null);
        }
    }

}

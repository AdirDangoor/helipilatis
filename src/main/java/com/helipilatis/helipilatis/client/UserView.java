package com.helipilatis.helipilatis.client;

import com.helipilatis.helipilatis.databaseModels.PilatisClass;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Route("user")
public class UserView extends VerticalLayout {

    private static final Logger logger = Logger.getLogger(UserView.class.getName());
    private final RestTemplate restTemplate;

    @Autowired
    private HttpSession session;

    private Long getCurrentUserId() {
        return (Long) session.getAttribute("userId");
    }

    @Autowired
    public UserView(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        // Set the max width and center the layout
        setMaxWidth("1200px");
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        // Fetch and display the schedule
        displaySchedule();
    }

    private void displaySchedule() {
        List<PilatisClass> pilatisClasses = fetchPilatisClasses();
        Map<LocalDate, List<PilatisClass>> groupedByDate = groupByDate(pilatisClasses);

        // Sort the dates in ascending order
        Stream<LocalDate> sortedDates = groupedByDate.keySet().stream().sorted();

        // Create a Tabs component for the dates
        Tabs dateTabs = new Tabs();
        dateTabs.setWidthFull();
        dateTabs.getStyle().set("overflow-x", "auto");

        // Container to hold the classes for each day
        VerticalLayout classesContainer = new VerticalLayout();
        classesContainer.setPadding(true);
        classesContainer.setSpacing(true);

        sortedDates.forEach(date -> {
            List<PilatisClass> items = groupedByDate.get(date);

            // Create tab for each day
            Tab dateTab = new Tab(date.toString());
            dateTab.getElement().getStyle().set("padding", "0.5em 1em");
            dateTabs.add(dateTab);
        });

        dateTabs.addSelectedChangeListener(event -> {
            // Clear previous classes
            classesContainer.removeAll();
            // Get the selected date
            Tab selectedTab = dateTabs.getSelectedTab();
            String selectedDate = selectedTab.getLabel();
            // Find the classes for the selected date
            LocalDate date = LocalDate.parse(selectedDate);
            List<PilatisClass> items = groupedByDate.get(date);
            // Add the classes for the selected date
            VerticalLayout dayClasses = createDayClasses(items);
            classesContainer.add(dayClasses);
        });

        // Add the date tabs and classes container to the layout
        add(dateTabs, classesContainer);
    }

    private List<PilatisClass> fetchPilatisClasses() {
        String url = "http://localhost:8080/api/calendar/classes"; // Replace with your actual API endpoint

        ResponseEntity<PilatisClass[]> response = restTemplate.getForEntity(url, PilatisClass[].class);
        // Print response
        logger.info("fetchPilatisClasses API response : " + Arrays.toString(response.getBody()));
        return List.of(response.getBody());
    }

    private Map<LocalDate, List<PilatisClass>> groupByDate(List<PilatisClass> pilatisClasses) {
        return pilatisClasses.stream().collect(Collectors.groupingBy(PilatisClass::getDate));
    }

    private VerticalLayout createDayClasses(List<PilatisClass> items) {
        VerticalLayout dayClasses = new VerticalLayout();
        dayClasses.setPadding(true);
        dayClasses.setSpacing(true);

        Grid<PilatisClass> grid = new Grid<>(PilatisClass.class, false);
        grid.addColumn(PilatisClass::getStartTime).setHeader("Time");
        grid.addColumn(pilatisClass -> pilatisClass.getInstructor().getName()).setHeader("Instructor");
        grid.addColumn(pilatisClass -> pilatisClass.getSignedUsers().size() + "/" + pilatisClass.getMaxParticipants()).setHeader("Participants");
        grid.addComponentColumn(this::createBookButton).setHeader("");

        grid.setItems(items);
        dayClasses.add(grid);

        return dayClasses;
    }

    private Button createBookButton(PilatisClass pilatisClass) {
        Button button = new Button("BOOK VIRTUAL");
        if (pilatisClass.getSignedUsers().size() >= pilatisClass.getMaxParticipants()) {
            button.setVisible(false);
        } else {
            button.addClickListener(e -> {
                try {
                    // Retrieve userId from local storage
                    getUI().ifPresent(ui -> ui.getPage().executeJs("return localStorage.getItem('userId');").then(String.class, userId -> {
                        if (userId == null) {
                            Notification.show("User not logged in", 3000, Notification.Position.MIDDLE);
                            return;
                        }
                        String url = "http://localhost:8080/api/calendar/classes/" + pilatisClass.getId() + "/signup?userId=" + userId;
                        logger.info("Sending request to URL: " + url);
                        ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);
                        logger.info("Response status code: " + response.getStatusCode());
                        if (response.getStatusCode().is2xxSuccessful()) {
                            Notification.show("Successfully booked", 3000, Notification.Position.MIDDLE);
                        } else if (response.getStatusCode().is4xxClientError()) {
                            Notification.show("Class is full", 3000, Notification.Position.MIDDLE);
                        } else {
                            Notification.show("Error booking class", 3000, Notification.Position.MIDDLE);
                        }
                    }));
                } catch (Exception ex) {
                    logger.severe("Error booking class: " + ex.getMessage());
                    Notification.show("Error booking class", 3000, Notification.Position.MIDDLE);
                }
            });
        }
        button.getStyle()
                .set("background-color", "#007BFF")
                .set("color", "white")
                .set("border-radius", "4px")
                .set("padding", "0.5em 1em");
        return button;
    }

}
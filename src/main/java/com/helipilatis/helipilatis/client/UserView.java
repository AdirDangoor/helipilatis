package com.helipilatis.helipilatis.client;

import com.helipilatis.helipilatis.databaseModels.PilatisClass;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Route("user")
public class UserView extends BaseView {


    private Tabs dateTabs;
    private VerticalLayout classesContainer;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public UserView(RestTemplate restTemplate) {
        super();
        this.restTemplate = restTemplate;
        initializeView();
    }

    private void initializeView() {
        setSizeFull();
        setAlignItems(Alignment.STRETCH);
        setJustifyContentMode(JustifyContentMode.START);
        HorizontalLayout topFooter = createTopFooter();
        setMaxWidth("100%");
        setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setWidthFull();
        mainLayout.setHeightFull(); // Set height to 100%
        mainLayout.add(topFooter);

        displaySchedule(mainLayout);
        add(mainLayout);
        refreshClassesContainer();
    }

    private Span createTicketCountSpan() {
        Long userId = getCurrentUserId();
        int ticketCount = 0;
        if (userId != null) {
            try {
                String url = "http://localhost:8080/api/user/tickets/" + userId;
                ResponseEntity<Integer> response = restTemplate.getForEntity(url, Integer.class);
                if (response.getStatusCode().is2xxSuccessful()) {
                    ticketCount = response.getBody();
                } else {
                    Notification.show("Error fetching ticket count", 3000, Notification.Position.MIDDLE);
                }
            } catch (Exception ex) {
                logger.severe("Error fetching ticket count: " + ex.getMessage());
                Notification.show("Error fetching ticket count", 3000, Notification.Position.MIDDLE);
            }
        }

        Span ticketCountSpan = new Span("Tickets: " + ticketCount);
        ticketCountSpan.getStyle()
                .set("color", "white")
                .set("font-weight", "bold");
        return ticketCountSpan;
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


    private void displaySchedule(VerticalLayout mainLayout) {
        List<PilatisClass> pilatisClasses = fetchPilatisClasses();
        Map<LocalDate, List<PilatisClass>> groupedByDate = groupByDate(pilatisClasses);
        Stream<LocalDate> sortedDates = groupedByDate.keySet().stream().sorted();
        initializeDateTabs();
        initializeClassesContainer();
        addDateTabs(sortedDates, groupedByDate);
        retrieveSelectedTabFromSession();
        mainLayout.add(dateTabs, classesContainer);
    }

    private void initializeDateTabs() {
        dateTabs = new Tabs();
        dateTabs.setWidthFull();
        dateTabs.getStyle().set("overflow-x", "auto");
        dateTabs.getStyle().set("background-color", "rgba(255, 255, 255, 0.8)"); // Set transparent white background

    }

    private void initializeClassesContainer() {
        classesContainer = new VerticalLayout();
        classesContainer.setPadding(true);
        classesContainer.setSpacing(true);
        classesContainer.setWidthFull();
        classesContainer.setHeightFull(); // Set height to 100%

    }

    private void addDateTabs(Stream<LocalDate> sortedDates, Map<LocalDate, List<PilatisClass>> groupedByDate) {
        sortedDates.forEach(date -> {
            // Get the day name
            String dayName = date.getDayOfWeek().toString(); // Example: MONDAY
            String formattedDayName = dayName.substring(0, 1) + dayName.substring(1).toLowerCase(); // Capitalize first letter
            String formattedDate = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")); // Format date
            String tabLabel = formattedDate + " (" + formattedDayName + ")";

            // Create tab for each day
            Tab dateTab = new Tab(tabLabel);
            dateTab.getElement().getStyle().set("padding", "0.5em 1em");
            dateTabs.add(dateTab);
        });

        dateTabs.addSelectedChangeListener(event -> {
            logger.info("Selected tab: " + dateTabs.getSelectedTab().getLabel());
            classesContainer.removeAll();
            Tab selectedTab = dateTabs.getSelectedTab();
            String selectedLabel = selectedTab.getLabel();
            String selectedDate = selectedLabel.split(" ")[0]; // Extract the date part
            LocalDate date = LocalDate.parse(selectedDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            List<PilatisClass> items = groupedByDate.get(date);
            VerticalLayout dayClasses = createDayClasses(items);
            classesContainer.add(dayClasses);
            refreshClassesContainer();
        });
    }

    private void retrieveSelectedTabFromSession() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            String selectedDate = (String) session.getAttribute("selectedDate");
            if (selectedDate != null) {
                dateTabs.getChildren()
                        .filter(tab -> ((Tab) tab).getLabel().equals(selectedDate))
                        .findFirst()
                        .ifPresent(tab -> dateTabs.setSelectedTab((Tab) tab));
            }
        }
    }

    // UserView.java
    private VerticalLayout createDayClasses(List<PilatisClass> items) {
        VerticalLayout dayClasses = new VerticalLayout();
        dayClasses.setPadding(true);
        dayClasses.setSpacing(true);
        dayClasses.setWidthFull();
        dayClasses.setHeightFull(); // Set height to 100%

        dayClasses.getStyle().set("background-color", "black");
        dayClasses.getStyle().set("border-radius", "8px");
        dayClasses.getStyle().set("box-shadow", "0 4px 8px rgba(0, 0, 0, 0.1)");
        dayClasses.getStyle().set("padding", "1em");
        // Add ticket count span to the day classes layout
        Span ticketCountSpan = createTicketCountSpan();
        dayClasses.add(ticketCountSpan);


        Grid<PilatisClass> grid = new Grid<>(PilatisClass.class, false);
        grid.addColumn(pilatisClass -> {
            LocalTime startTime = pilatisClass.getStartTime();
            LocalTime endTime = startTime.plusHours(1);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            return startTime.format(formatter) + " - " + endTime.format(formatter);
        }).setHeader("Time").setAutoWidth(true);
        grid.addColumn(pilatisClass -> pilatisClass.getInstructor().getName()).setHeader("Instructor").setAutoWidth(true);
        grid.addColumn(pilatisClass -> pilatisClass.getSignedUsers().size() + "/" + pilatisClass.getMaxParticipants()).setHeader("Participants").setAutoWidth(true);
        grid.addComponentColumn(pilatisClass -> {
            if (pilatisClass.getSignedUsers().size() < pilatisClass.getMaxParticipants()) {
                return createBookButton(pilatisClass);
            } else {
                return new Div(); // Return an empty component if max participants reached
            }
        }).setHeader("").setAutoWidth(true);

        grid.getStyle().set("background-color", "white");
        grid.getStyle().set("border-radius", "8px");
        grid.getStyle().set("box-shadow", "0 4px 8px rgba(0, 0, 0, 0.1)");
        grid.getStyle().set("padding", "1em");

        grid.setItems(items);
        dayClasses.add(grid);

        return dayClasses;
    }

    private Button createBookButton(PilatisClass pilatisClass) {
        Button button = new Button();
        Long userId = getCurrentUserId();
        if (userId == null) {
            Notification.show("User not logged in", 3000, Notification.Position.MIDDLE);
        }

        boolean isBooked = pilatisClass.getSignedUsers().stream()
                .anyMatch(user -> user.getId().equals(userId));

        if (isBooked) {
            button.setText("Cancel Class");
            button.addClickListener(e -> {
                try {
                    ResponseEntity<String> response = apiRequests.userCancelClass(pilatisClass.getId(), userId);

                    if (response.getStatusCode().is2xxSuccessful()) {
                        Notification.show("Successfully cancelled", 3000, Notification.Position.MIDDLE);
                        UI.getCurrent().navigate("my-classes"); // Navigate to "my classes" page
                    } else {
                        Notification.show("Error cancelling class", 3000, Notification.Position.MIDDLE);
                    }
                } catch (Exception ex) {
                    logger.severe("Error cancelling class: " + ex.getMessage());
                    Notification.show("Error cancelling class", 3000, Notification.Position.MIDDLE);
                }
            });
        } else {
            button.setText("Book Class");
            button.addClickListener(e -> {
                try {
                    ResponseEntity<String> response = apiRequests.userBookClass(pilatisClass.getId(), userId);

                    if (response.getStatusCode().is2xxSuccessful()) {
                        Notification.show("Successfully booked, bought with 1 ticket", 3000, Notification.Position.MIDDLE);
                        UI.getCurrent().navigate("my-classes"); // Navigate to "my classes" page
                    } else {
                        String errorMessage = response.getBody();
                        Notification.show("Error booking class: " + errorMessage, 3000, Notification.Position.MIDDLE);
                        refreshClassesContainer();
                    }
                } catch (Exception ex) {
                    logger.severe("Error booking class: " + ex.getMessage());
                    Notification.show("Error booking class", 3000, Notification.Position.MIDDLE);
                }
            });
        };

        button.getStyle()
                .set("background-color", "#007BFF")
                .set("color", "white")
                .set("border-radius", "4px")
                .set("padding", "0.5em 1em");
        return button;
    }

    private void refreshClassesContainer() {
        classesContainer.removeAll();
        Tab selectedTab = dateTabs.getSelectedTab();
        String selectedLabel = selectedTab.getLabel();
        String selectedDate = selectedLabel.split(" ")[0]; // Extract the date part
        LocalDate date = LocalDate.parse(selectedDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        List<PilatisClass> items = fetchPilatisClasses().stream()
                .filter(pilatisClass -> pilatisClass.getDate().equals(date))
                .collect(Collectors.toList());
        VerticalLayout dayClasses = createDayClasses(items);
        classesContainer.add(dayClasses);
    }

    private HorizontalLayout createTopFooter() {
        HorizontalLayout topFooter = new HorizontalLayout();
        topFooter.setWidthFull();
        topFooter.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        topFooter.setAlignItems(FlexComponent.Alignment.CENTER);

        // Add logo
        Image logo = new Image("images/logo.png", "Logo"); // Replace with your logo path
        logo.setHeight("50px");

        // Create a layout for the buttons
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);

        // Add shop button
        Button shopButton = new Button("Shop", event -> getUI().ifPresent(ui -> ui.navigate("shop")));
        shopButton.getStyle()
                .set("background-color", "#007BFF")
                .set("color", "white");

        // Add "My Classes" button
        Button myClassesButton = new Button("My Classes", event -> {
            Long userId = getCurrentUserId();
            if (userId != null) {
                // Navigate to a page to display the user's classes or make an API call to fetch classes
                getUI().ifPresent(ui -> ui.navigate("my-classes"));
            } else {
                Notification.show("User not logged in", 3000, Notification.Position.MIDDLE);
            }
        });
        myClassesButton.getStyle()
                .set("background-color", "#007BFF")
                .set("color", "white");


        buttonLayout.add(shopButton, myClassesButton);

        // Add logo and button layout to the top footer
        topFooter.add(logo, buttonLayout);
        topFooter.setFlexGrow(0, logo);
        topFooter.setFlexGrow(0, buttonLayout);
        topFooter.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        return topFooter;
    }


}
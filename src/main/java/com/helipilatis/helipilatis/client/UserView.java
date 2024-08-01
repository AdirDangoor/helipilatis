package com.helipilatis.helipilatis.client;

import com.helipilatis.helipilatis.databaseModels.PilatisClass;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.VaadinSession;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
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


    @Autowired
    private HttpSession session;

    public UserView(RestTemplate restTemplate) {
        super();
        this.restTemplate = restTemplate;
        initializeView();
    }

    private void initializeView() {
        setSizeFull();
        setAlignItems(Alignment.STRETCH);
        setJustifyContentMode(JustifyContentMode.START);
        setBackground();
        HorizontalLayout topFooter = createTopFooter();
        setMaxWidth("100%");
        setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setWidthFull();
        mainLayout.add(topFooter);
        displaySchedule(mainLayout);
        add(mainLayout);
    }

    private void setBackground() {
        String imagePath = "images/shop_background.jpg";
        getElement().getStyle()
                .set("background-image", "url('" + imagePath + "')")
                .set("background-size", "cover")
                .set("background-position", "center");
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

    private VerticalLayout createDayClasses(List<PilatisClass> items) {
        VerticalLayout dayClasses = new VerticalLayout();
        dayClasses.setPadding(true);
        dayClasses.setSpacing(true);
        dayClasses.setWidthFull(); // Ensure the day classes layout takes full width

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
        Button button = new Button();
        getCurrentUserId(userId -> {
            if (userId == null) {
                Notification.show("User not logged in", 3000, Notification.Position.MIDDLE);
                return;
            }

            boolean isBooked = pilatisClass.getSignedUsers().stream()
                    .anyMatch(user -> user.getId().equals(userId));

            if (isBooked) {
                button.setText("CANCEL");
                button.addClickListener(e -> {
                    try {
                        ResponseEntity<String> response = apiRequests.cancelClassForUser(pilatisClass.getId(), userId);

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
                button.setText("BOOK VIRTUAL");
                button.addClickListener(e -> {
                    try {
                        ResponseEntity<String> response = apiRequests.bookClass(pilatisClass.getId(), userId);

                        if (response.getStatusCode().is2xxSuccessful()) {
                            Notification.show("Successfully booked", 3000, Notification.Position.MIDDLE);
                            UI.getCurrent().navigate("my-classes"); // Navigate to "my classes" page
                        } else {
                            Notification.show("Error booking class", 3000, Notification.Position.MIDDLE);
                        }
                    } catch (Exception ex) {
                        logger.severe("Error booking class: " + ex.getMessage());
                        Notification.show("Error booking class", 3000, Notification.Position.MIDDLE);
                    }
                });
            }
        });

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
        String selectedDate = selectedTab.getLabel();
        LocalDate date = LocalDate.parse(selectedDate);
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
            getCurrentUserId(userId -> {
                if (userId != null) {
                    // Navigate to a page to display the user's classes or make an API call to fetch classes
                    getUI().ifPresent(ui -> ui.navigate("my-classes"));
                } else {
                    Notification.show("User not logged in", 3000, Notification.Position.MIDDLE);
                }
            });
        });
        myClassesButton.getStyle()
                .set("background-color", "#007BFF")
                .set("color", "white");

        // Add buttons to the button layout
        buttonLayout.add(shopButton, myClassesButton);

        // Add logo and button layout to the top footer
        topFooter.add(logo, buttonLayout);
        topFooter.setFlexGrow(0, logo);
        topFooter.setFlexGrow(0, buttonLayout);
        topFooter.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        return topFooter;
    }


}
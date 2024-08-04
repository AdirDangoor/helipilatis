package com.helipilatis.helipilatis.client;

import com.helipilatis.helipilatis.databaseModels.PilatisClass;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.VaadinSession;
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

import com.vaadin.flow.component.html.Div;

@Route("instructor")
public class InstructorView extends BaseView {

    private Tabs dateTabs;

    private VerticalLayout classesContainer;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Autowired
    public InstructorView(RestTemplate restTemplate) {
        super();
        try {
            this.restTemplate = restTemplate;
            initializeView();
        } catch (Exception ex) {
            logger.severe("Error initializing InstructorView: " + ex.getMessage());
            Notification.show("Error initializing view", 3000, Notification.Position.MIDDLE);
        }
    }

    private void initializeView() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            Notification.show("instructor not logged in", 3000, Notification.Position.MIDDLE);
            getUI().ifPresent(ui -> ui.navigate("")); // Redirect to login page
            return; // Stop further processing
        }
        setSizeFull();
        setAlignItems(Alignment.STRETCH);
        setJustifyContentMode(JustifyContentMode.START);
        setBackground();
        HorizontalLayout topFooter = createTopFooter();
        setMaxWidth("100%");
        setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setWidthFull();
        mainLayout.setHeightFull();
        mainLayout.add(topFooter);
        displaySchedule(mainLayout);
        add(mainLayout);
        refreshClassesContainer();

    }

    private void setBackground() {
        String imagePath = "images/shop_background.jpg";
        getElement().getStyle()
                .set("background-image", "url('" + imagePath + "')")
                .set("background-size", "cover")
                .set("background-position", "center");
    }

    private List<PilatisClass> fetchPilatisClasses() {
        String url = "http://localhost:8080/api/calendar/instructor/classes";
        ResponseEntity<PilatisClass[]> response = restTemplate.getForEntity(url, PilatisClass[].class);
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
        classesContainer.setHeightFull();
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

    private void displayUserNamesForClass(Long classId) {
       logger.info("Displaying user names for class " + classId);
    }

    private VerticalLayout createDayClasses(List<PilatisClass> items) {
        VerticalLayout dayClasses = new VerticalLayout();
        dayClasses.setPadding(true);
        dayClasses.setSpacing(true);
        dayClasses.setWidthFull();
        dayClasses.setHeightFull();
        Grid<PilatisClass> grid = new Grid<>(PilatisClass.class, false);
        grid.addColumn(PilatisClass::getStartTime).setHeader("Time");
        grid.addColumn(pilatisClass -> pilatisClass.getInstructor().getName()).setHeader("Instructor");
        grid.addColumn(pilatisClass -> pilatisClass.getSignedUsers().size() + "/" + pilatisClass.getMaxParticipants()).setHeader("Participants");
        grid.addComponentColumn(this::createBookButton).setHeader("");
        grid.addComponentColumn(pilatisClass -> {
            Details details = new Details();
            details.setSummaryText("User Names");
            details.setOpened(false);
            details.setSummary(new Div(new Text("User Names")));
            details.add(createUserNamesLayout(pilatisClass.getId()));
            return details;
        }).setHeader("");
        grid.addComponentColumn(this::createModifyParticipantsButton).setHeader("Modify Participants");
        grid.setItems(items);
        dayClasses.add(grid);
        return dayClasses;
    }

    private HorizontalLayout createModifyParticipantsButton(PilatisClass pilatisClass) {
        HorizontalLayout layout = new HorizontalLayout();
        TextField participantsField = new TextField();
        participantsField.setValue(String.valueOf(pilatisClass.getMaxParticipants()));
        Button saveButton = new Button("Save");
        saveButton.addClickListener(event -> {
            try {
                int newParticipants = Integer.parseInt(participantsField.getValue());
                if (newParticipants < pilatisClass.getSignedUsers().size()) {
                    Notification.show("New number of participants cannot be less than the current number of signed users.");
                    return;
                }
                if (newParticipants < 1) {
                    Notification.show("Number of participants must be at least 1.");
                    return;
                }
                // Send the new number of participants to the server
                ResponseEntity<String> response = apiRequests.instructorUpdateParticipants(pilatisClass.getId(), newParticipants);
                if (response.getStatusCode().is2xxSuccessful()) {
                    Notification.show("Number of participants updated successfully.");
                    refreshClassesContainer(); // Refresh the grid to reflect the changes
                } else {
                    Notification.show("Error updating participants: " + response.getBody());
                }
            } catch (NumberFormatException e) {
                Notification.show("Please enter a valid number.");
            }
        });
        layout.add(participantsField, saveButton);
        return layout;
    }


    private Div createUserNamesLayout(Long classId) {
        Div userNamesLayout = new Div();
        List<String> userNames = fetchUserNames(classId);
        for (String userName : userNames) {
            Div userNameDiv = new Div();
            userNameDiv.setText(userName);
            userNamesLayout.add(userNameDiv);
        }
        return userNamesLayout;
    }
    private List<String> fetchUserNames(Long classId) {
        String url = "http://localhost:8080/api/calendar/instructor/" + classId + "/users";
        ResponseEntity<String[]> response = restTemplate.getForEntity(url, String[].class);
        logger.info("fetchUserNames API response : " + Arrays.toString(response.getBody()));
        return List.of(response.getBody());
    }

    private Button createBookButton(PilatisClass pilatisClass) {
        Button button = new Button();
        Long userId = getCurrentUserId();
        if (userId == null) {
            Notification.show("User not logged in", 3000, Notification.Position.MIDDLE);
        }
        if (pilatisClass.isCanceled()) {
            button.setText("RESTORE");
            button.addClickListener(e -> {
                try {
                    VaadinSession session = VaadinSession.getCurrent();
                    if (session != null) {
                        Tab selectedTab = dateTabs.getSelectedTab();
                        session.setAttribute("selectedDate", selectedTab.getLabel());
                    }
                    ResponseEntity<String> response = apiRequests.instructorRestoreClass(pilatisClass.getId());
                    if (response.getStatusCode().is2xxSuccessful()) {
                        Notification.show("Successfully restored", 3000, Notification.Position.MIDDLE);
                        refreshClassesContainer();
                    } else {
                        Notification.show("Error restoring class", 3000, Notification.Position.MIDDLE);
                    }
                } catch (Exception ex) {
                    logger.severe("Error restoring class: " + ex.getMessage());
                    Notification.show("Error restoring class", 3000, Notification.Position.MIDDLE);
                }
            });
        } else {
            button.setText("CANCEL");
            button.addClickListener(e -> {
                try {
                    VaadinSession session = VaadinSession.getCurrent();
                    if (session != null) {
                        Tab selectedTab = dateTabs.getSelectedTab();
                        session.setAttribute("selectedDate", selectedTab.getLabel());
                    }
                    ResponseEntity<String> response = apiRequests.instructorCancelClass(pilatisClass.getId());
                    if (response.getStatusCode().is2xxSuccessful()) {
                        Notification.show("Successfully cancelled", 3000, Notification.Position.MIDDLE);
                        refreshClassesContainer();
                    } else {
                        Notification.show("Error cancelling class", 3000, Notification.Position.MIDDLE);
                    }
                } catch (Exception ex) {
                    logger.severe("Error cancelling class: " + ex.getMessage());
                    Notification.show("Error cancelling class", 3000, Notification.Position.MIDDLE);
                }
            });
        };
        button.getStyle()
                .set("background-color", pilatisClass.isCanceled() ? "#28a745" : "#007BFF")
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
        Image logo = new Image("images/logo.png", "Logo");
        logo.setHeight("50px");
        Button shopButton = new Button("Message Users", event -> getUI().ifPresent(ui -> ui.navigate("instructor-message")));
        topFooter.add(logo, shopButton);
        return topFooter;
    }

}
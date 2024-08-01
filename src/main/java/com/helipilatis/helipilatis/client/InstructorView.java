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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Route("instructor")
public class InstructorView extends BaseView{

    private Tabs dateTabs;

    private VerticalLayout classesContainer;

    @Autowired
    public InstructorView(RestTemplate restTemplate) {
        super();

        this.restTemplate = restTemplate;
        setSizeFull(); // Ensure the UserView takes up the full size
        setAlignItems(Alignment.STRETCH); // Stretch items to take full width
        setJustifyContentMode(JustifyContentMode.START); // Align content to the start

        // Set background image
        String imagePath = "images/shop_background.jpg"; // Replace with your image path
        getElement().getStyle()
                .set("background-image", "url('" + imagePath + "')")
                .set("background-size", "cover")
                .set("background-position", "center");

        // Create the top bar with the logo and shop button
        HorizontalLayout topFooter = createTopFooter();


        // Set the max width and center the layout
        setMaxWidth("100%"); // Set max width to 100% to take full width
        setDefaultHorizontalComponentAlignment(Alignment.STRETCH); // Stretch components horizontally

        // Create a vertical layout and add components to it
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setWidthFull(); // Ensure the main layout takes full width

        // Add the topTopBar and topBar to the main layout
        mainLayout.add(topFooter);

        // Fetch and display the schedule
        displaySchedule(mainLayout);

        add(mainLayout);
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

        // Sort the dates in ascending order
        Stream<LocalDate> sortedDates = groupedByDate.keySet().stream().sorted();

        // Initialize the dateTabs component
        dateTabs = new Tabs();
        dateTabs.setWidthFull();
        dateTabs.getStyle().set("overflow-x", "auto");

        // Initialize the classesContainer component
        classesContainer = new VerticalLayout();
        classesContainer.setPadding(true);
        classesContainer.setSpacing(true);
        classesContainer.setWidthFull(); // Ensure the container takes full width

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

        // Retrieve the selected tab from the session
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

        // Add the date tabs and classes container to the main layout
        mainLayout.add(dateTabs, classesContainer);
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

    // Modify the createBookButton method
    private Button createBookButton(PilatisClass pilatisClass) {
        Button button = new Button();
        getCurrentUserId(userId -> {
            if (userId == null) {
                Notification.show("User not logged in", 3000, Notification.Position.MIDDLE);
                return;
            }

            button.setText("CANCEL");
            button.addClickListener(e -> {
                try {
                    // Store the selected tab in the session
                    VaadinSession session = VaadinSession.getCurrent();
                    if (session != null) {
                        Tab selectedTab = dateTabs.getSelectedTab();
                        session.setAttribute("selectedDate", selectedTab.getLabel());
                    }

                    ResponseEntity<String> response = apiRequests.cancelClassAsInstructor(pilatisClass.getId());

                    if (response.getStatusCode().is2xxSuccessful()) {
                        Notification.show("Successfully cancelled", 3000, Notification.Position.MIDDLE);
                        refreshClassesContainer(); // Refresh the classes container
                    } else {
                        Notification.show("Error cancelling class", 3000, Notification.Position.MIDDLE);
                    }
                } catch (Exception ex) {
                    logger.severe("Error cancelling class: " + ex.getMessage());
                    Notification.show("Error cancelling class", 3000, Notification.Position.MIDDLE);
                }
            });

        });

        button.getStyle()
                .set("background-color", "#007BFF")
                .set("color", "white")
                .set("border-radius", "4px")
                .set("padding", "0.5em 1em");
        return button;
    }

    // Add a new method to refresh the classes container
    private void refreshClassesContainer() {
        // Clear previous classes
        classesContainer.removeAll();
        // Get the selected date
        Tab selectedTab = dateTabs.getSelectedTab();
        String selectedDate = selectedTab.getLabel();
        // Find the classes for the selected date
        LocalDate date = LocalDate.parse(selectedDate);
        List<PilatisClass> items = fetchPilatisClasses().stream()
                .filter(pilatisClass -> pilatisClass.getDate().equals(date))
                .collect(Collectors.toList());
        // Add the classes for the selected date
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

        // Add shop button
        Button shopButton = new Button("Shop", event -> getUI().ifPresent(ui -> ui.navigate("shop")));

        topFooter.add(logo, shopButton);
        return topFooter;
    }

}
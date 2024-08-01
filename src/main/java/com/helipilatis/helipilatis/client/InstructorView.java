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
    }

    private void initializeClassesContainer() {
        classesContainer = new VerticalLayout();
        classesContainer.setPadding(true);
        classesContainer.setSpacing(true);
        classesContainer.setWidthFull();
    }

    private void addDateTabs(Stream<LocalDate> sortedDates, Map<LocalDate, List<PilatisClass>> groupedByDate) {
        sortedDates.forEach(date -> {
            List<PilatisClass> items = groupedByDate.get(date);
            Tab dateTab = new Tab(date.toString());
            dateTab.getElement().getStyle().set("padding", "0.5em 1em");
            dateTabs.add(dateTab);
        });

        dateTabs.addSelectedChangeListener(event -> {
            classesContainer.removeAll();
            Tab selectedTab = dateTabs.getSelectedTab();
            String selectedDate = selectedTab.getLabel();
            LocalDate date = LocalDate.parse(selectedDate);
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
        dayClasses.setWidthFull();
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
            if (pilatisClass.isCanceled()) {
                button.setText("RESTORE");
                button.addClickListener(e -> {
                    try {
                        VaadinSession session = VaadinSession.getCurrent();
                        if (session != null) {
                            Tab selectedTab = dateTabs.getSelectedTab();
                            session.setAttribute("selectedDate", selectedTab.getLabel());
                        }
                        ResponseEntity<String> response = apiRequests.restoreClassAsInstructor(pilatisClass.getId());
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
                        ResponseEntity<String> response = apiRequests.cancelClassAsInstructor(pilatisClass.getId());
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
            }
        });
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
        String selectedDate = selectedTab.getLabel();
        LocalDate date = LocalDate.parse(selectedDate);
        List<PilatisClass> items = fetchPilatisClasses().stream()
                .filter(pilatisClass -> pilatisClass.getDate().equals(date))
                .collect(Collectors.toList());
        VerticalLayout dayClasses = createDayClasses(items);
        classesContainer.add(dayClasses);
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

    private HorizontalLayout createTopFooter() {
        HorizontalLayout topFooter = new HorizontalLayout();
        topFooter.setWidthFull();
        topFooter.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        topFooter.setAlignItems(FlexComponent.Alignment.CENTER);
        Image logo = new Image("images/logo.png", "Logo");
        logo.setHeight("50px");
        Button shopButton = new Button("Shop", event -> getUI().ifPresent(ui -> ui.navigate("shop")));
        topFooter.add(logo, shopButton);
        return topFooter;
    }

}
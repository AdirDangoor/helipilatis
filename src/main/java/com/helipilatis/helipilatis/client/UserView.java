package com.helipilatis.helipilatis.client;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.orderedlayout.FlexComponent;


import java.util.ArrayList;
import java.util.List;

@Route("user")
public class UserView extends VerticalLayout {

    public UserView() {
        // Create the top bar with the date and time
        HorizontalLayout topBar = createTopBar();

        // Create the schedule grid
        Grid<ScheduleItem> scheduleGrid = createScheduleGrid();
        Button ShopButton = new Button("Shop", event -> getUI().ifPresent(ui -> ui.navigate("shop")));

        // Create a layout for the top section
        HorizontalLayout topSection = new HorizontalLayout();
        topSection.setWidthFull();
        topSection.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        topSection.setAlignItems(FlexComponent.Alignment.CENTER);

        // Add the topBar and shopButton to the topSection
        topSection.add(topBar, ShopButton);

        // Create a vertical layout and add components to it
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.add(topSection, scheduleGrid);
        add(mainLayout);

    }

    private HorizontalLayout createTopBar() {
        HorizontalLayout topBar = new HorizontalLayout();
        topBar.setWidthFull();
        TextField dateField = new TextField();
        dateField.setValue("9:31 am  Sat 16 Jul");
        dateField.setReadOnly(true);
        topBar.add(dateField);
        return topBar;
    }

    private Grid<ScheduleItem> createScheduleGrid() {
        Grid<ScheduleItem> grid = new Grid<>(ScheduleItem.class, false);

        // Define columns
        grid.addColumn(ScheduleItem::getTime).setHeader("Time");
        grid.addColumn(ScheduleItem::getClassName).setHeader("Class");
        grid.addColumn(ScheduleItem::getInstructor).setHeader("Instructor");
        grid.addComponentColumn(this::createBookButton).setHeader("");

        // Set items
        grid.setItems(getScheduleItems());

        return grid;
    }

    private Button createBookButton(ScheduleItem item) {
        Button button = new Button("BOOK VIRTUAL");
        button.addClickListener(e -> {
            // Add booking logic here
        });
        return button;
    }

    private List<ScheduleItem> getScheduleItems() {
        List<ScheduleItem> items = new ArrayList<>();
        items.add(new ScheduleItem("7:00 AM - 8:00 AM", "Zumba", "Michelle Compass"));
        items.add(new ScheduleItem("7:30 AM - 9:30 AM", "Stretch & Strength", "Emily Jacob"));
        items.add(new ScheduleItem("8:00 AM - 9:00 AM", "Zumba", "Lyn Hartman"));
        items.add(new ScheduleItem("8:30 AM - 10:00 AM", "Stretch & Strength", "Emily Jacob"));
        items.add(new ScheduleItem("10:00 AM - 11:00 AM", "Holistic Health", "Michelle Compass"));
        items.add(new ScheduleItem("10:00 AM - 11:00 AM", "Zumba", "Lyn Hartman"));
        items.add(new ScheduleItem("10:30 AM - 11:50 AM", "Stretch & Strength", "Emily Jacob"));
        items.add(new ScheduleItem("11:00 AM - 12:00 PM", "Zumba", "Michelle Compass"));
        return items;
    }

    public static class ScheduleItem {
        private String time;
        private String className;
        private String instructor;

        public ScheduleItem(String time, String className, String instructor) {
            this.time = time;
            this.className = className;
            this.instructor = instructor;
        }

        public String getTime() {
            return time;
        }

        public String getClassName() {
            return className;
        }

        public String getInstructor() {
            return instructor;
        }
    }
}

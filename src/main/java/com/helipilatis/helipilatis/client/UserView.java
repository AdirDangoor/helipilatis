package com.helipilatis.helipilatis.client;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("user")
public class UserView extends VerticalLayout {

    public UserView() {
        // Simple headers for debugging
        add(new H1("Appointments"));
        add(new H1("Debug Header 1"));
        add(new H1("Debug Header 2"));
        add(new H1("Debug Header 3"));
    }
}
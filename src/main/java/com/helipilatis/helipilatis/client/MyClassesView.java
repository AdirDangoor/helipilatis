package com.helipilatis.helipilatis.client;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("my-classes")
public class MyClassesView extends VerticalLayout {

    public MyClassesView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        // Add header
        H1 header = new H1("My Classes");
        add(header);
    }
}
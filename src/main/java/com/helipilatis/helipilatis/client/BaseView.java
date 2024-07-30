package com.helipilatis.helipilatis.client;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Logger;

public abstract class BaseView extends VerticalLayout {

    protected static final Logger logger = Logger.getLogger(LoginView.class.getName());

    @Autowired
    protected RestTemplate restTemplate;

    @Autowired
    protected ApiRequests apiRequests;

    public BaseView() {
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
    }
}
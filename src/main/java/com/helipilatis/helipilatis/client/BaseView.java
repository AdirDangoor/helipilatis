package com.helipilatis.helipilatis.client;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinSession;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Logger;

public abstract class BaseView extends VerticalLayout {

    protected static final Logger logger = Logger.getLogger(LoginView.class.getName());

    @Autowired
    private HttpSession session;

    @Autowired
    protected RestTemplate restTemplate;

    @Autowired
    protected ApiRequests apiRequests;

    public BaseView() {
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
        setBackground();
    }

    private void setBackground() {
        String imagePath = "images/shop_background.jpg";
        getElement().getStyle()
                .set("background-image", "url('" + imagePath + "')")
                .set("background-size", "cover")
                .set("background-position", "center");
    }

    protected Long getCurrentUserId() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            Long userId = (Long) session.getAttribute("userId");
            logger.info("userId: " + userId);
            return userId;
        } else {
            logger.severe("VaadinSession is null");
            return null;
        }
    }

    protected boolean isInstructor() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            Boolean isInstructor = (Boolean) session.getAttribute("isInstructor");
            return isInstructor != null && isInstructor;
        } else {
            logger.severe("VaadinSession is null");
            return false;
        }
    }
}
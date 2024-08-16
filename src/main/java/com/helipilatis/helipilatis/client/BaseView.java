package com.helipilatis.helipilatis.client;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinSession;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import com.vaadin.flow.component.notification.Notification;

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
        try {
            setSizeFull();
            setJustifyContentMode(JustifyContentMode.CENTER);
            setAlignItems(Alignment.CENTER);
            setBackground();
        } catch (Exception ex) {
            logger.severe("Error initializing BaseView: " + ex.getMessage());
            Notification.show("Error initializing view", 3000, Notification.Position.MIDDLE);
        }
    }

    private void setBackground() {
        try {
            String imagePath = "images/shop_background.jpg";
            getElement().getStyle()
                    .set("background-image", "url('" + imagePath + "')")
                    .set("background-size", "cover")
                    .set("background-position", "center");
        } catch (Exception ex) {
            logger.severe("Error setting background: " + ex.getMessage());
            Notification.show("Error setting background", 3000, Notification.Position.MIDDLE);
        }
    }

    protected Long getCurrentUserId() {
        try {
            VaadinSession session = VaadinSession.getCurrent();
            if (session != null) {
                Long userId = (Long) session.getAttribute("userId");
                logger.info("userId: " + userId);
                return userId;
            } else {
                logger.severe("VaadinSession is null");
                return null;
            }
        } catch (Exception ex) {
            logger.severe("Error getting current user ID: " + ex.getMessage());
            Notification.show("Error getting current user ID", 3000, Notification.Position.MIDDLE);
            return null;
        }
    }

    protected boolean isInstructor() {
        try {
            VaadinSession session = VaadinSession.getCurrent();
            if (session != null) {
                Boolean isInstructor = (Boolean) session.getAttribute("isInstructor");
                return isInstructor != null && isInstructor;
            } else {
                logger.severe("VaadinSession is null");
                return false;
            }
        } catch (Exception ex) {
            logger.severe("Error checking if user is instructor: " + ex.getMessage());
            Notification.show("Error checking if user is instructor", 3000, Notification.Position.MIDDLE);
            return false;
        }
    }
}
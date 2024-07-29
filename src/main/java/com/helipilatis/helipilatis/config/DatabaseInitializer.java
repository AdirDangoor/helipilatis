package com.helipilatis.helipilatis.config;

import com.helipilatis.helipilatis.server.controllers.AuthController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Logger;

@Configuration
public class DatabaseInitializer {

    // logger:
    private static final java.util.logging.Logger logger = Logger.getLogger(AuthController.class.getName());

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void initialize() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            // Create users table if it does not exist
            statement.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id BIGINT PRIMARY KEY, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "phone VARCHAR(255) NOT NULL)");

            // Create USER_SEQ sequence if it does not exist
            statement.execute("CREATE SEQUENCE IF NOT EXISTS USER_SEQ START WITH 1 INCREMENT BY 1");

            // Create instructors table if it does not exist
            statement.execute("CREATE TABLE IF NOT EXISTS instructors (" +
                    "id BIGINT PRIMARY KEY, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "phone VARCHAR(255) NOT NULL)");

            // Create INSTRUCTOR_SEQ sequence if it does not exist
            statement.execute("CREATE SEQUENCE IF NOT EXISTS INSTRUCTOR_SEQ START WITH 1 INCREMENT BY 1");

            // Create calendar table if it does not exist
            statement.execute("CREATE TABLE IF NOT EXISTS calendar (" +
                    "id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
                    "date DATE NOT NULL, " +
                    "start_time TIME NOT NULL, " +
                    "end_time TIME NOT NULL, " +
                    "instructor_id BIGINT, " +
                    "max_participants INT, " + // New column
                    "FOREIGN KEY (instructor_id) REFERENCES instructors(id))");

            statement.execute("CREATE SEQUENCE IF NOT EXISTS PILATIS_CLASS_SEQ START WITH 1 INCREMENT BY 1");

            // Create class_users table if it does not exist
            statement.execute("CREATE TABLE IF NOT EXISTS class_users (" +
                    "class_id BIGINT, " +
                    "user_id BIGINT, " +
                    "FOREIGN KEY (class_id) REFERENCES calendar(id), " +
                    "FOREIGN KEY (user_id) REFERENCES users(id))");

            // Create calendar_metadata table if it does not exist
            statement.execute("CREATE TABLE IF NOT EXISTS calendar_metadata (" +
                    "id BIGINT PRIMARY KEY, " +
                    "last_initialization_date DATE NOT NULL)");

            // Check if the default instructor exists
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM instructors WHERE name = 'kaganov' AND phone = '0123456789'");
            resultSet.next();
            if (resultSet.getInt(1) == 0) {
                // Insert the default instructor
                statement.execute("INSERT INTO instructors (id, name, phone) VALUES (INSTRUCTOR_SEQ.NEXTVAL, 'kaganov', '0123456789')");
                logger.info("Default instructor 'kaganov' added to the database");
            }

            logger.info("Database initialized successfully");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
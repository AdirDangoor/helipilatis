//package com.helipilatis.helipilatis.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import javax.sql.DataSource;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//
//@Component
//public class AddInstructorCommandLineRunner implements CommandLineRunner {
//
//    @Autowired
//    private DataSource dataSource;
//
//    public void addInstructor(String name, String phone) {
//        String sql = "INSERT INTO instructors (id, name, phone) VALUES (INSTRUCTOR_SEQ.NEXTVAL, ?, ?)";
//
//        try (Connection connection = dataSource.getConnection();
//             PreparedStatement statement = connection.prepareStatement(sql)) {
//
//            statement.setString(1, name);
//            statement.setString(2, phone);
//            statement.executeUpdate();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//        if (args.length == 3 && args[0].equals("addInstructor")) {
//            String name = args[1];
//            String phone = args[2];
//            addInstructor(name, phone);
//            System.out.println("Instructor added: " + name + ", " + phone);
//        } else {
//            System.out.println("Usage: addInstructor <name> <phone>");
//        }
//    }
//}
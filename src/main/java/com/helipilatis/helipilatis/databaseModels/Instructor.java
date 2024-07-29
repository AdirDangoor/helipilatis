package com.helipilatis.helipilatis.databaseModels;

import jakarta.persistence.*;

@Entity
@Table(name = "instructors") // Ensure the table name matches the actual table name in the database
public class Instructor {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "instructor_seq")
    @SequenceGenerator(name = "instructor_seq", sequenceName = "INSTRUCTOR_SEQ", allocationSize = 1)
    private Long id;
    private String name;
    private String phone;

    public Instructor() {
    }

    public Instructor(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Instructor{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
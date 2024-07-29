package com.helipilatis.helipilatis.server.requests;

public class RegisterRequest {
    private String username;
    private String phone;
    private String email;
    private String password;
    private String status;
    private int age;
    private String gender;
    private int tickets;

    // Constructors
    public RegisterRequest() {
    }

    public RegisterRequest(String username, String email, String password, String status, int age, String gender, int tickets) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.status = status;
        this.age = age;
        this.gender = gender;
        this.tickets = tickets;
    }
    public RegisterRequest(String phone, String name, int age, String gender)
    {
        this.phone = phone;
        this.username = name;
        this.age =age;
        this.gender=gender;
    }

    // Getters and Setters
    public String getPhone(){
        return phone;
    }
    public void setPhone(String phone){
        this.phone = phone;
    }
    public String getName(){
        return username;
    }
    public void setName(String name){
        this.username = name;
    }
    public int getAge(){
        return age;
    }
    public void setAge(int age){
        this.age = age;
    }
    public String getGender(){
        return gender;
    }
    public void setGender(String gender){
        this.gender=gender;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }



    public int getTickets() {
        return tickets;
    }

    public void setTickets(int tickets) {
        this.tickets = tickets;
    }

    // toString method
    @Override
    public String toString() {
        return "RegisterRequest{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", status='" + status + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", tickets=" + tickets +
                '}';
    }
}

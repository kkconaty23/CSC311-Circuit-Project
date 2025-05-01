package org.example.circuit_project;

public class User {
    String ID;
    String firstName;
    String lastName;
    String email;
    String password;
    LoginController loginController = new LoginController();
    public User(){
        this.ID = "";
        this.firstName = "";
        this.lastName = "";
        this.email = "";
        this.password = "";
    }

    public User(String id, String firstName, String lastName, String email, String password) {
       this.ID = id;
       this.firstName = firstName;
       this.lastName = lastName;
       this.email = email;
       this.password = password;
    }
    String getID() {
        return ID;
    }
    String getFirstName() {
        return firstName;
    }
    String getLastName() {
        return lastName;
    }
    String getEmail() {
        return email;
    }
    String getPassword() {
        return password;
    }

}

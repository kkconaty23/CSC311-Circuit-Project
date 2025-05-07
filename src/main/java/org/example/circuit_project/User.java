package org.example.circuit_project;

/**
 * Represents a user in the circuit simulator application.
 * <p>
 * A user has identifying and credential-related fields such as:
 * <ul>
 *     <li>User ID</li>
 *     <li>First and last name</li>
 *     <li>Email address</li>
 *     <li>Password</li>
 * </ul>
 */
public class User {
    String ID;
    String firstName;
    String lastName;
    String email;
    String password;
    LoginController loginController = new LoginController();

    /**
     * Default constructor initializes all fields to empty strings.
     */
    public User(){
        this.ID = "";
        this.firstName = "";
        this.lastName = "";
        this.email = "";
        this.password = "";
    }

    /**
     * Constructs a User with all attributes specified.
     *
     * @param id        User ID
     * @param firstName First name
     * @param lastName  Last name
     * @param email     Email address
     * @param password  User password
     */
    public User(String id, String firstName, String lastName, String email, String password) {
       this.ID = id;
       this.firstName = firstName;
       this.lastName = lastName;
       this.email = email;
       this.password = password;
    }

    /** @return User ID */
    String getID() {
        return ID;
    }

    /** @return User's first name */
    String getFirstName() {
        return firstName;
    }

    /** @return User's last name */
    String getLastName() {
        return lastName;
    }

    /** @return User's email */
    String getEmail() {
        return email;
    }

    /** @return User's password */
    String getPassword() {
        return password;
    }

    /**
     * Sets the user's ID.
     *
     * @param ID New user ID
     */
    public void setID(String ID) {
        this.ID = ID;
    }

    /**
     * Sets the user's first name.
     *
     * @param firstName New first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Sets the user's last name.
     *
     * @param lastName New last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Sets the user's email address.
     *
     * @param email New email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Sets the user's password.
     *
     * @param password New password
     */
    public void setPassword(String password) {
        this.password = password;
    }


}

package org.example.circuit_project.Storage;

import org.example.circuit_project.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class DbOpps {
    final String MYSQL_SERVER_URL = "jdbc:mysql://csc311circuitsim.mysql.database.azure.com/";
    final String DB_URL = MYSQL_SERVER_URL + "DBname";
    final String USERNAME = "circuitAdmin";

    final String PASSWORD = "Qwerty123!";


    public boolean connectToDatabase() {
        boolean hasRegistredUsers = false;

        try {
            Connection conn = DriverManager.getConnection(MYSQL_SERVER_URL, USERNAME, PASSWORD);
            Statement statement = conn.createStatement();
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS DBname");
            statement.close();
            conn.close();

            conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            statement = conn.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS circuitUsers ("
                    + "id VARCHAR( 200 ) NOT NULL PRIMARY KEY,"
                    + "email VARCHAR(200) NOT NULL UNIQUE,"
                    + "password VARCHAR(200) NOT NULL,"
                    + "firstname VARCHAR(200) NOT NULL,"
                    + "lastname VARCHAR(200) NOT NULL,"
                    + "dob DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP"
                    + ")";
            statement.executeUpdate(sql);
            statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM circuitUsers");

            if (resultSet.next()) {
                int numUsers = resultSet.getInt(1);
                if (numUsers > 0) {
                    hasRegistredUsers = true;
                }
            }
            System.out.println("Connected to database");
            statement.close();
            conn.close();

        } catch (Exception e) {
            System.out.println("Database connection failed");
            e.printStackTrace();
        }

        return hasRegistredUsers;
    }

    public void insertUser(String uniqueID, String email, String password, String firstname, String lastname, String dob) {

        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "INSERT INTO circuitUsers (id, email, password, firstname, lastname, dob) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, uniqueID);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);
            preparedStatement.setString(4, firstname);
            preparedStatement.setString(5, lastname);
            preparedStatement.setString(6, dob);


            int row = preparedStatement.executeUpdate();

            if (row > 0) {
                System.out.println("A new user was inserted successfully.");
            }

            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User queryUserByName(String message, String password) {
        User currentUser = null;

        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

            String sql = "SELECT * FROM circuitusers WHERE email = ? AND password = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, message);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String email = resultSet.getString("email");
                String firstName = resultSet.getString("firstname");
                String lastName = resultSet.getString("lastname");
                String dob = resultSet.getString("dob");
                String pass = resultSet.getString("password");
                currentUser = new User(id, firstName, lastName, email, pass);
                System.out.println("ID: " + id + ", Email: " + email + ", FirstName: " + firstName + ", LastName: " + lastName);
            }

            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return currentUser;
    }

    /**
     * This method is utilized by the profile controller to change the password of the current user. It creates a UPDATE
     *  SQL query and uses the users UUID as the where clause.
     * @param newPassword a new password inputted by the user
     * @param id The users UUID taken from the User class
     * @return boolean dependent on a successful update
     */
    public boolean changePassword(String newPassword, String id) {
        boolean result = false;
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "UPDATE circuitusers SET password = ? WHERE id = ?;";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, newPassword);
            preparedStatement.setString(2, id);

            preparedStatement.executeUpdate();

            System.out.println("Password changed successfully");
            result = true;
            preparedStatement.close();
            conn.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * This method is ultilzed by the profile controller to change the personal info of a current user.
     * An UPDATE SQL query is created and changes the firstName, lastName and email of a user according
     * to the users UUID.
     * @param id The current users UUID
     * @param firstname The new first name of the user
     * @param lastname The new last name of the user
     * @param email The new email of the user
     * @return boolean according to the success of the update
     */
    public boolean chanegUserInfo(String id, String firstname, String lastname, String email) {
        boolean result = false;
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "UPDATE circuitusers SET firstname = ?, lastname = ?, email = ? WHERE id = ?;";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, firstname);
            preparedStatement.setString(2, lastname);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, id);

            preparedStatement.executeUpdate();

            System.out.println("User info changed successfully");
            result = true;
            preparedStatement.close();
            conn.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return result;
    }
}

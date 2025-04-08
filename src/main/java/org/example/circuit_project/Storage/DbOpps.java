package org.example.circuit_project.Storage;

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

    //password will be shared. When you have it, do not push it. DONT EXPOSE US :D
    final String PASSWORD = "";




    public boolean connectToDatabase() {
        boolean hasRegistredUsers = false;

//email, retype, pass, retype, first, last, dob
        //Class.forName("com.mysql.jdbc.Driver");
        try {
            //First, connect to MYSQL server and create the database if not created
            Connection conn = DriverManager.getConnection(MYSQL_SERVER_URL, USERNAME, PASSWORD);
            Statement statement = conn.createStatement();
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS DBname");
            statement.close();
            conn.close();

            //Second, connect to the database and create the table "users" if cot created
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

            //check if we have users in the table users
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
}

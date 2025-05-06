package org.example.circuit_project.Storage;

import org.example.circuit_project.Project;
import org.example.circuit_project.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


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

    /**
     * Helper method to get a database connection
     * @return Connection to the database
     * @throws SQLException if connection fails
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
    }
    /**
     * Retrieves all projects associated with a specific user
     *
     * @param userId String ID of the user whose projects to retrieve
     * @return List of Project objects
     */
    public List<Project> getUserProjects(String userId) {
        List<Project> projects = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            String query = "SELECT BIN_TO_UUID(id) as id_str, name, description, " +
                    "BIN_TO_UUID(id) as user_id_str, created_at, last_modified, blob_reference " +
                    "FROM projects WHERE id = UUID_TO_BIN(?) ORDER BY last_modified DESC";
            statement = connection.prepareStatement(query);
            statement.setString(1, userId);

            System.out.println("Executing query with user ID: " + userId);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String projectId = resultSet.getString("id_str");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                String projectUserId = resultSet.getString("user_id_str");
                Timestamp createdTimestamp = resultSet.getTimestamp("created_at");
                Timestamp modifiedTimestamp = resultSet.getTimestamp("last_modified");
                String blobReference = resultSet.getString("blob_reference");

                LocalDateTime createdAt = createdTimestamp.toLocalDateTime();
                LocalDateTime lastModified = modifiedTimestamp.toLocalDateTime();

                Project project = new Project(projectId, name, description, projectUserId,
                        createdAt, lastModified, blobReference);
                projects.add(project);

                System.out.println("Loaded project: " + name + " (ID: " + projectId + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error retrieving user projects: " + e.getMessage());
        } finally {
            closeResources(connection, statement, resultSet);
        }

        return projects;
    }

    /**
     * Creates a new project in the database
     *
     * @param project Project to create
     * @return true if successful, false otherwise
     */
    public boolean createProject(Project project) {
        Connection connection = null;
        PreparedStatement statement = null;
        boolean success = false;

        try {
            connection = getConnection();
            String query = "INSERT INTO projects (id, name, description, user_id, created_at, last_modified, blob_reference) " +
                    "VALUES (UUID_TO_BIN(?), ?, ?, UUID_TO_BIN(?), ?, ?, ?)";
            statement = connection.prepareStatement(query);

            statement.setString(1, project.getId());
            statement.setString(2, project.getName());
            statement.setString(3, project.getDescription());
            statement.setString(4, project.getUserId());
            statement.setTimestamp(5, Timestamp.valueOf(project.getCreatedAt()));
            statement.setTimestamp(6, Timestamp.valueOf(project.getLastModified()));
            statement.setString(7, project.getBlobReference());

            int rowsAffected = statement.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error creating project: " + e.getMessage());
        } finally {
            closeResources(connection, statement, null);
        }

        return success;
    }

    /**
     * Updates an existing project in the database
     *
     * @param project Project to update
     * @return true if successful, false otherwise
     */
    public boolean updateProject(Project project) {
        Connection connection = null;
        PreparedStatement statement = null;
        boolean success = false;

        try {
            connection = getConnection();
            String query = "UPDATE projects SET name = ?, description = ?, last_modified = ?, blob_reference = ? " +
                    "WHERE id = UUID_TO_BIN(?)";
            statement = connection.prepareStatement(query);

            statement.setString(1, project.getName());
            statement.setString(2, project.getDescription());
            statement.setTimestamp(3, Timestamp.valueOf(project.getLastModified()));
            statement.setString(4, project.getBlobReference());
            statement.setString(5, project.getId());

            int rowsAffected = statement.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error updating project: " + e.getMessage());
        } finally {
            closeResources(connection, statement, null);
        }

        return success;
    }

    /**
     * Deletes a project from the database
     *
     * @param projectId ID of the project to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteProject(String projectId) {
        Connection connection = null;
        PreparedStatement statement = null;
        boolean success = false;

        try {
            connection = getConnection();
            String query = "DELETE FROM projects WHERE id = UUID_TO_BIN(?)";
            statement = connection.prepareStatement(query);

            statement.setString(1, projectId);

            int rowsAffected = statement.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error deleting project: " + e.getMessage());
        } finally {
            closeResources(connection, statement, null);
        }

        return success;
    }

    /**
     * Retrieves a project by its ID
     *
     * @param projectId ID of the project to retrieve
     * @return Project object if found, null otherwise
     */
    public Project getProjectById(String projectId) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Project project = null;

        try {
            connection = getConnection();
            String query = "SELECT BIN_TO_UUID(id) as id_str, name, description, BIN_TO_UUID(user_id) as user_id_str, " +
                    "created_at, last_modified, blob_reference " +
                    "FROM projects WHERE id = UUID_TO_BIN(?)";
            statement = connection.prepareStatement(query);
            statement.setString(1, projectId);

            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                String userId = resultSet.getString("user_id_str");
                Timestamp createdTimestamp = resultSet.getTimestamp("created_at");
                Timestamp modifiedTimestamp = resultSet.getTimestamp("last_modified");
                String blobReference = resultSet.getString("blob_reference");

                LocalDateTime createdAt = createdTimestamp.toLocalDateTime();
                LocalDateTime lastModified = modifiedTimestamp.toLocalDateTime();

                project = new Project(projectId, name, description, userId,
                        createdAt, lastModified, blobReference);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error retrieving project: " + e.getMessage());
        } finally {
            closeResources(connection, statement, resultSet);
        }

        return project;
    }

    /**
     * Helper method to close database resources
     */
    private void closeResources(Connection connection, Statement statement, ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

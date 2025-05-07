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

/**
 * This class is used to connect and query SQL commands to the circuitsim database hosted on a Flexible server via Azure.
 * All operations for both circuitUsers and project tables are contained within this class.
 */
public class DbOpps {
    final String MYSQL_SERVER_URL = "jdbc:mysql://csc311circuitsim.mysql.database.azure.com/";
    final String DB_URL = MYSQL_SERVER_URL + "DBname";
    final String USERNAME = "circuitAdmin";

    final String PASSWORD = "Qwerty123!";

    /**
     * This method is Azure boilerplate code to establish a connection to the DB.
     * @return boolean according to a successful connection
     */
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

    /**
     * This method is used to check if an email exists within the circuitUsers DB to ensure there are
     * no duplicate emails.
     * @param email user inputted email
     * @return boolean according to whether the user exists within the DB
     */
    public boolean checkForUser(String email){
        boolean exists = false;
        try{
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "SELECT COUNT(*) FROM circuitUsers WHERE email = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                int count = resultSet.getInt(1);
                exists = count > 0;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }return exists;
    }

    /**
     * This method creates a new user within the circuitusers DB. All fields go through a regex check to ensure valid formatting
     * @param uniqueID UUID, primary key user
     * @param email email address of user
     * @param password password of the user
     * @param firstname first name of the user
     * @param lastname last name of the user
     * @param dob date of birth of user
     */
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

    /**
     * This method is used to search for a user within the circuitusers DB. This method is used particular for sign in.
     * @param userEmail user inputted email
     * @param password user inputted password
     * @return User object if the email and password match with the DB
     */
    public User queryUserByName(String userEmail, String password) {
        User currentUser = null;

        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

            String sql = "SELECT * FROM circuitusers WHERE email = ? AND password = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, userEmail);
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

            // Fix the WHERE clause to use user_id (not id)
            String query = "SELECT id, name, description, user_id as user_id_str, " +
                    "created_at, last_modified, blob_reference " +
                    "FROM projects WHERE user_id = ? ORDER BY last_modified DESC";

            System.out.println("Fetching projects for user ID: " + userId);
            statement = connection.prepareStatement(query);
            statement.setString(1, userId);

            resultSet = statement.executeQuery();
            int count = 0;

            while (resultSet.next()) {
                String projectId = resultSet.getString("id");
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
                count++;

                System.out.println("Loaded project " + count + ": " + name + " (ID: " + projectId + ")");
            }

            System.out.println("Total projects loaded: " + count);
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
            // First, ensure we can get a connection
            connection = getConnection();
            if (connection == null) {
                System.err.println("Error: Could not establish database connection");
                return false;
            }

            // Next, create a simple table to ensure the projects table exists
            try (Statement createTableStmt = connection.createStatement()) {
                String createTableSQL = "CREATE TABLE IF NOT EXISTS projects (" +
                        "id VARCHAR(200) NOT NULL PRIMARY KEY, " +
                        "name VARCHAR(100) NOT NULL, " +
                        "description TEXT, " +
                        "user_id VARCHAR(200) NOT NULL, " +
                        "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                        "last_modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                        "blob_reference VARCHAR(255) NOT NULL" +
                        ")";
                createTableStmt.executeUpdate(createTableSQL);
                System.out.println("Projects table verified or created");
            }

            // Now prepare the insert statement with simple values (no UUID conversion)
            String query = "INSERT INTO projects (id, name, description, user_id, created_at, last_modified, blob_reference) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

            System.out.println("Preparing SQL statement: " + query);
            statement = connection.prepareStatement(query);

            // Set the parameters
            statement.setString(1, project.getId());
            statement.setString(2, project.getName());
            statement.setString(3, project.getDescription());
            statement.setString(4, project.getUserId());  // No UUID conversion needed anymore
            statement.setTimestamp(5, Timestamp.valueOf(project.getCreatedAt()));
            statement.setTimestamp(6, Timestamp.valueOf(project.getLastModified()));
            statement.setString(7, project.getBlobReference());

            // Execute the query
            System.out.println("Executing insert for project ID: " + project.getId());
            int rowsAffected = statement.executeUpdate();
            success = rowsAffected > 0;
            System.out.println("Insert result: " + success + " (rows affected: " + rowsAffected + ")");
        } catch (SQLException e) {
            System.err.println("SQL Error in createProject: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error in createProject: " + e.getMessage());
            e.printStackTrace();
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

            // FIXED QUERY: Remove UUID_TO_BIN() since id is varchar in the database
            String query = "UPDATE projects SET name = ?, description = ?, last_modified = ?, blob_reference = ? " +
                    "WHERE id = ?";

            statement = connection.prepareStatement(query);

            statement.setString(1, project.getName());
            statement.setString(2, project.getDescription());
            statement.setTimestamp(3, Timestamp.valueOf(project.getLastModified()));
            statement.setString(4, project.getBlobReference());
            statement.setString(5, project.getId());  // No UUID conversion needed

            int rowsAffected = statement.executeUpdate();
            success = rowsAffected > 0;

            // If no rows were updated, the project might not exist yet
            if (rowsAffected == 0) {
                System.out.println("No rows affected, project might not exist. Trying createProject instead.");
                return createProject(project);
            }
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

            // FIXED QUERY: Remove BIN_TO_UUID for id, keep it for user_id
            String query = "SELECT id, name, description, user_id as user_id_str, " +
                    "created_at, last_modified, blob_reference " +
                    "FROM projects WHERE id = ?";

            statement = connection.prepareStatement(query);
            statement.setString(1, projectId);  // No UUID conversion needed

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

package org.example.circuit_project.User;

/**
 * Singleton class that manages the current logged-in {@link User}.
 * <p>
 * This is used throughout the application to access or update the user session context.
 */
public class UserManager {
    private static UserManager instance;
    private User currentUser;

    /**
     * Private constructor to enforce singleton pattern.
     */
    private UserManager(){
        currentUser = null;
    }

    /**
     * Returns the singleton instance of {@code UserManager}.
     * If no instance exists, it is created.
     *
     * @return The singleton {@code UserManager} instance
     */
    public static UserManager getInstance(){
        if(instance == null){
            instance = new UserManager();
        }
        return instance;
    }

    /**
     * Gets the currently logged-in user.
     *
     * @return The current {@link User}, or {@code null} if no user is logged in
     */
    public User getCurrentUser(){
        return currentUser;
    }

    /**
     * Sets the currently logged-in user.
     *
     * @param currentUser The {@link User} to set as the current user
     */
    public void setCurrentUser(User currentUser){
        this.currentUser = currentUser;
    }

}

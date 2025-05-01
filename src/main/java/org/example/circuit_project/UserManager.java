package org.example.circuit_project;

public class UserManager {
    private static UserManager instance;
    private User currentUser;

    private UserManager(){
        currentUser = null;
    }

    public static UserManager getInstance(){
        if(instance == null){
            instance = new UserManager();
        }
        return instance;
    }
    public User getCurrentUser(){
        return currentUser;
    }
    public void setCurrentUser(User currentUser){
        this.currentUser = currentUser;
    }

}

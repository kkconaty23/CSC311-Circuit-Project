package org.example.circuit_project;

/**
 * Singleton class to manage the current project being worked on
 */
public class ProjectManager {
    private static ProjectManager instance;
    private Project currentProject;

    private ProjectManager() {
        // Private constructor to prevent instantiation
    }

    /**
     * Gets the singleton instance of ProjectManager
     *
     * @return ProjectManager instance
     */
    public static ProjectManager getInstance() {
        if (instance == null) {
            instance = new ProjectManager();
        }
        return instance;
    }

    /**
     * Gets the current project being worked on
     *
     * @return Current project
     */
    public Project getCurrentProject() {
        return currentProject;
    }

    /**
     * Sets the current project being worked on
     *
     * @param project Project to set as current
     */
    public void setCurrentProject(Project project) {
        this.currentProject = project;
    }
}

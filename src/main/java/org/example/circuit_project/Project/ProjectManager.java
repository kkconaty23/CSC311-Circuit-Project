package org.example.circuit_project.Project;

/**
 * Singleton class to manage the currently active {@link Project} in the circuit editor.
 * <p>
 * This ensures only one project is "open" at a time and provides a centralized way
 * to access or modify the active project across different parts of the application.
 */
public class ProjectManager {
    private static ProjectManager instance;
    private Project currentProject;

    /**
     * Private constructor to prevent external instantiation.
     */
    private ProjectManager() {
        // Private constructor to prevent instantiation
    }

    /**
     * Retrieves the singleton instance of {@code ProjectManager}.
     * If the instance does not exist, it is created.
     *
     * @return The singleton {@code ProjectManager} instance
     */
    public static ProjectManager getInstance() {
        if (instance == null) {
            instance = new ProjectManager();
        }
        return instance;
    }

    /**
     * Returns the currently active project.
     *
     * @return The current {@link Project}, or {@code null} if none is set
     */
    public Project getCurrentProject() {
        return currentProject;
    }

    /**
     * Sets the specified project as the current active project.
     *
     * @param project The {@link Project} to set as active
     */
    public void setCurrentProject(Project project) {
        this.currentProject = project;
    }
}

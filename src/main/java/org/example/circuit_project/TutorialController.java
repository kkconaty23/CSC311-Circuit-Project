package org.example.circuit_project;

public class TutorialController extends SandboxController{

    // TODO
    // Make modals to walk user through each part of the sandbox
    // Button on each modal to move to next modal
    // Button to move to new sand box.

    // Inner class for modal
    public class TutorialModal {
        String title;
        String description;

        TutorialModal(String title, String description) {
            this.title = title;
            this.description = description;
        }
    }

}

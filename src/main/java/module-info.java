module org.example.circuit_project {
    requires javafx.controls;
    requires javafx.fxml;
    requires firebase.admin;
    requires com.google.auth.oauth2;
    requires com.google.auth;
    requires google.cloud.firestore;


    opens org.example.circuit_project to javafx.fxml;
    exports org.example.circuit_project;
}
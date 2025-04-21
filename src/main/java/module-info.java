module org.example.circuit_project {
    requires javafx.controls;
    requires javafx.fxml;
//    requires firebase.admin;
//    requires com.google.auth;
//    requires google.cloud.firestore;
    requires java.sql;


    opens org.example.circuit_project to javafx.fxml;
    exports org.example.circuit_project;
}
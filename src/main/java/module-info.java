module org.example.circuit_project {
    requires javafx.controls;
    requires javafx.fxml;
    requires firebase.admin;
    requires com.google.auth;
    requires google.cloud.firestore;
    requires jakarta.xml.bind;
    requires org.apache.httpcomponents.httpclient;

    exports org.example.circuit_project;

    opens org.example.circuit_project to javafx.fxml;
    opens org.example.circuit_project.Elements to jakarta.xml.bind;  // Add this line
}
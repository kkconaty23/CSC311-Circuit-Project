module org.example.circuit_project {
    requires javafx.controls;
    requires javafx.fxml;
//    requires firebase.admin;
//    requires com.google.auth;
//    requires google.cloud.firestore;
//    requires jakarta.xml.bind;
//    requires org.apache.httpcomponents.httpclient;
    requires java.sql;
    requires mysql.connector.j;
    requires com.azure.storage.blob;
    requires com.azure.identity;
    requires com.google.gson;


    exports org.example.circuit_project;
    opens org.example.circuit_project to javafx.fxml;
    exports org.example.circuit_project.Storage;
    opens org.example.circuit_project.Storage to javafx.fxml;




    exports org.example.circuit_project.Components;
    opens org.example.circuit_project.Components to javafx.fxml;


}
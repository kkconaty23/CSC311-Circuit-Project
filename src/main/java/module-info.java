module org.example.circuit_project {
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.xml.bind;
    requires java.sql;
    requires mysql.connector.j;
    requires com.google.gson;

    opens org.example.circuit_project to javafx.fxml, com.google.gson;
    exports org.example.circuit_project;

    exports org.example.circuit_project.Components;
    opens org.example.circuit_project.Components to javafx.fxml;
}

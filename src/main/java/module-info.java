module hellofx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires java.logging;
    requires org.slf4j;
    requires java.desktop;

    opens com.example to javafx.fxml;
    opens com.example.domain to com.fasterxml.jackson.databind;
    exports com.example;
    exports com.example.domain;
    exports com.example.client;
}
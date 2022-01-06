module com.example.blind_test {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires org.slf4j;
    requires com.google.gson;
    requires org.junit.jupiter.api;
    requires org.assertj.core;
    requires java.sql;
    opens com.example.blind_test to javafx.fxml;
    exports com.example.blind_test;
    exports com.example.blind_test.front.controllers;
    opens com.example.blind_test.front.controllers to javafx.fxml;
}

module io.github.edynut.rpgsheetmanager {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.fasterxml.jackson.databind;

    exports io.github.edynut.rpgsheetmanager.core;
    opens io.github.edynut.rpgsheetmanager.core to javafx.fxml;
    exports io.github.edynut.rpgsheetmanager.model;
    opens io.github.edynut.rpgsheetmanager.model to javafx.fxml, com.fasterxml.jackson.databind;
    exports io.github.edynut.rpgsheetmanager.repository;
    opens io.github.edynut.rpgsheetmanager.repository to javafx.fxml;
}
module io.github.edynut.rpgsheetmanager {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    opens io.github.edynut.rpgsheetmanager to javafx.fxml;
    exports io.github.edynut.rpgsheetmanager;
    exports io.github.edynut.rpgsheetmanager.core;
    opens io.github.edynut.rpgsheetmanager.core to javafx.fxml;
}
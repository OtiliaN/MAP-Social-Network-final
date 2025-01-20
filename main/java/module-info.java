module org.example.lab6javafx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires jdk.jshell;

    opens org.example.lab6javafx to javafx.fxml;
    exports org.example.lab6javafx;
}
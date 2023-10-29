module ru.rsatu.dbkursach {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.sql;
    requires java.base;
    requires lombok;
    requires aspose.cells;

    opens ru.rsatu.dbkursach to javafx.fxml;
    exports ru.rsatu.dbkursach;
    opens ru.rsatu.dbkursach.controller to javafx.fxml;
    exports ru.rsatu.dbkursach.controller;
    opens ru.rsatu.dbkursach.db.entity to javafx.base;
}
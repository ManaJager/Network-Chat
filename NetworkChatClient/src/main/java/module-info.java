module ru.gb.java2.kochemasov {
    requires javafx.controls;
    requires javafx.fxml;

    opens ru.gb.java2.kochemasov to javafx.fxml;
    exports ru.gb.java2.kochemasov;
    exports ru.gb.java2.kochemasov.controllers;
    opens ru.gb.java2.kochemasov.controllers to javafx.fxml;
}
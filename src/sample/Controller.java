package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class Controller {
    @FXML
    public TextArea textArea;
    @FXML
    public TextField textField;

    @FXML
    public void Send(ActionEvent actionEvent) {
        textArea.setEditable(false);
        textArea.appendText(textField.getText() + "\n");
        textField.clear();
        textField.requestFocus();
    }

    public void Clear(ActionEvent actionEvent) {
        textField.clear();
    }
}
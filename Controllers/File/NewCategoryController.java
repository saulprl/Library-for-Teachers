package Controllers.File;

import Models.ConnectionMethods;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.SQLException;

public class NewCategoryController {

    private static String query;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField descTextField;

    @FXML
    private Button registerButton;

    @FXML
    private Button cancelButton;

    @FXML
    private GridPane gridPane;

    @FXML
    private void registerButtonPressed(ActionEvent event) {
        String name = nameTextField.getText();
        String desc = descTextField.getText();

        if (!(name.isEmpty() && desc.isEmpty())) {
            query = String.format("INSERT INTO categories VALUES('%s', '%s');", name, desc);

            try {
                ConnectionMethods.executeUpdate(query);

                Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                dialog.setTitle("Registrar categoría");
                dialog.setHeaderText(null);
                dialog.setContentText("Se ha insertado la categoría correctamente.");
                dialog.showAndWait();
            } catch (SQLException sqlEx) {
                Alert dialog = new Alert(Alert.AlertType.ERROR);
                dialog.setTitle("Error al insertar");
                dialog.setHeaderText(null);
                dialog.setContentText("Ha ocurrido un error al insertar la categoría. Excepción: " + sqlEx.getMessage());
                dialog.showAndWait();
            }
        } else {
            Alert dialog = new Alert(Alert.AlertType.INFORMATION);
            dialog.setTitle("Parámetros de registro");
            dialog.setHeaderText(null);
            dialog.setContentText("Asegúrate de haber ingresado todos los parámetros de registro.");
            dialog.showAndWait();
        }
    }

    @FXML
    private void cancelButtonPressed(ActionEvent event) {
        Stage stage = (Stage) (gridPane.getScene().getWindow());
        stage.close();
    }

    @FXML
    private void initialize() {
        nameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[A-Za-z0-9 ]{0,20}")) {
                nameTextField.setText(oldValue);
            }
        });

        descTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[A-Za-z0-9.(),: ]{0,50}")) {
                descTextField.setText(oldValue);
            }
        });
    }

}

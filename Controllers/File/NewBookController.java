package Controllers.File;

import Models.ConnectionMethods;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class NewBookController {

    private static String query;

    @FXML
    private TextField isbnTextField;

    @FXML
    private TextField titleTextField;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Button registerButton;

    @FXML
    private Button cancelButton;

    @FXML
    private GridPane gridPane;

    @FXML
    private void registerButtonPressed(ActionEvent event) {
        String isbn = isbnTextField.getText();
        String title = titleTextField.getText();
        LocalDate date = datePicker.getValue();

        if (!(isbn.isEmpty() && title.isEmpty() && date == null)) {
            query = String.format("INSERT INTO books VALUES('%s', '%s', '%s');", isbn, title, date);

            try {
                ConnectionMethods.executeUpdate(query);

                Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                dialog.setTitle("Registrar libro");
                dialog.setHeaderText(null);
                dialog.setContentText("Se ha insertado el libro correctamente.");
                dialog.showAndWait();
            } catch (SQLException sqlEx) {
                Alert dialog = new Alert(Alert.AlertType.ERROR);
                dialog.setTitle("Error al insertar");
                dialog.setHeaderText(null);
                dialog.setContentText("Ha ocurrido un error al insertar el libro. Excepción: " + sqlEx.getMessage());
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
        datePicker = new DatePicker();
        gridPane.add(datePicker, 1, 2);
        datePicker.setPrefWidth(isbnTextField.getPrefWidth());

        isbnTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[0-9 -]{0,20}")) {
                isbnTextField.setText(oldValue);
            }
        });

        titleTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[A-Za-z ,.0-9()]{0,30}")) {
                titleTextField.setText(oldValue);
            }
        });

        datePicker.setConverter(new StringConverter<>() {
            private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            @Override
            public String toString(LocalDate localDate) {
                if (localDate == null) return "";
                return formatter.format(localDate);
            }

            @Override
            public LocalDate fromString(String dateString) {
                if (dateString == null || dateString.trim().isEmpty()) return null;
                return LocalDate.parse(dateString, formatter);
            }
        });
    }

}

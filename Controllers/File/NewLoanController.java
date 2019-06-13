package Controllers.File;

import Models.ConnectionMethods;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.SQLException;

public class NewLoanController {

    private static final String TEACHERSQUERY = "SELECT first_name, last_name FROM teachers;";
    private static final String BOOKSQUERY = "SELECT book_title FROM books;";

    @FXML
    private ComboBox nameComboBox;

    @FXML
    private ComboBox titleComboBox;

    @FXML
    private GridPane gridPane;

    @FXML
    private Button registerButton;

    @FXML
    private Button cancelButton;

    @FXML
    private void registerButtonPressed(ActionEvent event) {
        if (nameComboBox.getValue() != null && titleComboBox.getValue() != null) {
            int id = getTeacherId(nameComboBox.getValue().toString());
            String isbn = getBookIsbn(titleComboBox.getValue().toString());
            String query = String.format("INSERT INTO books_on_loan(teacher_id, isbn) VALUES(%s, '%s');",
                    id, isbn);

            try {
                ConnectionMethods.executeUpdate(query);

                Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                dialog.setTitle("Préstamo registrado");
                dialog.setHeaderText(null);
                dialog.setContentText("El préstamo se ha registrado correctamente.");
                dialog.showAndWait();
            } catch (SQLException sqlEx) {
                Alert dialog = new Alert(Alert.AlertType.ERROR);
                dialog.setTitle("Error");
                dialog.setHeaderText(null);
                dialog.setContentText("Ha ocurrido un error al registrar el préstamo. Excepción: " + sqlEx.getMessage());
                dialog.showAndWait();
            }
        } else {
            Alert dialog = new Alert(Alert.AlertType.INFORMATION);
            dialog.setTitle("Parámetros de entrada");
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
        updateData();
    }

    private void updateData() {
        for (Node node : gridPane.getChildren()) {
            if (node instanceof ComboBox) {
                ((ComboBox) node).getItems().clear();
            }
        }

        try {
            ResultSet teachersSet = ConnectionMethods.executeQuery(TEACHERSQUERY);
            ResultSet booksSet = ConnectionMethods.executeQuery(BOOKSQUERY);

            while (teachersSet.next()) {
                String fullName = String.format("%s %s", teachersSet.getString("first_name"),
                        teachersSet.getString("last_name"));
                nameComboBox.getItems().add(fullName);
            }

            while (booksSet.next()) {
                titleComboBox.getItems().add(booksSet.getString("book_title"));
            }

            ConnectionMethods.close();
        } catch (SQLException sqlEx) {
            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error");
            dialog.setHeaderText(null);
            dialog.setContentText("Ha ocurrido un error de conexión. Excepción: " + sqlEx.getMessage());
            dialog.showAndWait();
        }
    }

    private int getTeacherId(String fullName) {
        String firstName = fullName.split("\\s")[0];
        String lastName = fullName.split("\\s")[1];
        String sql = String.format("SELECT teacher_id FROM teachers WHERE first_name = '%s' AND last_name = '%s';",
                firstName, lastName);
        int id = 0;

        try {
            ResultSet teachers = ConnectionMethods.executeQuery(sql);

            teachers.next();
            id = teachers.getInt("teacher_id");

            ConnectionMethods.close();
        } catch (SQLException sqlEx) {
            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error de búsqueda");
            dialog.setHeaderText(null);
            dialog.setContentText("Ha ocurrido un error al buscar el ID del profesor. Excepción: " + sqlEx.getMessage());
            dialog.showAndWait();
        }

        return id;
    }

    private String getBookIsbn(String title) {
        String sql = String.format("SELECT isbn FROM books WHERE book_title = '%s';", title);
        String isbn = "";

        try {
            ResultSet books = ConnectionMethods.executeQuery(sql);

            books.next();
            isbn = books.getString("isbn");
        } catch (SQLException sqlEx) {
            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error de búsqueda");
            dialog.setHeaderText(null);
            dialog.setContentText("Ha ocurrido un error al buscar el ISBN del libro. Excepción: " + sqlEx.getMessage());
            dialog.showAndWait();
        }

        return isbn;
    }

}
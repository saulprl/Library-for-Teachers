package Controllers.TableManagement;

import Models.Author;
import Models.ConnectionMethods;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AuthorsTableController {

    private static Author selectedAuthor;
    private static final String INITIALIZEQUERY = "SELECT * FROM authors;";
    private static String query;

    @FXML
    private ComboBox idComboBox;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField surTextField;

    @FXML
    private Button registerButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button clearButton;

    @FXML
    private TableView tableView;

    @FXML
    private GridPane gridPane;

    @FXML
    private void registerButtonPressed(ActionEvent event) {
        if (!updateButton.getText().equals("Guardar")) {
            if (!nameTextField.getText().isEmpty() && !surTextField.getText().isEmpty()) {
                String first = nameTextField.getText();
                String last = surTextField.getText();

                query = String.format("INSERT INTO authors(author_firstname, author_surname) " +
                        "VALUES('%s', '%s');", first, last);

                try {
                    ConnectionMethods.executeUpdate(query);

                    updateData((query = INITIALIZEQUERY));

                    Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                    dialog.setTitle("Registro insertado");
                    dialog.setHeaderText(null);
                    dialog.setContentText("El autor se ha insertado correctamente.");
                    dialog.showAndWait();
                } catch (SQLException sqlEx) {
                    Alert dialog = new Alert(Alert.AlertType.ERROR);
                    dialog.setTitle("Error al insertar");
                    dialog.setHeaderText(null);
                    dialog.setContentText("Ha ocurrido un error al insertar el autor. Excepción: " + sqlEx.getMessage());
                    dialog.showAndWait();
                }
            } else {
                Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                dialog.setTitle("Parámetros de entrada");
                dialog.setHeaderText(null);
                dialog.setContentText("El campo del nombre del autor debe tener un espacio entre el nombre y el apellido.");
                dialog.showAndWait();
            }
        } else {
            Alert dialog = new Alert(Alert.AlertType.INFORMATION);
            dialog.setTitle("Acción no disponible");
            dialog.setHeaderText(null);
            dialog.setContentText("No puedes realizar un registro ahora mismo. Presiona el botón 'Cancelar'.");
            dialog.showAndWait();
        }
    }

    @FXML
    private void updateButtonPressed(ActionEvent event) {
        if (updateButton.getText().equals("Modificar")) {
            nameTextField.setText(nameTextField.getPromptText());
            surTextField.setText(surTextField.getPromptText());

            registerButton.setDisable(true);
            deleteButton.setDisable(true);
            updateButton.setText("Guardar");
            clearButton.setText("Cancelar");
        } else if (updateButton.getText().equals("Guardar")) {
            String name = nameTextField.getText();
            String surname = surTextField.getText();

            query = String.format("UPDATE authors SET author_firstname = '%s', author_surname = '%s' " +
                    "WHERE author_id = %d;", name, surname, selectedAuthor.author_idProperty().getValue());

            try {
                ConnectionMethods.executeUpdate(query);

                Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                dialog.setTitle("Modificar registro");
                dialog.setHeaderText(null);
                dialog.setContentText("La información del autor ha sido modificada correctamente.");
                dialog.showAndWait();

                clearButtonPressed(new ActionEvent());
            } catch (SQLException sqlEx) {
                Alert dialog = new Alert(Alert.AlertType.ERROR);
                dialog.setTitle("Error al modificar");
                dialog.setHeaderText(null);
                dialog.setContentText("Ha ocurrido un error al actualizar. Excepción: " + sqlEx.getMessage());
                dialog.showAndWait();
            }
        }
    }

    @FXML
    private void deleteButtonPressed(ActionEvent event) {
        if (!updateButton.getText().equals("Guardar")) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Borrar registro");
            confirmation.setHeaderText(null);
            confirmation.setContentText("¿Seguro de que deseas eliminar a este autor?");
            confirmation.showAndWait();

            if (confirmation.getResult() == ButtonType.OK) {
                if (selectedAuthor != null) {
                    try {
                        query = String.format("DELETE FROM authors WHERE author_id = %s;",
                                selectedAuthor.author_idProperty().getValue());

                        ConnectionMethods.executeUpdate(query);

                        updateData((query = INITIALIZEQUERY));

                        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                        dialog.setTitle("Borrar registro");
                        dialog.setHeaderText(null);
                        dialog.setContentText("El autor se ha eliminado de la tabla correctamente.");
                        dialog.showAndWait();
                    } catch (SQLException sqlEx) {
                        Alert dialog = new Alert(Alert.AlertType.ERROR);
                        dialog.setTitle("Error al borrar");
                        dialog.setHeaderText(null);
                        dialog.setContentText("Ha ocurrido un error al eliminar el autor. Excepción: " + sqlEx.getMessage());
                        dialog.showAndWait();
                    }
                } else {
                    Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                    dialog.setTitle("Selección nula");
                    dialog.setHeaderText(null);
                    dialog.setContentText("Debes seleccionar un objeto en la tabla para poder borrarlo.");
                    dialog.showAndWait();
                }
            } else {
                Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                dialog.setTitle("Borrar registro");
                dialog.setHeaderText(null);
                dialog.setContentText("No se efetuó ningún cambio.");
                dialog.showAndWait();
            }
        } else {
            Alert dialog = new Alert(Alert.AlertType.INFORMATION);
            dialog.setTitle("Acción no disponible");
            dialog.setHeaderText(null);
            dialog.setContentText("No puedes eliminar un registro ahora mismo. Presiona el botón 'Cancelar'.");
            dialog.showAndWait();
        }
    }

    @FXML
    private void clearButtonPressed(ActionEvent event) {
        updateData((query = INITIALIZEQUERY));

        for (Node node : gridPane.getChildren()) {
            if (node instanceof TextField) {
                ((TextField) node).clear();
                ((TextField) node).setPromptText(((TextField) node).getText());
            } else if (node instanceof ComboBox) {
                ((ComboBox) node).getSelectionModel().select(null);
            } else if (node instanceof Button) {
                switch (((Button) node).getText()) {
                    case "Guardar":
                        ((Button) node).setText("Modificar");
                        break;
                    case "Cancelar":
                        ((Button) node).setText("Limpiar");
                        break;
                }

                if (!node.getId().contains("clear")) {
                    node.setDisable(true);
                }
            }
        }
    }

    @FXML
    private void initialize() {
        updateData((query = INITIALIZEQUERY));

        registerButton.setDisable(true);
        deleteButton.setDisable(true);

        nameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && !surTextField.getText().isEmpty()) {
                registerButton.setDisable(false);
            } else {
                registerButton.setDisable(true);
            }

            if (!newValue.matches("\\w{0,20}")) {
                nameTextField.setText(oldValue);
            }
        });

        surTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && !surTextField.getText().isEmpty()) {
                registerButton.setDisable(false);
            } else {
                registerButton.setDisable(true);
            }

            if (!newValue.matches("\\w{0,20}")) {
                surTextField.setText(oldValue);
            }
        });

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedAuthor = (Author) (newValue);
                deleteButton.setDisable(false);

                idComboBox.getSelectionModel().select(selectedAuthor.author_idProperty().getValue());
                nameTextField.setPromptText(selectedAuthor.author_firstnameProperty().getValue());
                surTextField.setPromptText(selectedAuthor.author_surnameProperty().getValue());
            } else {
                selectedAuthor = null;
                deleteButton.setDisable(true);

                idComboBox.getSelectionModel().select(null);
                nameTextField.setPromptText(nameTextField.getText());
            }
        });
    }

    private void updateData(String sql) {
        tableView.getItems().clear();
        tableView.getColumns().clear();
        idComboBox.getItems().clear();

        try {
            ResultSet resultSet = ConnectionMethods.executeQuery(sql);

            if (resultSet != null) {
                while (resultSet.next()) {
                    idComboBox.getItems().add(resultSet.getInt("author_id"));
                }
                resultSet.beforeFirst();

                ObservableList data = FXCollections.observableArrayList(dataBaseArrayList(resultSet));

                for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                    TableColumn column = new TableColumn();

                    switch (resultSet.getMetaData().getColumnName(i + 1)) {
                        case "author_id":
                            column.setText("ID");
                            break;
                        case "author_firstname":
                            column.setText("Nombre");
                            break;
                        case "author_surname":
                            column.setText("Apellido");
                            break;
                        default:
                            column.setText(resultSet.getMetaData().getColumnName(i + 1));
                    }

                    column.setCellValueFactory(new PropertyValueFactory<>(resultSet.getMetaData().getColumnName(i + 1)));
                    tableView.getColumns().add(column);
                }

                tableView.setItems(data);
            }
        } catch (SQLException sqlEx) {
            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error");
            dialog.setHeaderText(null);
            dialog.setContentText("Ha ocurrido un error de conexión. Excepción: " + sqlEx.getMessage());
            dialog.showAndWait();
        }
    }

    private ArrayList<Author> dataBaseArrayList(ResultSet resultSet) throws SQLException {
        ArrayList<Author> data = new ArrayList<>();

        while (resultSet.next()) {
            Author author = new Author();

            author.author_id.setValue(resultSet.getInt("author_id"));
            author.author_firstname.setValue(resultSet.getString("author_firstname"));
            author.author_surname.setValue(resultSet.getString("author_surname"));

            data.add(author);
        }

        return data;
    }

}

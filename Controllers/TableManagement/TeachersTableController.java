package Controllers.TableManagement;

import Models.ConnectionMethods;
import Models.Teacher;
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

public class TeachersTableController {

    private static Teacher selectedTeacher;
    private static final String INITIALIZEQUERY = "SELECT * FROM teachers;";
    private static String query;

    @FXML
    private ComboBox idComboBox;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField surTextField;

    @FXML
    private TextField addressTextField;

    @FXML
    private TextField phoneTextField;

    @FXML
    private TextField emailTextField;

    @FXML
    private GridPane gridPane;

    @FXML
    private TableView tableView;

    @FXML
    private Button registerButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button clearButton;

    @FXML
    private void registerButtonPressed(ActionEvent event) {
        String name = nameTextField.getText();
        String surn = surTextField.getText();
        String addr = addressTextField.getText();
        String phone = phoneTextField.getText();
        String email = emailTextField.getText();

        if (!updateButton.getText().equals("Guardar")) {
            if (!(name.isEmpty() && surn.isEmpty() && addr.isEmpty() && phone.isEmpty() && email.isEmpty())) {
                query = String.format("INSERT INTO teachers(first_name, last_name, teacher_address, phone_number, " +
                        "email_address) VALUES('%s', '%s', '%s', '%s', '%s');", name, surn, addr, phone, email);

                try {
                    ConnectionMethods.executeUpdate(query);

                    Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                    dialog.setTitle("Registro insertado");
                    dialog.setHeaderText(null);
                    dialog.setContentText("El profesor se ha agregado a la tabla correctamente.");
                    dialog.showAndWait();

                    clearButtonPressed(new ActionEvent());
                } catch (SQLException sqlEx) {
                    Alert dialog = new Alert(Alert.AlertType.ERROR);
                    dialog.setTitle("Error al insertar");
                    dialog.setHeaderText(null);
                    dialog.setContentText("Ha ocurrido un error al insertar el profesor. Excepción: " +
                            sqlEx.getMessage());
                    dialog.showAndWait();
                }
            } else {
                Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                dialog.setTitle("Parámetros de registro");
                dialog.setHeaderText(null);
                dialog.setContentText("Asegúrate de haber llenado todos los campos de registro.");
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
            addressTextField.setText(addressTextField.getPromptText());
            phoneTextField.setText(phoneTextField.getPromptText());
            emailTextField.setText(emailTextField.getPromptText());

            registerButton.setDisable(true);
            deleteButton.setDisable(true);
            updateButton.setText("Guardar");
            clearButton.setText("Cancelar");
        } else if (updateButton.getText().equals("Guardar")) {
            String name = nameTextField.getText();
            String surn = surTextField.getText();
            String address = addressTextField.getText();
            String phone = phoneTextField.getText();
            String email = emailTextField.getText();

            query = String.format("UPDATE teachers SET first_name = '%s', last_name = '%s', teacher_address = '%s', " +
                    "phone_number = '%s', email_address = '%s' WHERE teacher_id = %d;", name, surn, address, phone,
                    email, selectedTeacher.teacher_idProperty().getValue());

            try {
                ConnectionMethods.executeUpdate(query);

                Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                dialog.setTitle("Modificar registro");
                dialog.setHeaderText(null);
                dialog.setContentText("La información del profesor ha sido modificada correctamente.");
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
            if (selectedTeacher != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Borrar registro");
                confirm.setHeaderText(null);
                confirm.setContentText("¿Seguro de que deseas eliminar este profesor?");
                confirm.showAndWait();

                if (confirm.getResult() == ButtonType.OK) {
                    query = String.format("DELETE FROM teachers WHERE teacher_id = %d;",
                            selectedTeacher.teacher_idProperty().getValue());

                    try {
                        ConnectionMethods.executeUpdate(query);

                        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                        dialog.setTitle("Borrar registro");
                        dialog.setHeaderText(null);
                        dialog.setContentText("El profesor se ha eliminado de la tabla correctamente.");
                        dialog.showAndWait();

                        clearButtonPressed(new ActionEvent());
                    } catch (SQLException sqlEx) {
                        Alert dialog = new Alert(Alert.AlertType.ERROR);
                        dialog.setTitle("Error al borrar");
                        dialog.setHeaderText(null);
                        dialog.setContentText("Ha ocurrido un error al eliminar al profesor. Excepción: "
                                + sqlEx.getMessage());
                        dialog.showAndWait();
                    }
                } else {
                    Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                    dialog.setTitle("Borrar registro");
                    dialog.setHeaderText(null);
                    dialog.setContentText("No se ha efectuado ningún cambio.");
                    dialog.showAndWait();
                }
            } else {
                Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                dialog.setTitle("Selección nula");
                dialog.setHeaderText(null);
                dialog.setContentText("Debes seleccionar un objeto de la tabla para eliminarlo.");
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
                    case "Cancelar":
                        ((Button) node).setText("Limpiar");
                        break;
                    case "Guardar":
                        ((Button) node).setText("Modificar");
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
        updateButton.setDisable(true);
        deleteButton.setDisable(true);

        nameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!(newValue.isEmpty() && surTextField.getText().isEmpty() && addressTextField.getText().isEmpty() &&
                    phoneTextField.getText().isEmpty() && emailTextField.getText().isEmpty())) {
                registerButton.setDisable(false);
            } else {
                registerButton.setDisable(true);
            }

            if (newValue.length() > 20) {
                nameTextField.setText(oldValue);
            }
        });

        surTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!(newValue.isEmpty() && nameTextField.getText().isEmpty() && addressTextField.getText().isEmpty() &&
                    phoneTextField.getText().isEmpty() && emailTextField.getText().isEmpty())) {
                registerButton.setDisable(false);
            } else {
                registerButton.setDisable(true);
            }

            if (newValue.length() > 20) {
                surTextField.setText(oldValue);
            }
        });

        addressTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!(newValue.isEmpty() && nameTextField.getText().isEmpty() && surTextField.getText().isEmpty() &&
                    phoneTextField.getText().isEmpty() && emailTextField.getText().isEmpty())) {
                registerButton.setDisable(false);
            } else {
                registerButton.setDisable(true);
            }

            if (newValue.length() > 50) {
                addressTextField.setText(oldValue);
            }
        });

        phoneTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!(newValue.isEmpty() && nameTextField.getText().isEmpty() && surTextField.getText().isEmpty() &&
                    addressTextField.getText().isEmpty() && emailTextField.getText().isEmpty())) {
                registerButton.setDisable(false);
            } else {
                registerButton.setDisable(true);
            }

            if (!newValue.matches("\\d{0,10}")) {
                phoneTextField.setText(oldValue);
            }
        });

        emailTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!(newValue.isEmpty() && nameTextField.getText().isEmpty() && surTextField.getText().isEmpty() &&
                    addressTextField.getText().isEmpty() && phoneTextField.getText().isEmpty())) {
                registerButton.setDisable(false);
            } else {
                registerButton.setDisable(true);
            }
        });

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedTeacher = (Teacher) (newValue);
                updateButton.setDisable(false);
                deleteButton.setDisable(false);

                idComboBox.getSelectionModel().select(selectedTeacher.teacher_idProperty().getValue());
                nameTextField.setPromptText(selectedTeacher.first_nameProperty().getValue());
                surTextField.setPromptText(selectedTeacher.last_nameProperty().getValue());
                addressTextField.setPromptText(selectedTeacher.teacher_addressProperty().getValue());
                phoneTextField.setPromptText(selectedTeacher.phone_numberProperty().getValue());
                emailTextField.setPromptText(selectedTeacher.email_addressProperty().getValue());
            } else {
                selectedTeacher = null;
                updateButton.setDisable(true);
                deleteButton.setDisable(true);
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
                    idComboBox.getItems().add(resultSet.getInt("teacher_id"));
                }
                resultSet.beforeFirst();

                ObservableList data = FXCollections.observableArrayList(dataBaseArrayList(resultSet));

                for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                    TableColumn column = new TableColumn();

                    switch (resultSet.getMetaData().getColumnName(i + 1)) {
                        case "teacher_id":
                            column.setText("ID");
                            break;
                        case "first_name":
                            column.setText("Nombre");
                            break;
                        case "last_name":
                            column.setText("Apellido");
                            break;
                        case "teacher_address":
                            column.setText("Dirección");
                            break;
                        case "phone_number":
                            column.setText("Teléfono");
                            break;
                        case "email_address":
                            column.setText("Correo electrónico");
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

    private ArrayList<Teacher> dataBaseArrayList(ResultSet resultSet) throws SQLException {
        ArrayList<Teacher> data = new ArrayList<>();

        while (resultSet.next()) {
            Teacher teacher = new Teacher();

            teacher.teacher_id.setValue(resultSet.getInt("teacher_id"));
            teacher.first_name.setValue(resultSet.getString("first_name"));
            teacher.last_name.setValue(resultSet.getString("last_name"));
            teacher.teacher_address.setValue(resultSet.getString("teacher_address"));
            teacher.phone_number.setValue(resultSet.getString("phone_number"));
            teacher.email_address.setValue(resultSet.getString("email_address"));

            data.add(teacher);
        }

        return data;
    }

}

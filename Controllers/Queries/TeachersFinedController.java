package Controllers.Queries;

import Models.ConnectionMethods;
import Models.TeachersFinedViewModel;
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

public class TeachersFinedController {

    private static final String INITIALIZEQUERY = "SELECT * FROM fined_teachers_view;";
    private static String query;

    @FXML
    private ComboBox teacherComboBox;

    @FXML
    private TextField teacherTextField;

    @FXML
    private TextField phoneTextField;

    @FXML
    private TextField fineTextField;

    @FXML
    private Button fetchButton;

    @FXML
    private Button clearButton;

    @FXML
    private GridPane gridPane;

    @FXML
    private TableView tableView;

    @FXML
    private void fetchButtonPressed(ActionEvent event) {
        if (!(teacherComboBox.getValue() == null && teacherTextField.getText().isEmpty() &&
                phoneTextField.getText().isEmpty() && fineTextField.getText().isEmpty())) {
            String field = "";
            String param = "";
            boolean combo = false;

            for (Node node : gridPane.getChildren()) {
                if (node instanceof TextField && !node.isDisabled()) {
                    if (node.getId().contains("teacher")) {
                        field = "full_name";
                        param = teacherTextField.getText();
                        break;
                    } else if (node.getId().contains("phone")) {
                        field = "phone_number";
                        param = phoneTextField.getText();
                        break;
                    } else {
                        field = "fine";
                        param = fineTextField.getText();
                        break;
                    }
                } else if (node instanceof ComboBox && !node.isDisabled()) {
                    field = "teacher_id";
                    param = teacherComboBox.getValue().toString();
                    combo = true;
                    break;
                }
            }

            if (!combo) {
                query = String.format("SELECT * FROM fined_teachers_view WHERE %s LIKE '%s%s%s';", field,
                        "%", param, "%");
            } else {
                query = String.format("SELECT * FROM fined_teachers_view WHERE %s = %s;", field, param);
            }

            updateData(query);
        } else {
            Alert dialog = new Alert(Alert.AlertType.INFORMATION);
            dialog.setTitle("Parámetros de búsqueda");
            dialog.setHeaderText(null);
            dialog.setContentText("Asegúrate de haber ingresado un parámetro de búsqueda.");
            dialog.showAndWait();
        }
    }

    @FXML
    private void clearButtonPressed(ActionEvent event) {
        updateData((query = INITIALIZEQUERY));

        for (Node node : gridPane.getChildren()) {
            node.setDisable(false);

            if (node instanceof TextField) {
                ((TextField) node).clear();
            } else if (node instanceof ComboBox) {
                ((ComboBox) node).setValue(null);
            }
        }
    }

    @FXML
    private void initialize() {
        query = INITIALIZEQUERY;
        updateData(query);

        teacherComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                for (Node node : gridPane.getChildren()) {
                    if (node instanceof TextField) {
                        node.setDisable(true);
                    }
                }
            } else {
                for (Node node : gridPane.getChildren()) {
                    if (node instanceof TextField) {
                        node.setDisable(false);
                    }
                }
            }
        });

        teacherTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                for (Node node : gridPane.getChildren()) {
                    if ((node instanceof TextField || node instanceof ComboBox) && node != teacherTextField) {
                        node.setDisable(true);
                    }
                }

                if (!newValue.matches("\\w{0,20}([ ]\\w{0,20})?")) {
                    teacherTextField.setText(oldValue);
                }
            } else {
                for (Node node : gridPane.getChildren()) {
                    if ((node instanceof TextField || node instanceof ComboBox) && node != teacherTextField) {
                        node.setDisable(false);
                    }
                }
            }
        });

        phoneTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                for (Node node : gridPane.getChildren()) {
                    if ((node instanceof TextField || node instanceof ComboBox) && node != phoneTextField) {
                        node.setDisable(true);
                    }
                }

                if (!newValue.matches("\\d{0,10}")) {
                    phoneTextField.setText(oldValue);
                }
            } else {
                for (Node node : gridPane.getChildren()) {
                    if ((node instanceof TextField || node instanceof ComboBox) && node != phoneTextField) {
                        node.setDisable(false);
                    }
                }
            }
        });

        fineTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                for (Node node : gridPane.getChildren()) {
                    if ((node instanceof TextField || node instanceof ComboBox) && node != fineTextField) {
                        node.setDisable(true);
                    }
                }

                if (!newValue.matches("\\d{0,4}([.]\\d{0,2})?")) {
                    fineTextField.setText(oldValue);
                }
            } else {
                for (Node node : gridPane.getChildren()) {
                    if ((node instanceof TextField || node instanceof ComboBox) && node != fineTextField) {
                        node.setDisable(false);
                    }
                }
            }
        });
    }

    private void updateData(String sql) {
        tableView.getItems().clear();
        tableView.getColumns().clear();
        teacherComboBox.getItems().clear();

        try {
            ResultSet resultSet = ConnectionMethods.executeQuery(sql);

            if (resultSet != null) {
                while (resultSet.next()) {
                    teacherComboBox.getItems().add(resultSet.getInt("teacher_id"));
                }

                resultSet.beforeFirst();
                ObservableList data = FXCollections.observableArrayList(dataBaseArrayList(resultSet));

                for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                    TableColumn column = new TableColumn();

                    switch (resultSet.getMetaData().getColumnName(i + 1)) {
                        case "teacher_id":
                            column.setText("ID");
                            break;
                        case "full_name":
                            column.setText("Profesor");
                            break;
                        case "phone_number":
                            column.setText("Teléfono");
                            break;
                        case "email_address":
                            column.setText("Correo electrónico");
                            break;
                        case "fine":
                            column.setText("Multa");
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

    private ArrayList<TeachersFinedViewModel> dataBaseArrayList(ResultSet resultSet) throws SQLException {
        ArrayList<TeachersFinedViewModel> data = new ArrayList<>();

        while (resultSet.next()) {
            TeachersFinedViewModel item = new TeachersFinedViewModel();

            item.teacher_id.setValue(resultSet.getInt("teacher_id"));
            item.full_name.setValue(resultSet.getString("full_name"));
            item.phone_number.setValue(resultSet.getString("phone_number"));
            item.email_address.setValue(resultSet.getString("email_address"));
            item.fine.setValue(resultSet.getDouble("fine"));

            data.add(item);
        }

        return data;
    }

}

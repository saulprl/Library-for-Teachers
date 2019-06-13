package Controllers.File;

import Models.ConnectionMethods;
import Models.OnLoan;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class ReturnBookController {

    private static OnLoan selectedOnLoan;
    private static final String SELECT = "SELECT DISTINCT teacher_id FROM books_on_loan " +
            "WHERE date_returned IS NULL;";
    private static String query;

    @FXML
    private ComboBox idComboBox;

    @FXML
    private Button returnButton;

    @FXML
    private Button cancelButton;

    @FXML
    private GridPane gridPane;

    @FXML
    private TableView tableView;

    @FXML
    private void returnButtonPressed(ActionEvent event) {
        if (selectedOnLoan != null) {
            query = String.format("UPDATE books_on_loan SET date_returned = '%s' " +
                    "WHERE teacher_id = %s AND isbn = '%s' AND date_issued = '%s';",
                    LocalDate.now(), selectedOnLoan.teacher_id.getValue(), selectedOnLoan.isbn.getValue(),
                    selectedOnLoan.date_issued.getValue());

            try {
                ConnectionMethods.executeUpdate(query);

                Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                dialog.setTitle("Libro devuelto");
                dialog.setHeaderText(null);
                dialog.setContentText("La fecha de devolución se ha registrado correctamente.");
                dialog.showAndWait();

                updateTable(null);
                updateCombo();
            } catch (SQLException sqlEx) {
                Alert dialog = new Alert(Alert.AlertType.ERROR);
                dialog.setTitle("Error al actualizar");
                dialog.setHeaderText(null);
                dialog.setContentText("Ha ocurrido un error al registrar la fecha de devolución. Excepción: " +
                        sqlEx.getMessage());
                dialog.showAndWait();
            }

        } else {
            Alert dialog = new Alert(Alert.AlertType.INFORMATION);
            dialog.setTitle("Selección nula");
            dialog.setHeaderText(null);
            dialog.setContentText("Asegúrate de haber seleccionado un objeto en la tabla.");
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
        updateCombo();
        returnButton.setDisable(true);

        idComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                query = String.format("SELECT * FROM books_on_loan WHERE teacher_id = %s;", newValue.toString());
                updateTable(query);
            } else {
                updateTable(null);
            }
        });

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedOnLoan = (OnLoan) (newValue);
                returnButton.setDisable(false);
            } else {
                selectedOnLoan = null;
                returnButton.setDisable(true);
            }
        });
    }

    private void updateCombo() {
        idComboBox.getItems().clear();

        try {
            ResultSet resultSet = ConnectionMethods.executeQuery(SELECT);

            while (resultSet.next()) {
                idComboBox.getItems().add(resultSet.getInt("teacher_id"));
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

    private void updateTable(String sql) {
        tableView.getItems().clear();
        tableView.getColumns().clear();

        if (sql == null) {
            return;
        }

        try {
            ResultSet resultSet = ConnectionMethods.executeQuery(sql);

            if (resultSet != null) {
                ObservableList data = FXCollections.observableArrayList(dataBaseArrayList(resultSet));

                for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                    TableColumn column = new TableColumn();

                    switch (resultSet.getMetaData().getColumnName(i + 1)) {
                        case "teacher_id":
                            column.setText("ID de profesor");
                            break;
                        case "isbn":
                            column.setText("ISBN");
                            break;
                        case "date_issued":
                            column.setText("Fecha de emisión");
                            break;
                        case "date_due":
                            column.setText("Fecha límite");
                            break;
                        case "date_returned":
                            column.setText("Fecha de devolución");
                            break;
                        case "amount_of_fine":
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

    private ArrayList<OnLoan> dataBaseArrayList(ResultSet resultSet) throws SQLException {
        ArrayList<OnLoan> data = new ArrayList<>();

        while (resultSet.next()) {
            OnLoan item = new OnLoan();

            item.teacher_id.setValue(resultSet.getInt("teacher_id"));
            item.isbn.setValue(resultSet.getString("isbn"));
            item.date_issued.setValue(resultSet.getObject("date_issued", LocalDate.class));
            item.date_due.setValue(resultSet.getObject("date_due", LocalDate.class));
            item.date_returned.setValue(resultSet.getObject("date_returned", LocalDate.class));
            item.amount_of_fine.setValue(resultSet.getDouble("amount_of_fine"));

            data.add(item);
        }

        return data;
    }

}

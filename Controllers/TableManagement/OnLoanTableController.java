package Controllers.TableManagement;

import Models.ConnectionMethods;
import Models.OnLoan;
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
import java.time.LocalDate;
import java.util.ArrayList;

public class OnLoanTableController {

    private static OnLoan selectedOnLoan;
    private static final String INITIALIZEQUERY = "SELECT * FROM books_on_loan;";
    private static final String TEACHERSQUERY = "SELECT teacher_id FROM teachers;";
    private static final String BOOKSQUERY = "SELECT isbn FROM books;";
    private static String query;

    @FXML
    private ComboBox idComboBox;

    @FXML
    private ComboBox isbnComboBox;

    @FXML
    private Button registerButton;

    @FXML
    private Button clearButton;

    @FXML
    private GridPane gridPane;

    @FXML
    private TableView tableView;

    @FXML
    private void registerButtonPressed(ActionEvent event) {
        if (idComboBox.getValue() != null && isbnComboBox.getValue() != null) {
            String id = idComboBox.getValue().toString();
            String isbn = isbnComboBox.getValue().toString();
            query = String.format("INSERT INTO books_on_loan(teacher_id, isbn) VALUES('%s', '%s');", id, isbn);

            try {
                ConnectionMethods.executeUpdate(query);

                Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                dialog.setTitle("Registro insertado");
                dialog.setHeaderText(null);
                dialog.setContentText("La relación se ha insertado correctamente.");
                dialog.showAndWait();

                clearButtonPressed(new ActionEvent());
            } catch (SQLException sqlEx) {
                Alert dialog = new Alert(Alert.AlertType.ERROR);
                dialog.setTitle("Error al insertar");
                dialog.setHeaderText(null);
                dialog.setContentText("Ha ocurrido un error al insertar el registro. Excepción: " + sqlEx.getMessage());
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
    private void clearButtonPressed(ActionEvent event) {
        updateData(INITIALIZEQUERY);

        for (Node node : gridPane.getChildren()) {
            if (node instanceof ComboBox) {
                ((ComboBox) node).getSelectionModel().select(null);
            } else if (node instanceof Button) {
                if (!node.getId().contains("clear")) {
                    node.setDisable(true);
                }
            }
        }
    }

    @FXML
    private void initialize() {
        updateData(INITIALIZEQUERY);
        registerButton.setDisable(true);

        idComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && isbnComboBox.getValue() != null) {
                registerButton.setDisable(false);
            } else {
                registerButton.setDisable(true);
            }
        });

        isbnComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && idComboBox.getValue() != null) {
                registerButton.setDisable(false);
            } else {
                registerButton.setDisable(true);
            }
        });

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedOnLoan = (OnLoan) (newValue);

                idComboBox.getSelectionModel().select(selectedOnLoan.teacher_idProperty().getValue());
                isbnComboBox.getSelectionModel().select(selectedOnLoan.isbnProperty().getValue());
            } else {
                selectedOnLoan = null;
            }
        });
    }

    private void updateData(String sql) {
        tableView.getItems().clear();
        tableView.getColumns().clear();
        idComboBox.getItems().clear();
        isbnComboBox.getItems().clear();

        try {
            ResultSet resultSet = ConnectionMethods.executeQuery(sql);
            ResultSet teachersSet = ConnectionMethods.executeQuery(TEACHERSQUERY);
            ResultSet booksSet = ConnectionMethods.executeQuery(BOOKSQUERY);

            if (resultSet != null) {
                while (teachersSet.next())
                    idComboBox.getItems().add(teachersSet.getInt("teacher_id"));

                while (booksSet.next())
                    isbnComboBox.getItems().add(booksSet.getString("isbn"));

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

            ConnectionMethods.close();
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

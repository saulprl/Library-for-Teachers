package Controllers.Queries;

import Models.ConnectionMethods;
import Models.OnLoanViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class OnLoanController {

    private static final String INITIALIZEQUERY = "SELECT * FROM books_on_loan_view;";
    private static String query;

    @FXML
    private TextField titleTextField;

    @FXML
    private TextField teacherTextField;

    @FXML
    private TextField isbnTextField;

    @FXML
    private DatePicker datePicker;

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
        if (!(titleTextField.getText().isEmpty() && isbnTextField.getText().isEmpty() &&
                teacherTextField.getText().isEmpty() && datePicker.getValue() == null)) {
            String field = "";
            String parameter = "";
            boolean date = false;

            for (Node node : gridPane.getChildren()) {
                if (node instanceof TextField && !node.isDisabled()) {
                    if (node.getId().contains("title")) {
                        field = "book_title";
                        parameter = titleTextField.getText();
                    } else if (node.getId().contains("isbn")) {
                        field = "isbn";
                        parameter = isbnTextField.getText();
                    } else {
                        field = "full_name";
                        parameter = teacherTextField.getText();
                    }
                } else if (node instanceof DatePicker) {
                    field = "date_issued";
                    parameter = datePicker.getValue().toString();
                    date = true;
                }
            }

            if (date) {
                query = String.format("SELECT * FROM books_on_loan_view WHERE %s = '%s';", field, parameter);
            } else {
                query = String.format("SELECT * FROM books_on_loan_view WHERE %s LIKE '%s%s%s';",
                        field, "%", parameter, "%");
            }

            updateTable(query);
        } else {
            Alert dialog = new Alert(Alert.AlertType.INFORMATION);
            dialog.setTitle("Par�metros de b�squeda");
            dialog.setHeaderText(null);
            dialog.setContentText("Aseg�rate de haber ingresado un par�metro de b�squeda.");
            dialog.showAndWait();
        }
    }

    @FXML
    private void clearButtonPressed(ActionEvent event) {
        query = INITIALIZEQUERY;
        updateTable(query);

        for (Node node : gridPane.getChildren()) {
            node.setDisable(false);

            if (node instanceof TextField) {
                ((TextField) node).clear();
            } else if (node instanceof DatePicker) {
                ((DatePicker) node).setValue(null);
            }
        }
    }

    @FXML
    private void initialize() {
        query = INITIALIZEQUERY;
        updateTable(query);

        datePicker = new DatePicker();
        datePicker.setPrefWidth(isbnTextField.getPrefWidth());
        gridPane.add(datePicker, 3, 1);

        titleTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 30) {
                titleTextField.setText(oldValue);
            }

            if (!newValue.isEmpty()) {
                teacherTextField.setDisable(true);
                isbnTextField.setDisable(true);
                datePicker.setDisable(true);
            } else {
                teacherTextField.setDisable(false);
                isbnTextField.setDisable(false);
                datePicker.setDisable(false);
            }
        });

        teacherTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\w{0,20}([ ]\\w{0,20})?")) {
                teacherTextField.setText(oldValue);
            }

            if (newValue.length() > 41) {
                teacherTextField.setText(oldValue);
            }

            if (!newValue.isEmpty()) {
                titleTextField.setDisable(true);
                isbnTextField.setDisable(true);
                datePicker.setDisable(true);
            } else {
                titleTextField.setDisable(false);
                isbnTextField.setDisable(false);
                datePicker.setDisable(false);
            }
        });

        isbnTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 20) {
                isbnTextField.setText(oldValue);
            }

            if (!newValue.isEmpty()) {
                titleTextField.setDisable(true);
                teacherTextField.setDisable(true);
                datePicker.setDisable(true);
            } else {
                titleTextField.setDisable(false);
                teacherTextField.setDisable(false);
                datePicker.setDisable(false);
            }
        });

        datePicker.valueProperty().addListener((observable) -> {
            if (datePicker.getValue() != null) {
                isbnTextField.setDisable(true);
                teacherTextField.setDisable(true);
                titleTextField.setDisable(true);
            } else {
                isbnTextField.setDisable(false);
                teacherTextField.setDisable(false);
                titleTextField.setDisable(false);
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

    private void updateTable(String sql) {
        tableView.getItems().clear();
        tableView.getColumns().clear();

        try {
            ResultSet resultSet = ConnectionMethods.executeQuery(sql);

            if (resultSet != null) {
                ObservableList data = FXCollections.observableArrayList(dataBaseArrayList(resultSet));

                for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                    TableColumn column = new TableColumn();

                    switch (resultSet.getMetaData().getColumnName(i + 1)) {
                        case "book_title":
                            column.setText("T�tulo");
                            break;
                        case "isbn":
                            column.setText("ISBN");
                            break;
                        case "full_name":
                            column.setText("Profesor");
                            break;
                        case "date_issued":
                            column.setText("Fecha de pr�stamo");
                            break;
                        case "date_due":
                            column.setText("Fecha de vencimiento");
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

            resultSet.close();
            ConnectionMethods.close();
        } catch (SQLException sqlEx) {
            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error");
            dialog.setHeaderText(null);
            dialog.setContentText("Ha ocurrido un error de conexi�n. Excepci�n: " + sqlEx.getMessage());
            dialog.showAndWait();
        }
    }

    private ArrayList<OnLoanViewModel> dataBaseArrayList(ResultSet resultSet) throws SQLException {
        ArrayList<OnLoanViewModel> data = new ArrayList<>();

        while (resultSet.next()) {
            OnLoanViewModel item = new OnLoanViewModel();

            item.book_title.setValue(resultSet.getString("book_title"));
            item.isbn.setValue(resultSet.getString("isbn"));
            item.full_name.setValue(resultSet.getString("full_name"));
            item.date_issued.setValue(resultSet.getObject("date_issued", LocalDate.class));
            item.date_due.setValue(resultSet.getObject("date_due", LocalDate.class));
            item.fine.setValue(resultSet.getDouble("fine"));

            data.add(item);
        }

        return data;
    }
}

package Controllers.Queries;

import Models.BooksByAuthorViewModel;
import Models.ConnectionMethods;
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

public class ByAuthorController {

    private static final String INITIALIZEQUERY = "SELECT * FROM books_by_author_view;";
    private static String query;

    @FXML
    private GridPane gridPane;

    @FXML
    private TextField titleTextField;

    @FXML
    private TextField authorTextField;

    @FXML
    private TextField isbnTextField;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Button fetchButton;

    @FXML
    private Button clearButton;

    @FXML
    private TableView tableView;

    @FXML
    private void fetchButtonPressed(ActionEvent event) {
        if (!(titleTextField.getText().isEmpty() && authorTextField.getText().isEmpty()
                && isbnTextField.getText().isEmpty() && datePicker.getValue() == null)) {
            String field = "";
            String parameter = "";
            boolean date = false;

            for (Node node : gridPane.getChildren()) {
                if (node instanceof TextField && !node.isDisabled()) {
                    if (node.getId().contains("title")) {
                        parameter = titleTextField.getText();
                        field = "book_title";
                        break;
                    } else if (node.getId().contains("author")) {
                        parameter = authorTextField.getText();
                        field = "author_fullname";
                        break;
                    } else {
                        parameter = isbnTextField.getText();
                        field = "isbn";
                        break;
                    }
                } else if (node instanceof DatePicker && !node.isDisabled()) {
                    parameter = datePicker.getValue().toString();
                    field = "date_of_publication";
                    date = true;
                    break;
                }
            }

            if (!date) {
                query = String.format("SELECT * FROM books_by_author_view WHERE %s LIKE '%s%s%s';",
                        field, "%", parameter, "%");
            } else {
                query = String.format("SELECT * FROM books_by_author_view WHERE %s = '%s';", field, parameter);
            }

            updateTable(query);
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

//        fetchButton.setDisable(true);
        datePicker = new DatePicker();
        datePicker.setPrefWidth(isbnTextField.getPrefWidth());
        gridPane.add(datePicker, 3, 1);

        titleTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 30) {
                titleTextField.setText(oldValue);
            }

            if (!newValue.isEmpty()) {
                authorTextField.setDisable(true);
                isbnTextField.setDisable(true);
                datePicker.setDisable(true);
            } else {
                authorTextField.setDisable(false);
                isbnTextField.setDisable(false);
                datePicker.setDisable(false);
            }
        });

        authorTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\w{0,20}([ ]\\w{0,20})?")) {
                authorTextField.setText(oldValue);
            }

            if (newValue.length() > 41) {
                authorTextField.setText(oldValue);
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
                authorTextField.setDisable(true);
                datePicker.setDisable(true);
            } else {
                titleTextField.setDisable(false);
                authorTextField.setDisable(false);
                datePicker.setDisable(false);
            }
        });

        datePicker.valueProperty().addListener((observable) -> {
            if (datePicker.getValue() != null) {
                isbnTextField.setDisable(true);
                authorTextField.setDisable(true);
                titleTextField.setDisable(true);
            } else {
                isbnTextField.setDisable(false);
                authorTextField.setDisable(false);
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
                            column.setText("Título");
                            break;
                        case "author_fullname":
                            column.setText("Autor");
                            break;
                        case "isbn":
                            column.setText("ISBN");
                            break;
                        case "date_of_publication":
                            column.setText("Fecha de publicación");
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
            dialog.setContentText("Ha ocurrido un error de conexión. Excepción: " + sqlEx.getMessage());
            dialog.showAndWait();
        }
    }

    private ArrayList<BooksByAuthorViewModel> dataBaseArrayList(ResultSet resultSet) throws SQLException {
        ArrayList<BooksByAuthorViewModel> data = new ArrayList<>();

        while (resultSet.next()) {
            BooksByAuthorViewModel item = new BooksByAuthorViewModel();
            item.book_title.setValue(resultSet.getString("book_title"));
            item.author_fullname.setValue(resultSet.getString("author_fullname"));
            item.isbn.setValue(resultSet.getString("isbn"));
            item.date_of_publication.setValue(resultSet.getObject("date_of_publication", LocalDate.class));

            data.add(item);
        }

        return data;
    }
}

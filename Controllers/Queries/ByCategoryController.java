package Controllers.Queries;

import Models.BooksByCategoryViewModel;
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

public class ByCategoryController {

    private static final String INITIALIZEQUERY = "SELECT * FROM books_by_category_view;";
    private static String query;

    @FXML
    private TextField titleTextField;

    @FXML
    private ComboBox categoryComboBox;

    @FXML
    private TextField isbnTextField;

    @FXML
    private TextField descriptionTextField;

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
        if (!(titleTextField.getText().isEmpty() && categoryComboBox.getValue() == null &&
                isbnTextField.getText().isEmpty() && descriptionTextField.getText().isEmpty())) {
            String field = "";
            String parameter = "";
            boolean category = false;

            for (Node node : gridPane.getChildren()) {
                if (node instanceof TextField && !node.isDisabled()) {
                    if (node.getId().contains("title")) {
                        parameter = titleTextField.getText();
                        field = "book_title";
                        break;
                    } else if (node.getId().contains("isbn")) {
                        parameter = isbnTextField.getText();
                        field = "isbn";
                        break;
                    } else {
                        parameter = descriptionTextField.getText();
                        field = "category_description";
                        break;
                    }
                } else if (node instanceof ComboBox && !node.isDisabled()) {
                    parameter = categoryComboBox.getValue().toString();
                    field = "category_name";
                    category = true;
                    break;
                }
            }

            if (!category) {
                query = String.format("SELECT * FROM books_by_category_view WHERE %s LIKE '%s%s%s';", field,
                        "%", parameter, "%");
            } else {
                query = String.format("SELECT * FROM books_by_category_view WHERE %s = '%s';", field, parameter);
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
        query = INITIALIZEQUERY;
        updateData(query);

        for (Node node : gridPane.getChildren()) {
            node.setDisable(false);

            if (node instanceof TextField) {
                ((TextField) node).clear();

                if (node.getId().contains("description")) {
                    ((TextField) node).setPromptText("Una palabra");
                }
            } else if (node instanceof ComboBox) {
                ((ComboBox) node).setValue(null);
            }
        }
    }

    @FXML
    private void initialize() {
        query = INITIALIZEQUERY;
        updateData(query);

        titleTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 30) {
                titleTextField.setText(oldValue);
            }

            if (!newValue.isEmpty()) {
                categoryComboBox.setDisable(true);
                isbnTextField.setDisable(true);
                descriptionTextField.setDisable(true);
            } else {
                categoryComboBox.setDisable(false);
                isbnTextField.setDisable(false);
                descriptionTextField.setDisable(false);
            }
        });

        categoryComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                titleTextField.setDisable(true);
                isbnTextField.setDisable(true);
                descriptionTextField.setDisable(true);
            } else {
                titleTextField.setDisable(false);
                isbnTextField.setDisable(false);
                descriptionTextField.setDisable(false);
            }
        });

        isbnTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 20) {
                isbnTextField.setText(oldValue);
            }

            if (!newValue.isEmpty()) {
                titleTextField.setDisable(true);
                categoryComboBox.setDisable(true);
                descriptionTextField.setDisable(true);
            } else {
                titleTextField.setDisable(false);
                categoryComboBox.setDisable(false);
                descriptionTextField.setDisable(false);
            }
        });

        descriptionTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                descriptionTextField.setText(newValue.trim());
                titleTextField.setDisable(true);
                categoryComboBox.setDisable(true);
                isbnTextField.setDisable(true);
            } else {
                titleTextField.setDisable(false);
                categoryComboBox.setDisable(false);
                isbnTextField.setDisable(false);
            }
        });
    }

    private void updateData(String sql) {
        tableView.getItems().clear();
        tableView.getColumns().clear();
        categoryComboBox.getItems().clear();

        try {
            ResultSet resultSet = ConnectionMethods.executeQuery(sql);

            if (resultSet != null) {
                while (resultSet.next()) {
                    String categoryN = resultSet.getString("category_name");
                    if (!categoryComboBox.getItems().contains(categoryN)) {
                        categoryComboBox.getItems().add(categoryN);
                    }
                }
                resultSet.beforeFirst();
                ObservableList data = FXCollections.observableArrayList(dataBaseArrayList(resultSet));


                for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                    TableColumn column = new TableColumn();

                    switch (resultSet.getMetaData().getColumnName(i + 1)) {
                        case "book_title":
                            column.setText("Título");
                            break;
                        case "category_name":
                            column.setText("Categoría");
                            break;
                        case "category_description":
                            column.setText("Descripción");
                            break;
                        case "isbn":
                            column.setText("ISBN");
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

    private ArrayList<BooksByCategoryViewModel> dataBaseArrayList(ResultSet resultSet) throws SQLException {
        ArrayList<BooksByCategoryViewModel> data = new ArrayList<>();

        while (resultSet.next()) {
            BooksByCategoryViewModel item = new BooksByCategoryViewModel();
            item.book_title.setValue(resultSet.getString("book_title"));
            item.category_name.setValue(resultSet.getString("category_name"));
            item.category_description.setValue(resultSet.getString("category_description"));
            item.isbn.setValue(resultSet.getString("isbn"));

            data.add(item);
        }

        return data;
    }
}

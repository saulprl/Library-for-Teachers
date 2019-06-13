package Controllers.TableManagement;

import Models.ByCategory;
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

public class ByCategoryTableController {

    private static ByCategory selectedByCategory;
    private static final String TABLEQUERY = "SELECT * FROM books_by_category;";
    private static final String CATEGORYQUERY = "SELECT category_name FROM categories;";
    private static final String BOOKSQUERY = "SELECT isbn FROM books;";
    private static String query;

    @FXML
    private ComboBox categoryComboBox;

    @FXML
    private ComboBox isbnComboBox;

    @FXML
    private Button registerButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button clearButton;

    @FXML
    private GridPane gridPane;

    @FXML
    private TableView tableView;

    @FXML
    private void registerButtonPressed(ActionEvent event) {
        if (categoryComboBox.getValue() != null && isbnComboBox.getValue() != null) {
            String category = categoryComboBox.getValue().toString();
            String isbn = isbnComboBox.getValue().toString();
            query = String.format("INSERT INTO books_by_author VALUES('%s', '%s');", category, isbn);

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
    private void deleteButtonPressed(ActionEvent event) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Borrar registro");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Seguro de que deseas eliminar esta relación?");
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.OK) {
            query = String.format("DELETE FROM books_by_category WHERE category_name = %s AND isbn = '%s';",
                    selectedByCategory.category_nameProperty().getValue(),
                    selectedByCategory.isbnProperty().getValue());

            try {
                ConnectionMethods.executeUpdate(query);

                Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                dialog.setTitle("Borrar registro");
                dialog.setHeaderText(null);
                dialog.setContentText("La relación ha sido borrado correctamente.");
                dialog.showAndWait();

                clearButtonPressed(new ActionEvent());
            } catch (SQLException sqlEx) {
                Alert dialog = new Alert(Alert.AlertType.ERROR);
                dialog.setTitle("Error al borrar");
                dialog.setHeaderText(null);
                dialog.setContentText("Ha ocurrido un error al borrar la relación. Excepción: " + sqlEx.getMessage());
                dialog.showAndWait();
            }
        } else {
            Alert dialog = new Alert(Alert.AlertType.INFORMATION);
            dialog.setTitle("Borrar registro");
            dialog.setHeaderText(null);
            dialog.setContentText("No se ha efectuado ningún cambio.");
            dialog.showAndWait();
        }
    }

    @FXML
    private void clearButtonPressed(ActionEvent event) {
        updateData(TABLEQUERY);

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
        updateData(TABLEQUERY);
        registerButton.setDisable(true);
        deleteButton.setDisable(true);

        categoryComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && isbnComboBox.getValue() != null) {
                registerButton.setDisable(false);
            } else {
                registerButton.setDisable(true);
            }
        });

        isbnComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && categoryComboBox.getValue() != null) {
                registerButton.setDisable(false);
            } else {
                registerButton.setDisable(false);
            }
        });

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedByCategory = (ByCategory) (newValue);

                categoryComboBox.getSelectionModel().select(selectedByCategory.category_nameProperty().getValue());
                isbnComboBox.getSelectionModel().select(selectedByCategory.isbnProperty().getValue());

                deleteButton.setDisable(false);
            } else {
                selectedByCategory = null;

                deleteButton.setDisable(true);
            }
        });
    }

    private void updateData(String sql) {
        tableView.getItems().clear();
        tableView.getColumns().clear();
        categoryComboBox.getItems().clear();
        isbnComboBox.getItems().clear();

        try {
            ResultSet tableSet = ConnectionMethods.executeQuery(sql);
            ResultSet categorySet = ConnectionMethods.executeQuery(CATEGORYQUERY);
            ResultSet booksSet = ConnectionMethods.executeQuery(BOOKSQUERY);

            if (tableSet != null) {
                while (categorySet.next())
                    categoryComboBox.getItems().add(categorySet.getString("category_name"));

                while (booksSet.next())
                    isbnComboBox.getItems().add(booksSet.getString("isbn"));

                ObservableList data = FXCollections.observableArrayList(dataBaseArrayList(tableSet));

                for (int i = 0; i < tableSet.getMetaData().getColumnCount(); i++) {
                    TableColumn column = new TableColumn();

                    switch (tableSet.getMetaData().getColumnName(i + 1)) {
                        case "category_name":
                            column.setText("Categoría");
                            break;
                        case "isbn":
                            column.setText("ISBN");
                            break;
                        default:
                            column.setText(tableSet.getMetaData().getColumnName(i + 1));
                    }

                    column.setCellValueFactory(new PropertyValueFactory<>(tableSet.getMetaData().getColumnName(i + 1)));
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

    private ArrayList<ByCategory> dataBaseArrayList(ResultSet resultSet) throws SQLException {
        ArrayList<ByCategory> data = new ArrayList<>();

        while (resultSet.next()) {
            ByCategory item = new ByCategory();

            item.category_name.setValue(resultSet.getString("category_name"));
            item.isbn.setValue(resultSet.getString("isbn"));

            data.add(item);
        }

        return data;
    }

}

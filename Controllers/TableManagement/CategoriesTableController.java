package Controllers.TableManagement;

import Models.Category;
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

public class CategoriesTableController {

    private static Category selectedCategory;
    private static final String INITIALIZEQUERY = "SELECT * FROM categories;";
    private static String query;

    @FXML
    private TextField categoryTextField;

    @FXML
    private TextField descriptionTextField;

    @FXML
    private Button registerButton;

    @FXML
    private Button updateButton;

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
        String cName = categoryTextField.getText();
        String cDesc = descriptionTextField.getText();

        if (!updateButton.getText().equals("Guardar")) {
            if (!(cName.isEmpty() && cDesc.isEmpty())) {
                query = String.format("INSERT INTO categories VALUES('%s', '%s');", cName, cDesc);

                try {
                    ConnectionMethods.executeUpdate(query);

                    Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                    dialog.setTitle("Registro insertado");
                    dialog.setHeaderText(null);
                    dialog.setContentText("La categoría se ha agregado a la tabla correctamente.");
                    dialog.showAndWait();

                    updateTable((query = INITIALIZEQUERY));
                } catch (SQLException sqlEx) {
                    Alert dialog = new Alert(Alert.AlertType.ERROR);
                    dialog.setTitle("Error al insertar");
                    dialog.setHeaderText(null);
                    dialog.setContentText("Ha ocurrido un error al insertar la categoría. Excepción: " +
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
            categoryTextField.setText(categoryTextField.getPromptText());
            descriptionTextField.setText(descriptionTextField.getPromptText());

            registerButton.setDisable(true);
            deleteButton.setDisable(true);
            updateButton.setText("Guardar");
            clearButton.setText("Cancelar");
        } else if (updateButton.getText().equals("Guardar")) {
            String cName = categoryTextField.getText();
            String cDesc = descriptionTextField.getText();

            query = String.format("UPDATE categories SET category_name = '%s', category_description = '%s' " +
                            "WHERE category_name = '%s';", cName, cDesc,
                    selectedCategory.category_nameProperty().getValue());

            try {
                ConnectionMethods.executeUpdate(query);

                Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                dialog.setTitle("Modificar registro");
                dialog.setHeaderText(null);
                dialog.setContentText("La información de la categoría ha sido modificada correctamente.");
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
            if (selectedCategory != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Borrar registro");
                confirm.setHeaderText(null);
                confirm.setContentText("¿Seguro de que deseas eliminar esta categoría?");
                confirm.showAndWait();

                if (confirm.getResult() == ButtonType.OK) {
                    query = String.format("DELETE FROM categories WHERE category_name = '%s';",
                            categoryTextField.getText());

                    try {
                        ConnectionMethods.executeUpdate(query);

                        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                        dialog.setTitle("Borrar registro");
                        dialog.setHeaderText(null);
                        dialog.setContentText("La categoría se ha eliminado de la tabla correctamente.");
                        dialog.showAndWait();

                        clearButtonPressed(new ActionEvent());
                    } catch (SQLException sqlEx) {
                        Alert dialog = new Alert(Alert.AlertType.ERROR);
                        dialog.setTitle("Error al borrar");
                        dialog.setHeaderText(null);
                        dialog.setContentText("Ha ocurrido un error al eliminar la categoría. Excepción: "
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
        updateTable((query = INITIALIZEQUERY));

        for (Node node : gridPane.getChildren()) {
            if (node instanceof TextField) {
                ((TextField) node).clear();
                ((TextField) node).setPromptText(((TextField) node).getText());
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
        updateTable((query = INITIALIZEQUERY));
        registerButton.setDisable(true);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);

        categoryTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!(newValue.isEmpty() && descriptionTextField.getText().isEmpty())) {
                registerButton.setDisable(false);
            } else {
                registerButton.setDisable(true);
            }

            if (newValue.length() > 20) {
                categoryTextField.setText(oldValue);
            }
        });

        descriptionTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!(newValue.isEmpty() && categoryTextField.getText().isEmpty())) {
                registerButton.setDisable(false);
            } else {
                registerButton.setDisable(true);
            }
        });

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedCategory = (Category) (newValue);
                updateButton.setDisable(false);
                deleteButton.setDisable(false);

                categoryTextField.setPromptText(selectedCategory.category_nameProperty().getValue());
                descriptionTextField.setPromptText(selectedCategory.category_descriptionProperty().getValue());
            } else {
                selectedCategory = null;
                updateButton.setDisable(true);
                deleteButton.setDisable(true);
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
                        case "category_name":
                            column.setText("Categoría");
                            break;
                        case "category_description":
                            column.setText("Descripción");
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

    @FXML
    private ArrayList<Category> dataBaseArrayList(ResultSet resultSet) throws SQLException {
        ArrayList<Category> data = new ArrayList<>();

        while (resultSet.next()) {
            Category category = new Category();

            category.category_name.setValue(resultSet.getString("category_name"));
            category.category_description.setValue(resultSet.getString("category_description"));

            data.add(category);
        }

        return data;
    }

}

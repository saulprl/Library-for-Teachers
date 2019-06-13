package Models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Category {

    public StringProperty category_name = new SimpleStringProperty();
    public StringProperty category_description = new SimpleStringProperty();

    public StringProperty category_nameProperty() {
        return category_name;
    }
    public StringProperty category_descriptionProperty() {
        return category_description;
    }

    public Category(String name, String description) {
        this.category_name.setValue(name);
        this.category_description.setValue(description);
    }

    public Category() {

    }
    public Category(Category another) {

    }

}

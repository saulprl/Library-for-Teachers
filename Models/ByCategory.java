package Models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ByCategory {

    public StringProperty isbn = new SimpleStringProperty();
    public StringProperty category_name = new SimpleStringProperty();

    public StringProperty isbnProperty() {
        return isbn;
    }
    public StringProperty category_nameProperty() {
        return category_name;
    }

    public ByCategory(String isbn, String name) {
        this.isbn.setValue(isbn);
        this.category_name.setValue(name);
    }

    public ByCategory() {

    }
    public ByCategory(ByCategory another) {

    }
}

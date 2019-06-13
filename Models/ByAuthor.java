package Models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ByAuthor {

    public IntegerProperty author_id = new SimpleIntegerProperty();
    public StringProperty isbn = new SimpleStringProperty();

    public IntegerProperty author_idProperty() {
        return author_id;
    }
    public StringProperty isbnProperty() {
        return isbn;
    }

    public ByAuthor(int id, String isbn) {
        this.author_id.setValue(id);
        this.isbn.setValue(isbn);
    }

    public ByAuthor() {

    }
    public ByAuthor(ByAuthor another) {

    }

}

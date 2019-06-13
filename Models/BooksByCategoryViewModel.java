package Models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class BooksByCategoryViewModel {

    public StringProperty book_title = new SimpleStringProperty();
    public StringProperty category_name = new SimpleStringProperty();
    public StringProperty category_description = new SimpleStringProperty();
    public StringProperty isbn = new SimpleStringProperty();

    public StringProperty book_titleProperty() {
        return book_title;
    }
    public StringProperty category_nameProperty() {
        return category_name;
    }
    public StringProperty category_descriptionProperty() {
        return category_description;
    }
    public StringProperty isbnProperty() {
        return isbn;
    }

    public BooksByCategoryViewModel() {

    }
    public BooksByCategoryViewModel(BooksByCategoryViewModel another) {

    }
}

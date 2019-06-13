package Models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Author {

    public IntegerProperty author_id = new SimpleIntegerProperty();
    public StringProperty author_firstname = new SimpleStringProperty();
    public StringProperty author_surname = new SimpleStringProperty();

    public IntegerProperty author_idProperty() {
        return author_id;
    }
    public StringProperty author_firstnameProperty() {
        return author_firstname;
    }
    public StringProperty author_surnameProperty() {
        return author_surname;
    }

    public Author(int id, String first, String last) {
        this.author_id.setValue(id);
        this.author_firstname.setValue(first);
        this.author_surname.setValue(last);
    }

    public Author() {

    }
    public Author(Author another) {

    }

}

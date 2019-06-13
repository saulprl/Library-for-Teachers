package Models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Teacher {

    public IntegerProperty teacher_id = new SimpleIntegerProperty();
    public StringProperty first_name = new SimpleStringProperty();
    public StringProperty last_name = new SimpleStringProperty();
    public StringProperty teacher_address = new SimpleStringProperty();
    public StringProperty phone_number = new SimpleStringProperty();
    public StringProperty email_address = new SimpleStringProperty();

    public IntegerProperty teacher_idProperty() {
        return teacher_id;
    }
    public StringProperty first_nameProperty() {
        return first_name;
    }
    public StringProperty last_nameProperty() {
        return last_name;
    }
    public StringProperty teacher_addressProperty() {
        return teacher_address;
    }
    public StringProperty phone_numberProperty() {
        return phone_number;
    }
    public StringProperty email_addressProperty() {
        return email_address;
    }

    public Teacher(int id, String first, String last, String address, String phone, String email) {
        this.teacher_id.setValue(id);
        this.first_name.setValue(first);
        this.last_name.setValue(last);
        this.teacher_address.setValue(address);
        this.phone_number.setValue(phone);
        this.email_address.setValue(email);
    }

    public Teacher() {

    }
    public Teacher(Teacher another) {

    }

}

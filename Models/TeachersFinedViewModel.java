package Models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.DoubleProperty;

public class TeachersFinedViewModel {

    public IntegerProperty teacher_id = new SimpleIntegerProperty();
    public StringProperty full_name = new SimpleStringProperty();
    public StringProperty phone_number = new SimpleStringProperty();
    public StringProperty email_address = new SimpleStringProperty();
    public DoubleProperty fine = new SimpleDoubleProperty();

    public IntegerProperty teacher_idProperty() {
        return teacher_id;
    }
    public StringProperty full_nameProperty() {
        return full_name;
    }
    public StringProperty phone_numberProperty() {
        return phone_number;
    }
    public StringProperty email_addressProperty() {
        return email_address;
    }
    public DoubleProperty fineProperty() {
        return fine;
    }

    public TeachersFinedViewModel() {

    }

    public TeachersFinedViewModel(int id, String name, String phone, String email,
                                  double fine) {
        this.teacher_id.setValue(id);
        this.full_name.setValue(name);
        this.phone_number.setValue(phone);
        this.email_address.setValue(email);
        this.fine.setValue(fine);
    }

    public TeachersFinedViewModel(TeachersFinedViewModel another) {
    }

}

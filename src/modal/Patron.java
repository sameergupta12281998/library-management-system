package modal;

import java.util.Objects;
import java.util.UUID;

public class Patron {
    private final String id;
    private  String name;
    private  String email;
    private  String phoneNumber;

    public Patron(String name, String email, String phone){
        this(UUID.randomUUID().toString(), name, email, phone);
    }

    public Patron(String id, String name, String email, String phone){
        if (id == null || id.isBlank()) throw new IllegalArgumentException("id required");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name required");
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phone;
    }
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    
       @Override
    public String toString(){
        return "Patron{id='%s', name='%s'}".formatted(id, name);
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof Patron)) return false;
        Patron that = (Patron) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode(){
        return Objects.hash(id);
    }
    
}

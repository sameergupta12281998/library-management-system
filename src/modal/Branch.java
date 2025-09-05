package modal;

public class Branch {

    private final String id;
    private String name;
    private String address;

    public Branch(String name, String address) {
        this(java.util.UUID.randomUUID().toString(), name, address);
    }

    public Branch(String id, String name, String address) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("id required");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name required");
        this.id = id;
        this.name = name;
        this.address = address;
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
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    @Override
    public String toString() {
        return "Branch{id='%s', name='%s', address='%s'}".formatted(id, name, address);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Branch)) return false;
        Branch that = (Branch) o;
        return java.util.Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id);
    }

}

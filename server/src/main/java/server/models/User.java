package server.models;

public class User implements Model {
    private String name;
    // Id will automatically be mapped to this property by jackson. No need for setter
    private int id;

    // This is for jackson databind ObjectMapper
    public User() {}

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
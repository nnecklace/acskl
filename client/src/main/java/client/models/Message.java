package client.models;

public class Message {
    private int id;
    private String content;
    private int timestamp;
    private int userId;

    public Message(int id, String content, int timestamp, int userId) {
        this.id = id;
        this.content = content;
        this.timestamp = timestamp;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public int getUserId() {
        return userId;
    }

}
package server.services;

import java.util.List;

import server.database.Database;
import server.models.Message;

public class MessageService {
    private Database database;

    public MessageService(Database database) {
        this.database = database;
    }

    public Message create(String content, int timestamp, int userId) {
        if (content.length() > 1000) {
            return null;
        }

        int messageId = database.establish()
                                .query("INSERT INTO messages (content,timestamp,userId) VALUES(?,?,?)")
                                .addValue(1, content)
                                .addValue(2, timestamp)
                                .addValue(3, userId)
                                .execute();
        
        // 0 insert failed
        if (messageId == 0) {
            database.close();
            return null;
        }
        
        Message message = database.establish()
                                  .query("SELECT * FROM messages WHERE id = ?")
                                  .addValue(1, messageId)
                                  .executeReturning()
                                  .asValue(Message.class);

        database.close();

        return message;
    }

    public List<Message> getAll() {
        List<Message> messages = database.establish()
                                        .query("SELECT * FROM messages")
                                        .executeReturning()
                                        .asList(Message.class);
        database.close();

        return messages;
    }
}
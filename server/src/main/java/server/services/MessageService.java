package server.services;

import java.util.List;

import server.database.Database;
import server.models.Message;

/**
 * Class is incharge of handling all message table related calls to the database.
 * Everythinig that retrives something from the message table should be handled with this class
 */
public class MessageService {
    private Database database;

    public MessageService(Database database) {
        this.database = database;
    }

    /**
     * Method creates a new message with the givev parameters.
     * Method does not allow messages over 1000 characters.
     * @param content the content of the message
     * @param timestamp the current time when the message was inserted into the database
     * @param userId the id for the author of the message
     * @return the newly created message or null if message could not be created
     */
    public Message create(String content, int timestamp, int userId) {
        if (content.length() > 1000) {
            return null;
        }

        int messageId = insert(content, timestamp, userId);

        // 0 insert failed
        if (messageId == 0) {
            database.close();
            return null;
        }

        Message message = get(messageId);

        database.close();
        return message;
    }

    private int insert(String content, int timestamp, int userId) {
        return database.establish()
                    .query("INSERT INTO messages (content,timestamp,userId) VALUES(?,?,?)")
                    .addValue(1, content)
                    .addValue(2, timestamp)
                    .addValue(3, userId)
                    .execute();
    }

    private Message get(int id) {
        return database.establish()
                    .query("SELECT *, name FROM messages INNER JOIN users ON messages.userId = users.id WHERE messages.id = ?")
                    .addValue(1, id)
                    .executeReturning()
                    .asValue(Message.class);
    }

    /**
     * Method retrives all messages from the database.
     * @return List of all messages in the database
     */
    public List<Message> getAll() {
        List<Message> messages = database.establish()
                                        .query("SELECT *, name FROM messages INNER JOIN users ON messages.userId = users.id")
                                        .executeReturning()
                                        .asList(Message.class);
        database.close();

        return messages;
    }
}
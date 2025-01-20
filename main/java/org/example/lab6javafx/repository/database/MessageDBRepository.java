package org.example.lab6javafx.repository.database;

import org.example.lab6javafx.domain.Friendship;
import org.example.lab6javafx.domain.Message;
import org.example.lab6javafx.domain.Utilizator;
import org.example.lab6javafx.repository.AbstractRepository;
import org.example.lab6javafx.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class MessageDBRepository extends AbstractRepository<Long, Message> {
    private String url;
    private String username;
    private String password;

    public MessageDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;

    }

    private Optional<Message> get_message_details_view(ResultSet resultSet, Long id) throws SQLException {
        try{
            String firstNameSend = resultSet.getString("firstNameSend");
            String lastNameSend = resultSet.getString("lastNameSend");
            String usernameSend = resultSet.getString("usernameSend");
            String passwordSend = resultSet.getString("passwordSend");
            Utilizator from = new Utilizator(firstNameSend, lastNameSend, usernameSend, passwordSend);
            from.setId(resultSet.getLong("idSender"));

            String firstNameRecv = resultSet.getString("firstNameRecv");
            String lastNameRecv = resultSet.getString("lastNameRecv");
            String usernameRecv = resultSet.getString("usernameRecv");
            String passwordRecv = resultSet.getString("passwordRecv");
            Utilizator to = new Utilizator(firstNameRecv, lastNameRecv, usernameRecv, passwordRecv);
            to.setId(resultSet.getLong("idReceiver"));

            String message = resultSet.getString("message");
            LocalDateTime timestamp = resultSet.getTimestamp("dateSend").toLocalDateTime();
            Long replyingTo = resultSet.getLong("replyingToId");

            Message m = new Message(from, to, message, timestamp, replyingTo);
            m.setId(id);
            return Optional.of(m);
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Message> findOne(Long id) {
        if(id == null){
            throw new IllegalArgumentException("Id cannot be null");
        }
        String query = "SELECT * FROM \"message_details_view\" WHERE \"id\" = ?";
        Message message = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);)
        {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                return get_message_details_view(resultSet, id);
            }
            return Optional.empty();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<Message> findAll() {
        List<Message> messages = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM \"message_details_view\"");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Long idMessage = resultSet.getLong("id");
                Optional<Message> message = get_message_details_view(resultSet, idMessage);
                message.ifPresent(messages::add);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return messages;
    }

    @Override
    public Optional<Message> save(Message entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity must not be null");
        }

        String query = "INSERT INTO \"message\" (\"idsender\", \"idreceiver\", \"message\", \"replyingtoid\", \"datesend\") VALUES (?, ?, ?, ?, ?) RETURNING \"id\"";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);) {
            statement.setLong(1, entity.getFrom().getId());
            statement.setLong(2, entity.getTo().getId());
            statement.setString(3, entity.getMessage());
            statement.setLong(4, entity.getReplyingTo());
            statement.setTimestamp(5, Timestamp.valueOf(entity.getDate()));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Long id = resultSet.getLong("id");
                entity.setId(id);
                return Optional.of(entity);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public Optional<Message> delete(Long aLong) {
        String query = "DELETE FROM \"message\" WHERE \"id\" = ?";
        Optional<Message> entity = findOne(aLong);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);) {
            statement.setLong(1, aLong);
            int response = 0;
            if(entity.isPresent()){
                response = statement.executeUpdate();
            }
            if (response == 0) {
                return Optional.empty(); //niciun rand nu a fost actualizat
            }
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Optional<Message> update(Message entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity can't be null!");
        }
        String query = "UPDATE \"message\" SET \"message\" = ?, \"datesend\" = ?, \"idreceiver\" = ?, \"idsender\" = ? WHERE(\"id\" = ?);";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);) {
            statement.setString(1, entity.getMessage());
            statement.setTimestamp(2, Timestamp.valueOf(entity.getDate()));
            statement.setLong(3, entity.getReplyingTo());
            statement.setLong(4, entity.getTo().getId());
            statement.setLong(5, entity.getFrom().getId());
            statement.setLong(6, entity.getId());
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                return Optional.empty(); //niciun rand nu a fost actualizat
            }
            return Optional.of(entity);} catch (SQLException e) {
            throw new RuntimeException(e);
            }
        }

}

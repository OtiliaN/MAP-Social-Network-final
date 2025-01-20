package org.example.lab6javafx.repository.database;

import org.example.lab6javafx.domain.Friendship;
import org.example.lab6javafx.domain.FriendshipStatus;
import org.example.lab6javafx.domain.Utilizator;
import org.example.lab6javafx.domain.validators.Validator;
import org.example.lab6javafx.repository.AbstractRepository;
import org.example.lab6javafx.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class FriendshipDBRepository extends AbstractRepository<Long, Friendship> {
    private String url;
    private String username;
    private String password;
    private Validator<Friendship> validator;

    public FriendshipDBRepository(String url, String username, String password, Validator<Friendship> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    private Friendship get_friendship_details_view(ResultSet resultSet) throws SQLException {
        var idFriendship = resultSet.getLong("id");

        var idUser1 = resultSet.getLong("firstUserId");
        var firstNameUser1 = resultSet.getString("firstNameU1");
        var lastNameUser1 = resultSet.getString("lastNameU1");
        var usernameUser1 = resultSet.getString("usernameU1");
        var passwordUser1 = resultSet.getString("passwordU1");
        var user1 = new Utilizator(firstNameUser1, lastNameUser1, usernameUser1,passwordUser1);
        user1.setId(idUser1);

        var idUser2 = resultSet.getLong("secondUserId");
        var firstNameUser2 = resultSet.getString("firstNameU2");
        var lastNameUser2 = resultSet.getString("lastNameU2");
        var usernameUser2 = resultSet.getString("usernameU2");
        var passwordUser2 = resultSet.getString("passwordU2");
        var user2 = new Utilizator(firstNameUser2, lastNameUser2, usernameUser2, passwordUser2);
        user2.setId(idUser2);

        String status = resultSet.getString("status");
        Timestamp friendsFrom = resultSet.getTimestamp("date");
        Friendship friendship = new Friendship(user1, user2, friendsFrom.toLocalDateTime(), FriendshipStatus.valueOf(status));
        friendship.setId(idFriendship);

        return friendship;
    }

    @Override
    public Optional<Friendship> findOne(Long aLong) {
        String query = "SELECT * FROM \"friendship_details_view\" WHERE \"id\" = ?";
        Friendship friendship = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);)
        {
            statement.setLong(1, aLong);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                return Optional.of(get_friendship_details_view(resultSet));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<Friendship> findAll() {
        Set<Friendship> friendships = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM \"friendship_details_view\"");
             ResultSet resultSet = statement.executeQuery()) {
             while (resultSet.next()) {
                Friendship friendship = get_friendship_details_view(resultSet);
                friendships.add(friendship);
             }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return friendships;
    }

    @Override
    public Optional<Friendship> save(Friendship entity) {
        if (entity == null) {
            throw new IllegalArgumentException("id must not be null");
        }

        String query = "INSERT INTO \"friendship\" (\"idfriend1\", \"idfriend2\", \"date\", \"status\") VALUES (?,?,?,?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);) {
            statement.setLong(1, entity.getUser1().getId());
            statement.setLong(2, entity.getUser2().getId());
            statement.setTimestamp(3, Timestamp.valueOf(entity.getDate()));
            statement.setString(4,entity.getStatus().toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.of(entity);
    }

    @Override
    public Optional<Friendship> delete(Long aLong) {
        String query = "DELETE FROM \"friendship\" WHERE \"idfriendship\" = ?";
        Optional<Friendship> entity = findOne(aLong);
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
    public Optional<Friendship> update(Friendship entity) {
        if (entity == null) {
            throw new IllegalArgumentException("User can't be null!");
        }
        String query = "UPDATE \"friendship\" SET \"date\" = ?, \"status\" = ? WHERE (\"idfriend1\" = ? AND \"idfriend2\" = ?) OR (\"idfriend1\" = ? AND \"idfriend2\" = ?);";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);) {
            statement.setTimestamp(1, Timestamp.valueOf(entity.getDate()));
            statement.setString(2, entity.getStatus().toString());
            statement.setLong(3, entity.getUser1().getId());
            statement.setLong(4, entity.getUser2().getId());
            statement.setLong(5, entity.getUser2().getId());
            statement.setLong(6, entity.getUser1().getId());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                return Optional.empty(); //niciun rand nu a fost actualizat
            }
            return Optional.of(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

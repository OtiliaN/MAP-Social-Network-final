package org.example.lab6javafx.repository.database;

import org.example.lab6javafx.domain.Friendship;
import org.example.lab6javafx.domain.FriendshipStatus;
import org.example.lab6javafx.domain.Utilizator;
import org.example.lab6javafx.repository.Repository;
import org.example.lab6javafx.utils.paging.Page;
import org.example.lab6javafx.utils.paging.Pageable;

import java.sql.*;
import java.util.*;

public class UserDBRepository implements UserPaging {
    private String url;
    private String username;
    private String password;

    public UserDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;

    }

    private Friendship getFriendshipFromStatement(ResultSet resultSet) throws SQLException {
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
        Timestamp friendsFrom = resultSet.getTimestamp("friendsFrom");
        Friendship friendship = new Friendship(user1, user2, friendsFrom.toLocalDateTime(), FriendshipStatus.valueOf(status));
        friendship.setId(idFriendship);

        return friendship;
    }

    private int countFriends(Long id){
        String query = "SELECT COUNT(*) FROM friendship_details_view WHERE ((firstUserId=? OR secondUserId=?) AND status=?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);) {
            statement.setLong(1, id);
            statement.setLong(2, id);
            statement.setString(3, "ACCEPTED");
            try{
                ResultSet resultSet = statement.executeQuery();
                return resultSet.next() ? resultSet.getInt(1) : 0;
            }catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Utilizator getFriendFromStatement(ResultSet resultSet, Integer position) throws SQLException {
        Utilizator friend;
        if (position.equals(1)) {
            var idUser1 = resultSet.getLong("firstUserId");
            var firstNameUser1 = resultSet.getString("firstNameU1");
            var lastNameUser1 = resultSet.getString("lastNameU1");
            var usernameUser1 = resultSet.getString("usernameU1");
            var passwordUser1 = resultSet.getString("passwordU1");
            friend = new Utilizator(firstNameUser1, lastNameUser1, usernameUser1, passwordUser1);
            friend.setId(idUser1);
        } else {
            var idUser2 = resultSet.getLong("secondUserId");
            var firstNameUser2 = resultSet.getString("firstNameU2");
            var lastNameUser2 = resultSet.getString("lastNameU2");
            var usernameUser2 = resultSet.getString("usernameU2");
            var passwordUser2 = resultSet.getString("passwordU2");
            friend = new Utilizator(firstNameUser2, lastNameUser2, usernameUser2, passwordUser2);
            friend.setId(idUser2);
        }
        return friend;
    }

    @Override
    public Page<Utilizator> findAllOnPage(Pageable pageable, Long id) {
        Map<Long, Utilizator> friendsMap = new HashMap<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendship_details_view WHERE ((firstUserId=? OR secondUserId=?) AND status=?)" +  "LIMIT ? OFFSET ?");){
            statement.setLong(1, id);
            statement.setLong(2, id);
            statement.setString(3, "ACCEPTED");
            statement.setInt(4, pageable.getPageSize());
            statement.setInt(5,pageable.getPageNumber() * pageable.getPageSize());
            try(ResultSet resultSet = statement.executeQuery()) {
                Integer position = 0;
                while(resultSet.next()){
                    Long userId1 = resultSet.getLong("firstUserId");
                    Long userId2 = resultSet.getLong("secondUserId");
                    if(userId1.equals(id)){
                        position = 2;
                    }
                    if(userId2.equals(id)){
                        position = 1;
                    }
                    Utilizator friend = getFriendFromStatement(resultSet, position);
                    if(friend != null)
                        friendsMap.put(friend.getId(), friend);
                }
            }catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new Page<>(friendsMap.values(), countFriends(id));
    }

    private Optional<Utilizator> getUser(ResultSet resultSet, Long id) throws SQLException {
        //Long id = resultSet.getLong("id");
        String firstName = resultSet.getString("first_name");
        String lastName = resultSet.getString("last_name");
        String username = resultSet.getString("username");
        String password = resultSet.getString("password");
        Utilizator user = new Utilizator(firstName, lastName, username, password);
        user.setId(id);
        return Optional.of(user);
    }

    @Override
    public Optional<Utilizator> findOne(Long id) {
        String query = "select * from \"user\" WHERE \"id\" = ?";
        Utilizator user = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                return getUser(resultSet, id);
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Iterable<Utilizator> findAll() {
        Set<Utilizator> users = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from \"user\"");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                users.add(getUser(resultSet, id).get());

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public Optional<Utilizator> save(Utilizator entity) {
        if (entity == null) {
            throw new IllegalArgumentException("User can't be null!");
        }
        String query = "INSERT INTO \"user\"(\"first_name\", \"last_name\", \"username\", \"password\") VALUES (?,?,?,?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);) {
            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            statement.setString(3, entity.getUsername());
            statement.setString(4, entity.getPassword());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.of(entity);
    }

    @Override
    public Optional<Utilizator> delete(Long aLong) {
        if (aLong == null) {
            throw new IllegalArgumentException("Id can't be null!");
        }
        Optional<Utilizator> entity = findOne(aLong);
        String query = "DELETE FROM \"user\" WHERE \"id\" = ?";
        int rowsAffected = 0;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);) {
            statement.setLong(1, aLong);
            if(entity.isPresent()){
                rowsAffected = statement.executeUpdate();
            }
           if (rowsAffected == 0) {
                return Optional.empty(); //niciun rand nu a fost actualizat
           }
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<Utilizator> update(Utilizator entity) {
        if (entity == null) {
            throw new IllegalArgumentException("User can't be null!");
        }
        String query = "UPDATE \"user\" SET \"first_name\" = ?, \"last_name\" = ?, \"username\" = ?, \"password\" = ? WHERE \"id\" = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);) {

            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            statement.setString(3, entity.getUsername());
            statement.setString(4, entity.getPassword());
            statement.setLong(5, entity.getId());
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



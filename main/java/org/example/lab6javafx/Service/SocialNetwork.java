package org.example.lab6javafx.Service;

import jdk.jshell.execution.Util;
import org.example.lab6javafx.domain.Friendship;
import org.example.lab6javafx.domain.FriendshipStatus;
import org.example.lab6javafx.domain.Message;
import org.example.lab6javafx.domain.Utilizator;
import org.example.lab6javafx.domain.validators.ValidationException;
import org.example.lab6javafx.repository.Repository;
import org.example.lab6javafx.repository.database.UserPaging;
import org.example.lab6javafx.utils.events.ChangeEventType;
import org.example.lab6javafx.utils.events.EntityChangeEvent;
import org.example.lab6javafx.utils.observer.Observable;
import org.example.lab6javafx.utils.observer.Observer;
import org.example.lab6javafx.utils.paging.Page;
import org.example.lab6javafx.utils.paging.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public class SocialNetwork implements Observable<EntityChangeEvent>{
    private final UserPaging repositoryUser;
    private final Repository<Long, Friendship> repositoryFriendship;
    private final Repository<Long, Message> repositoryMessage;
    private List<Observer<EntityChangeEvent>> observers = new ArrayList<>();

    /**
     * creates a social network
     * @param repositoryUser repository for users
     * @param repositoryFriendship repository for friendships
     */
    public SocialNetwork(UserPaging repositoryUser, Repository<Long, Friendship> repositoryFriendship, Repository<Long, Message> repositoryMessage) {
        this.repositoryUser = repositoryUser;
        this.repositoryFriendship = repositoryFriendship;
        this.repositoryMessage = repositoryMessage;
    }

    ////PAGE
    public Page<Utilizator> findAllOnPage(Pageable pageable, Long id) {
        return repositoryUser.findAllOnPage(pageable, id);
    }

    @Override
    public void addObserver(Observer<EntityChangeEvent> observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer<EntityChangeEvent> observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(EntityChangeEvent e) {
        observers.stream().forEach(x -> x.update(e));
    }

    //////USERS
    public Iterable<Utilizator> getAllUsers() {
        return repositoryUser.findAll();
    }


    public Utilizator findUserByUsername(String username) {
        return (Utilizator) StreamSupport.stream(repositoryUser.findAll().spliterator(), false)
                .filter(user -> user.getUsername().equals(username))
                .findFirst().orElse(null);
    }

    public Utilizator findUserById(Long id) {
        return repositoryUser.findOne(id).orElseThrow(() -> new ValidationException("No user"));
    }


    public boolean addUser(String firstName, String lastName, String username, String password) {
        Utilizator user = new Utilizator(firstName, lastName, username, password);
        repositoryUser.save(user);
        notifyObservers(new EntityChangeEvent(ChangeEventType.ADD, user));
        return true;
    }


    public Utilizator removeUser(long id) {
        Optional<Utilizator> u1 = repositoryUser.findOne(id);
        if(u1.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        Utilizator user = u1.get();
        repositoryUser.delete(user.getId());
        notifyObservers(new EntityChangeEvent(ChangeEventType.DELETE, user));
        return user;
    }

    public boolean updateUser(String firstName, String lastName, String username, String password, String oldusername) {
        Utilizator user = findUserByUsername(oldusername);
        Utilizator newUser = new Utilizator(firstName, lastName, username, password);
        if(user == null){
            throw new IllegalArgumentException("User not found");
        }
        newUser.setId(user.getId());
        boolean updated = repositoryUser.update(newUser).isPresent();
        if(updated){
            notifyObservers(new EntityChangeEvent(ChangeEventType.UPDATE, newUser));
        }
        return updated;
    }

    public List<Utilizator> getUserNotFriendsWith(Utilizator user) {
        List<Utilizator> allUsers = StreamSupport.stream(repositoryUser.findAll().spliterator(), false)
                .collect(Collectors.toList());
        List<Utilizator> friends = getUsersFriends(user);

        return allUsers.stream()
                .filter(u -> !u.getId().equals(user.getId()) && !friends.contains(u))
                .collect(Collectors.toList());
    }

    /////FRIENDSHIPS
    /**
     * @return the friendships
     */
    public Iterable<Friendship> getFriendships() {
        return repositoryFriendship.findAll();
    }

    public Friendship findFriendship(Long id1, Long id2){
        Iterable<Friendship> friends = repositoryFriendship.findAll();
        Optional<Friendship> friendship = StreamSupport.stream(friends.spliterator(), false)
                .filter(f -> (f.getUser1().getId().equals(id1) && f.getUser2().getId().equals(id2)) ||
                        (f.getUser1().getId().equals(id2) && f.getUser2().getId().equals(id1)))
                .findFirst();
        return friendship.orElse(null);
    }

    /**
     * adds a new friendship
     */
    public boolean addFriendship(Long id1, Long id2) {
        Optional<Utilizator> u1 = repositoryUser.findOne(id1);
        Optional<Utilizator> u2 = repositoryUser.findOne(id2);

        if (u1.isEmpty() || u2.isEmpty()) {
            throw new ValidationException("One or both users do not exist");
        }

        Utilizator user1 = u1.get();
        Utilizator user2 = u2.get();
        Friendship friendship = new Friendship(user1, user2, LocalDateTime.now(), FriendshipStatus.PENDING);

        repositoryFriendship.save(friendship);
        notifyObservers(new EntityChangeEvent(ChangeEventType.ADD, friendship));
        return true;
    }

    /**
     * removes a friendship
     * @param id1 the id of the first user
     * @param id2 the id of the second user
     */
    public boolean removeFriendship(Long id1, Long id2) {
        Iterable<Friendship> friendships = repositoryFriendship.findAll();
        Optional<Friendship> fr = StreamSupport.stream(friendships.spliterator(), false)
                .filter(friendship -> (friendship.getUser1().getId().equals(id1) && friendship.getUser2().getId().equals(id2))
                        || friendship.getUser1().getId().equals(id2) && friendship.getUser2().getId().equals(id1))
                .findFirst();
        if(fr.isPresent()){
            repositoryFriendship.delete(fr.get().getId());
        }
        else{
            throw new IllegalArgumentException("Friendship not found");
        }
        notifyObservers(new EntityChangeEvent(ChangeEventType.DELETE, fr.get()));
        return true;
    }

    public boolean modifyFriendship(Long id1, Long id2, FriendshipStatus status) {
        Friendship fr = findFriendship(id1, id2);
        if(status == FriendshipStatus.ACCEPTED){
            fr.setDate(LocalDateTime.now());
            fr.setStatus(FriendshipStatus.ACCEPTED);
            repositoryFriendship.update(fr);
            notifyObservers(new EntityChangeEvent(ChangeEventType.UPDATE, fr));
        } else if(status == FriendshipStatus.REJECTED) {
            repositoryFriendship.delete(fr.getId());
            notifyObservers(new EntityChangeEvent(ChangeEventType.DELETE, fr));
        }
        return true;
    }

    public List<Friendship> getRequests(String username) {
        Utilizator currentUser = findUserByUsername(username);
        if (currentUser == null) {
            throw new IllegalArgumentException("User not found");
        }
        Iterable<Friendship> friendships = repositoryFriendship.findAll();


        return StreamSupport.stream(friendships.spliterator(), false)
                .filter(friendship ->
                        (friendship.getUser2().getId().equals(currentUser.getId()) &&
                                (friendship.getStatus() == FriendshipStatus.PENDING || friendship.getStatus() == FriendshipStatus.ACCEPTED))                 )
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all user's friends
     * @param user - the user whose friends are being retrieved
     * @return the user's friend list
     */
    public ArrayList<Utilizator> getUsersFriends(Utilizator user) {
        Long userId = user.getId();


        return StreamSupport.stream(repositoryFriendship.findAll().spliterator(), false)
                .filter(friendship ->
                        (friendship.getUser1().getId().equals(userId) || friendship.getUser2().getId().equals(userId))
                                && friendship.getStatus() == FriendshipStatus.ACCEPTED)
                .map(friendship ->

                        friendship.getUser1().getId().equals(userId) ? friendship.getUser2() : friendship.getUser1())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    ////MESSAGES
    public List<Message> getAllMessages() {
        return (List<Message>) repositoryMessage.findAll();
    }

    public List<Message> getMessagesBetweenUsers(Utilizator u1, Utilizator u2) {
        List<Message> conv = getAllMessages().stream()
                .filter(m->m.getFrom().equals(u1) && m.getTo().equals(u2) || m.getFrom().equals(u2) && m.getTo().equals(u1))
                .collect(Collectors.toList());
        conv.sort(Comparator.comparing(Message::getDate));
        return conv;
    }


    public void sendMessage(Utilizator from, Utilizator to, String message, Long replyingTo) {
        Message mes = new Message(from, to, message, LocalDateTime.now(), replyingTo);
        repositoryMessage.save(mes);
        notifyObservers(new EntityChangeEvent<>(ChangeEventType.ADDMESSAGE, mes));
    }



}

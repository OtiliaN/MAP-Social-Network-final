package org.example.lab6javafx.domain.validators;



import org.example.lab6javafx.domain.Friendship;
import org.example.lab6javafx.domain.Utilizator;
import org.example.lab6javafx.repository.database.UserDBRepository;

import java.util.Optional;

public class FriendshipValidator implements Validator<Friendship> {
    /**
     * Validates a friendship between two users
     * @param friendship the friendship to be validated
     * @throws ValidationException if the id does not exist
     */
    @Override
    public void validate(Friendship friendship) throws ValidationException {
        String erori = "";
        if(friendship.getDate() == null) {
            erori += "The Date cannot be null!";
        }
        if(friendship.getUser1() == null || friendship.getUser2() == null) {
            erori += "The User cannot be null!\n";
        }
        if(erori.isEmpty()) {
            return;
        }
        throw new ValidationException(erori);
    }
}

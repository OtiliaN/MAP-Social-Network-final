package org.example.lab6javafx.domain.validators;


import org.example.lab6javafx.domain.Utilizator;

public class UtilizatorValidator implements Validator<Utilizator> {
    /**
     * Validates a user
     * @param entity the user to be validated
     * @throws ValidationException if user's names aren't valid
     */
    @Override
    public void validate(Utilizator entity) throws ValidationException {
        String error = "";

        if(entity.getFirstName().isEmpty()){
            error += "First name is required!";
        }
        if(entity.getLastName().isEmpty()){
            error += "Last name is required!";
        }
        System.out.println(error);
        if(!error.equals("")){
            throw new ValidationException(error);
        }
    }
}

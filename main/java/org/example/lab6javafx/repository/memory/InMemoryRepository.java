package org.example.lab6javafx.repository.memory;



import org.example.lab6javafx.domain.Entity;
import org.example.lab6javafx.domain.validators.ValidationException;
import org.example.lab6javafx.domain.validators.Validator;
import org.example.lab6javafx.repository.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID,E> {

    private Validator<E> validator;
    protected Map<ID,E> entities;

    /**
     * Initializing the repo
     * @param validator validating objects
     */
    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        entities=new HashMap<ID,E>();
    }

    /**
     * Find the id
     * @param id -the id of the entity to be returned
     *           id must not be null
     */
    @Override
    public Optional<E> findOne(ID id) {
        if(id==null){
            throw new IllegalArgumentException("id cannot be null");
        }
        return Optional.ofNullable(entities.get(id));
    }

    /**
     * Find all the entities
     */
    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }

    @Override
    public Optional<E> save(E entity) throws ValidationException {

        /**
         *
         * @param entity
         *         entity must be not null
         * @return null- if the given entity is saved
         *         otherwise returns the entity (id already exists)
         * @throws ValidationException
         *            if the entity is not valid
         * @throws IllegalArgumentException
         *             if the given entity is null.     *
         */

        if(entity==null)
            throw new IllegalArgumentException("ENTITY CANNOT BE NULL");
        validator.validate(entity);
        return Optional.ofNullable(entities.putIfAbsent(entity.getId(), entity));
    }

    /**
     * Removes an entity from the repository
     * @param id
     *      id must be not null
     */
    @Override
    public Optional<E> delete(ID id) {
        if(id==null)
            throw new IllegalArgumentException("ID CANNOT BE NULL");
        return Optional.ofNullable(entities.remove(id));
    }

    /**
     * Modifies an entity from the repository
     * @param entity
     *          entity must not be null
     * @return the new entity
     */
    @Override
    public Optional<E> update(E entity) {
        if(entity==null){
            throw new IllegalArgumentException("ENTITY CANNOT BE NULL");
        }
        validator.validate(entity);
        if(entities.containsKey(entity.getId())){
            entities.put(entity.getId(),entity);
            return Optional.empty();
        }
        return Optional.of(entity);
    }
}

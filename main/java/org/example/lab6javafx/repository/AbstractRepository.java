package org.example.lab6javafx.repository;

import org.example.lab6javafx.domain.Entity;

import java.util.Optional;

public abstract class AbstractRepository <ID, E extends Entity<ID>> implements Repository<ID, E>{

    @Override
    public abstract Optional<E> findOne(ID id);

    @Override
    public abstract Iterable<E> findAll();

    @Override
    public abstract Optional<E> save(E entity);

    @Override
    public abstract Optional<E> delete(ID id);

    @Override
    public abstract Optional<E> update(E entity);

}

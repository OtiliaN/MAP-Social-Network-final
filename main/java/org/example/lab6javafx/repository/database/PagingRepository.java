package org.example.lab6javafx.repository.database;

import org.example.lab6javafx.domain.Entity;
import org.example.lab6javafx.repository.Repository;
import org.example.lab6javafx.utils.paging.Page;
import org.example.lab6javafx.utils.paging.Pageable;

public interface PagingRepository<ID, E extends Entity<ID>> extends Repository<ID, E> {
    Page<E> findAllOnPage(Pageable pageable, Long id);
}

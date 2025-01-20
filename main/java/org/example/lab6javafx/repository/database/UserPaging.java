package org.example.lab6javafx.repository.database;

import org.example.lab6javafx.domain.Utilizator;
import org.example.lab6javafx.utils.paging.Page;
import org.example.lab6javafx.utils.paging.Pageable;

public interface UserPaging  extends PagingRepository<Long, Utilizator>{
    Page<Utilizator> findAllOnPage(Pageable pageable, Long id);
}

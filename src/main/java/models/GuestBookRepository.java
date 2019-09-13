package models;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

public interface GuestBookRepository extends CrudRepository<GuestBookEntry, Integer>{

    Iterable<GuestBookEntry> findAll(Sort timestamp);
}

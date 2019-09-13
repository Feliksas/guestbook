package models;

import org.springframework.data.repository.CrudRepository;

public interface GuestBookRepository extends CrudRepository<GuestBookEntry, Integer>{

}

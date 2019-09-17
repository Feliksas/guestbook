package models;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface GuestBookRepository extends  PagingAndSortingRepository<GuestBookEntry, Integer>{

}

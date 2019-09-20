package models;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface MessageRepository extends  PagingAndSortingRepository<GuestBookEntry, Integer>{

}

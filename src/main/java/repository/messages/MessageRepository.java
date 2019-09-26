package repository.messages;

import models.messages.GuestBookEntry;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends  PagingAndSortingRepository<GuestBookEntry, Integer>{

}

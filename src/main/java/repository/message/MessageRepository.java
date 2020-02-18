package repository.message;

import java.util.List;
import java.util.Optional;
import domain.message.GuestBookEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends PagingAndSortingRepository<GuestBookEntry, Integer> {
    Optional<GuestBookEntry> findById(int id);

    List<GuestBookEntry> findAll();

    @Query("SELECT e FROM GuestBookEntry e WHERE e.parentMsgId IS NULL")
    Page<GuestBookEntry> findRootMsgs(PageRequest pageable);

    @Query("SELECT e FROM GuestBookEntry e WHERE e.parentMsgId IS NULL")
    List<GuestBookEntry> findRootMsgs();

    List<GuestBookEntry> findAllByParentMsgId(int parentMsgId);

    @Query("SELECT COUNT(e) FROM GuestBookEntry e WHERE e.parentMsgId = :id")
    int countChildren(int id);
}

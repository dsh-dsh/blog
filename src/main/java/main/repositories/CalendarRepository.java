package main.repositories;

import main.dto.CalendarDTO;
import main.model.ModerationStatus;
import main.model.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CalendarRepository extends CrudRepository<Post, Integer> {

    @Query("SELECT new main.dto.CalendarDTO(DATE(p.time), count(p)) " +
            "FROM Post p " +
            "WHERE p.isActive = :isActive " +
            "AND p.moderationStatus = :moderationStatus " +
            "GROUP BY DATE(p.time)")
    List<CalendarDTO> getCalendar(boolean isActive, ModerationStatus moderationStatus);
}

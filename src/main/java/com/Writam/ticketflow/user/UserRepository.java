package com.Writam.ticketflow.user;
//the data access layer
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository <User, UUID> {

    Optional<User> findByEmail(String email);
    @Query(value = "SELECT u.*, count(t.id) as ticket_count FROM users u  left join tickets t on t.assigned_to =  u.id AND t.status != 'CLOSED' WHERE" +
            " u.role = 'AGENT' group by u.id order by ticket_count asc limit 1", nativeQuery = true)
    Optional<User> findLeastLoadedAgent();

    boolean existsByEmail(String email);
}

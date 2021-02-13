package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface TournamentRepository extends JpaRepository<Tournament, Integer> {

    @Query(value = "SELECT MAX(key) FROM tournaments", nativeQuery = true)
    Integer getMaxTournamentKey();

    @Query(value = "SELECT * FROM tournaments", nativeQuery = true)
    List<Tournament> findAll();

    @Query(value = "SELECT Count(*) FROM enrolled_students t WHERE tournament_id = :id", nativeQuery = true)
    Integer getEnrolledStudensCount(Integer id);

    @Query(value = "SELECT * FROM tournaments t WHERE t.start_time > :now", nativeQuery = true)
    List<Tournament> findAllOpen(LocalDateTime now);

    @Query(value = "SELECT * FROM tournaments t WHERE t.id = :id", nativeQuery = true)
    Optional<Tournament> findById(Integer id);

    @Query(value = " SELECT * FROM tournaments t WHERE (id NOT IN (SELECT tournament_id AS id FROM enrolled_students WHERE user_id = :userId) AND t.end_time > :now AND t.course_execution_id = :executionId)", nativeQuery = true)
    List<Tournament> findAllEnrollable(LocalDateTime now, int userId, int executionId);

    @Query(value = "SELECT * FROM tournaments t WHERE t.start_time > :now AND t.creator_id = :creatorId", nativeQuery = true)
    List<Tournament> findByCreator(LocalDateTime now, Integer creatorId);

    @Query(value = "DELETE FROM tournaments WHERE id = :id", nativeQuery = true)
    @Modifying
    @Transactional
    void deleteById(Integer id);

    @Query(value = "DELETE FROM enrolled_students WHERE tournament_id = :id", nativeQuery = true)
    @Modifying
    @Transactional
    void deleteEnrolledByTournamentId(Integer id);
}

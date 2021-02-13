package pt.ulisboa.tecnico.socialsoftware.tutor.clarification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface ClarificationRepository extends JpaRepository<Clarification, Integer> {
    @Query(value = "SELECT * FROM clarifications c WHERE c.state = 'NOT_ANSWERED'", nativeQuery = true)
    List<Clarification> getUnansweredClarifications();

    @Query(value = "SELECT * FROM clarifications c WHERE c.state = 'ANSWERED'", nativeQuery = true)
    List<Clarification> getAnsweredClarifications();

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE clarifications c SET c.teacher_clarification = :clarificationResponse WHERE c.id = :clarificationId", nativeQuery = true)
    int updateTeacherResponse(Integer clarificationId, String clarificationResponse);

    @Modifying(clearAutomatically = true)
    @Query(value ="UPDATE clarifications c SET c.state = 'ANSWERED' WHERE c.id = :clarificationId", nativeQuery = true)
    int updateState(Integer clarificationId);

    @Query(value = "SELECT teacher_clarification FROM clarifications c WHERE c.id = :clarificationId", nativeQuery = true)
    String getTeacherResponse(Integer clarificationId);

    @Query(value = "SELECT * FROM clarifications c, question_answers q WHERE c.question_clarification_id = q.id " +
                    "AND q.id = :questionAnswerId AND c.student_request = :question", nativeQuery = true)
    Clarification findClarificationByRequest(int questionAnswerId, String question);

    @Query(value = "SELECT * FROM clarifications c WHERE c.privacy = 'PUBLIC'", nativeQuery = true)
    List<Clarification> getPublicClarifications();

    @Query(value = "SELECT COUNT(*) FROM clarifications c WHERE c.student_username = :username", nativeQuery = true)
    Integer getNumberOfRequests(String username);

    @Query(value = "SELECT COUNT(*) FROM clarifications c WHERE c.student_username = :username AND c.state = 'ANSWERED'", nativeQuery = true)
    Integer getNumberOfResponses(String username);
}

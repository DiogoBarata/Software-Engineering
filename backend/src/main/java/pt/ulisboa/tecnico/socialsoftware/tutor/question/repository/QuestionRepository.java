package pt.ulisboa.tecnico.socialsoftware.tutor.question.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface QuestionRepository extends JpaRepository<Question, Integer> {
    @Query(value = "SELECT * FROM questions q WHERE q.course_id = :courseId", nativeQuery = true)
    List<Question> findQuestions(int courseId);

    @Query(value = "SELECT * FROM questions q WHERE q.course_id = :courseId AND q.status = 'AVAILABLE'", nativeQuery = true)
    List<Question> findAvailableQuestions(int courseId);

    @Query(value = "SELECT * FROM questions q WHERE q.course_id = :courseId AND q.status = 'DISABLED'", nativeQuery = true)
    List<Question> findDisabledQuestions(int courseId);

    @Query(value = "SELECT * FROM questions q WHERE q.course_id = :courseId AND q.state = 'SUBMITTED'", nativeQuery = true)
    List<Question> findSubmittedQuestions(int courseId);

    @Query(value = "SELECT * FROM questions q WHERE q.course_id = :courseId AND q.state = 'APPROVED'", nativeQuery = true)
    List<Question> findApprovedQuestions(int courseId);

    @Query(value = "SELECT questions_id FROM topics_questions WHERE topics_id = :topicId ", nativeQuery = true)
    List<Integer> findByTopic(int topicId);

    @Query(value = "SELECT count(*) FROM questions q WHERE q.course_id = :courseId AND q.status = 'AVAILABLE'", nativeQuery = true)
    Integer getAvailableQuestionsSize(Integer courseId);

    @Query(value = "SELECT MAX(key) FROM questions", nativeQuery = true)
    Integer getMaxQuestionNumber();

    @Query(value = "SELECT * FROM questions q WHERE q.course_id = :courseId and q.username like :user_name", nativeQuery = true)
    List<Question> findUserQuestions(int courseId, String user_name);

    @Query(value = "SELECT * FROM questions q WHERE q.course_id = :courseId and q.justification like :just_fication", nativeQuery = true)
    List<Question> checkJustificationQuestions(int courseId, String just_fication);

    @Query(value = "SELECT * FROM questions q WHERE q.key = :key", nativeQuery = true)
    Optional<Question> findByKey(Integer key);

    @Query(value ="SELECT count(*) FROM questions q WHERE q.course_id = :courseId and q.username like :user_name", nativeQuery = true)
    Integer getSubmittedQuestionsStudent(int courseId, String user_name);

    @Query(value ="SELECT count(*) FROM questions q WHERE q.course_id = :courseId and q.state like 'APPROVED' and q.username like :user_name", nativeQuery = true)
    Integer getApprovedQuestionsStudent(int courseId, String user_name);

    @Query(value ="SELECT count(*) FROM questions q WHERE q.course_id = :courseId and q.state like 'DISAPPROVED' and q.username like :user_name", nativeQuery = true)
    Integer getDisapprovedQuestionsStudent(int courseId, String user_name);

}
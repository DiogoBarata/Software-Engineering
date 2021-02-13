package pt.ulisboa.tecnico.socialsoftware.tutor.clarification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.clarification.Clarification.State.ANSWERED;
import static pt.ulisboa.tecnico.socialsoftware.tutor.clarification.Clarification.State.NOT_ANSWERED;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class ClarificationService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionAnswerRepository questionAnswerRepository;

    @Autowired
    private ClarificationRepository clarificationRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ClarificationDto createClarification(Integer questionAnswerId, ClarificationDto clarificationDto){
        QuestionAnswer questionAnswer = questionAnswerRepository.findById(questionAnswerId)
                .orElseThrow(() -> new TutorException(QUESTION_ANSWER_NOT_FOUND, questionAnswerId));

        if (clarificationRepository.findClarificationByRequest(questionAnswer.getId(), clarificationDto.getStudentRequest()) != null) {
            throw new TutorException(DUPLICATE_CLARIFICATION_REQUEST, clarificationDto.getStudentRequest());
        }
        clarificationDto.setRequestDate(clarificationDto.getRequestDate().replaceAll("=", "-"));
        clarificationDto.setState(NOT_ANSWERED);
        Clarification clarification = new Clarification(questionAnswer, clarificationDto);
        clarificationRepository.save(clarification);

        return new ClarificationDto(clarification);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ClarificationDto createClarificationRequest(Integer userId, String text, QuestionAnswer questionAnswer){
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        Clarification clarification = new Clarification(text, user.getUsername(), questionAnswer);
        entityManager.persist(clarification);

        return  new ClarificationDto(clarification);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<Clarification> getUnansweredClarification(){
        return clarificationRepository.getUnansweredClarifications();
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<ClarificationDto> getClarificationController(){
        return clarificationRepository.findAll().stream().map(ClarificationDto::new).collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<Clarification> getAnsweredClarification(){
        return clarificationRepository.getAnsweredClarifications();
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Clarification findById(Integer clarificationId){
        return clarificationRepository.findById(clarificationId)
                .orElseThrow(() -> new TutorException(CLARIFICATION_NOT_FOUND, clarificationId));
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ClarificationDto findClarificationById(Integer clarificationId){
        return clarificationRepository.findById(clarificationId).map(ClarificationDto::new)
                .orElseThrow(() -> new TutorException(CLARIFICATION_NOT_FOUND, clarificationId));
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void changeClarification(Integer clarificationId, String content, User user){
        Clarification clarification = clarificationRepository.findById(clarificationId).orElseThrow(() -> new TutorException(CLARIFICATION_NOT_FOUND, clarificationId));
        clarification.setAnswerDateAuto();

        if(user.getRole() == User.Role.STUDENT){
            throw new TutorException(USER_WITHOUT_PERMISSIONS);
        }

        else if( content == null || content.isEmpty()){
            throw new TutorException(CLARIFICATION_TEXT_IS_EMPTY, clarificationId);
        }
        else {
            clarificationRepository.updateTeacherResponse(clarificationId, content);
            clarificationRepository.updateState(clarificationId);
        }
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ClarificationDto resendClarificationRequest(Integer clarificationId, ClarificationDto clarificationDto){
        Clarification clarification = clarificationRepository.findById(clarificationId)
                .orElseThrow(() -> new TutorException(CLARIFICATION_NOT_FOUND, clarificationId));

        String content = clarificationDto.getAdditionalStudentRequest();

        if(content == null || content.isEmpty()){
            throw new TutorException(CLARIFICATION_TEXT_IS_EMPTY);
        }
        clarification.setAdditionalStudentRequest(content);
        clarification.setState(NOT_ANSWERED);

        return new ClarificationDto(clarification);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ClarificationDto resendClarificationResponse(Integer clarificationId, ClarificationDto clarificationDto){
        Clarification clarification = clarificationRepository.findById(clarificationId)
                .orElseThrow(() -> new TutorException(CLARIFICATION_NOT_FOUND, clarificationId));

        String content = clarificationDto.getAdditionalTeacherClarification();

        if(content == null || content.isEmpty()){
            throw new TutorException(CLARIFICATION_TEXT_IS_EMPTY);
        }
        clarification.setAdditionalTeacherClarification(content);
        clarification.setState(ANSWERED);

        return new ClarificationDto(clarification);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String getAnswer(Integer clarificationId){
        return clarificationRepository.getTeacherResponse(clarificationId);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ClarificationDto updateClarification(Integer clarificationId, ClarificationDto clarificationDto){
        Clarification clarification = clarificationRepository.findById(clarificationId)
                .orElseThrow(() -> new TutorException(CLARIFICATION_NOT_FOUND, clarificationId));

        clarification.setState(clarificationDto.getState());
        clarification.setTeacherClarification(clarificationDto.getTeacherClarification());
        clarification.setAnswerDate(clarificationDto.getAnswerDateDate());
        return new ClarificationDto(clarification);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public CourseDto findClarificationCourse(Integer clarificationId){
        return clarificationRepository.findById(clarificationId)
                .map(Clarification::getQuestionAnswer)
                .map(QuestionAnswer::getQuizQuestion)
                .map(QuizQuestion::getQuestion)
                .map(Question::getCourse)
                .map(CourseDto::new)
                .orElseThrow(() -> new TutorException(CLARIFICATION_NOT_FOUND, clarificationId));
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<ClarificationDto> getClarifications(){
        return clarificationRepository.findAll().stream()
                .map(ClarificationDto::new)
                .collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void updateTeacherClarification(Integer clarificationId, String newTeacherClarification){
        Clarification clarification = clarificationRepository.findById(clarificationId).orElseThrow(() -> new TutorException(CLARIFICATION_NOT_FOUND, clarificationId));
        clarification.setTeacherClarification(newTeacherClarification);
        clarification.setAnswerDateAuto();
        clarification.setState(ANSWERED);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void removeClarification(int clarificationId) {
        Clarification clarification = clarificationRepository.findById(clarificationId)
                .orElseThrow(() -> new TutorException(CLARIFICATION_NOT_FOUND, clarificationId));

        clarification.delete();

        clarificationRepository.delete(clarification);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void setPrivacyClarification(Integer clarificationId, Clarification.Privacy privacy) {
        Clarification clarification = clarificationRepository.findById(clarificationId).orElseThrow(() -> new TutorException(CLARIFICATION_NOT_FOUND, clarificationId));
        clarification.setPrivacy(privacy);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<ClarificationDto> getClarificationsByQuestion(Integer quizQuestionId) {
        QuizQuestion quizQuestion = quizQuestionRepository.findById(quizQuestionId).orElseThrow(() -> new TutorException(QUIZ_QUESTION_NOT_FOUND, quizQuestionId));
        Integer questionId = quizQuestion.getQuestion().getId();

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionId));
        Set<QuizQuestion> quizQuestionList = question.getQuizQuestions();
        List<QuestionAnswer> questionAnswerList = new ArrayList<QuestionAnswer>();
        List<Clarification> clarificationList = new ArrayList<Clarification>();
        List<ClarificationDto> clarificationDtoList = new ArrayList<ClarificationDto>();

        for(QuizQuestion q : quizQuestionList) {
            questionAnswerList.addAll(q.getQuestionAnswers());
        }
        for(QuestionAnswer q : questionAnswerList) {
            clarificationList.addAll(q.getClarifications().stream().filter(
                    p -> p.getPrivacy() == Clarification.Privacy.PUBLIC).collect(Collectors.toList()));
        }
        for(Clarification c : clarificationList) {
            clarificationDtoList.add(new ClarificationDto(c));
        }
        return clarificationDtoList;

    }
}

package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.annotation.DirtiesContext
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.Clarification
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationDto
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationService
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.CLARIFICATION_TEXT_IS_EMPTY

@DataJpaTest
class AdditionalClarificationRequest extends Specification {
    public static final String STUDENT_USERNAME = "Student"
    public static final String TEACHER_USERNAME = "Teacher"

    @Autowired
    ClarificationRepository clarificationRepository;

    @Autowired
    ClarificationService clarificationService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    QuestionAnswerRepository questionAnswerRepository;

    @Autowired
    QuizQuestionRepository quizQuestionRepository;

    @Autowired
    QuestionRepository questionRepository;

    def student
    def teacher
    def questionAnswer
    def clarification
    def quizQuestion
    def question

    def setup(){
        teacher = new User('user1', TEACHER_USERNAME, 1, User.Role.TEACHER);
        student = new User('user2', STUDENT_USERNAME, 2, User.Role.STUDENT)
        question = new Question();
        quizQuestion = new QuizQuestion();
        questionAnswer = new QuestionAnswer();
        clarification = new Clarification();

        quizQuestion.setQuestion(question);
        questionAnswer.setQuizQuestion(quizQuestion);
        clarification.setStudentUsername(student.getUsername());
        clarification.setStudentRequest('Dúvida 1');
        clarification.setTeacherClarification('Resposta 1');
        clarification.setQuestionAnswer(questionAnswer);
        clarification.setState(Clarification.State.ANSWERED);

        userRepository.save(student);
        userRepository.save(teacher);
        questionRepository.save(question);
        quizQuestionRepository.save(quizQuestion);
        questionAnswerRepository.save(questionAnswer);
        clarificationRepository.save(clarification);
    }

    def "student requests an additional clarification"() {
        given: "a clarificationDto"
        def clarificationDto = new ClarificationDto();
        clarificationDto.setAdditionalStudentRequest('Dúvida adicional 1');

        when:
        clarificationService.resendClarificationRequest(clarificationRepository.findAll().get(0).getId(), clarificationDto);
        def unansweredClarifications = clarificationService.getUnansweredClarification();
        def answeredClarifications = clarificationService.getAnsweredClarification();
        def updatedClarification = clarificationRepository.findAll().get(0);

        then: "clarification state change to NOT_ANSWERED"
        updatedClarification.getAdditionalStudentRequest().equals('Dúvida adicional 1');
        updatedClarification.getState().equals(Clarification.State.NOT_ANSWERED);
        unansweredClarifications.size() == 1;
        answeredClarifications.size() == 0;
    }

    def "teacher responses to an additional clarification request" () {
        given: "a clarificationDto"
        def clarificationDto = new ClarificationDto();
        clarificationDto.setAdditionalStudentRequest('Dúvida adicional 1');
        clarificationDto.setAdditionalTeacherClarification('Resposta adicional 1');

        when:
        clarificationService.resendClarificationRequest(clarificationRepository.findAll().get(0).getId(), clarificationDto);
        clarificationService.resendClarificationResponse(clarificationRepository.findAll().get(0).getId(), clarificationDto);
        def unansweredClarifications = clarificationService.getUnansweredClarification();
        def answeredClarifications = clarificationService.getAnsweredClarification();
        def updatedClarification = clarificationRepository.findAll().get(0);

        then: "clarification state must be ANSWERED"
        updatedClarification.getAdditionalTeacherClarification().equals('Resposta adicional 1');
        updatedClarification.getState().equals(Clarification.State.ANSWERED);
        unansweredClarifications.size() == 0;
        answeredClarifications.size() == 1;
    }

    def "user requests an empty additional clarification"() {
        given: "a clarificationDto"
        def clarificationDto = new ClarificationDto();
        clarificationDto.setAdditionalStudentRequest("");

        when:
        clarificationService.resendClarificationRequest(clarificationRepository.findAll().get(0).getId(), clarificationDto);

        then: "throw exception"
        TutorException exception = thrown();
        exception.getErrorMessage() == CLARIFICATION_TEXT_IS_EMPTY;
    }

    @TestConfiguration
    static class AnswerServiceImplTestContextConfiguration {

        @Bean
        ClarificationService clarificationService2() {
            return new ClarificationService()
        }
        @Bean
        AnswerService answerService() {
            return new AnswerService()
        }
        @Bean
        AnswersXmlImport aswersXmlImport() {
            return new AnswersXmlImport()
        }
    }
}

package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.annotation.DirtiesContext
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationService
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.time.LocalDateTime

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.CLARIFICATION_TEXT_IS_EMPTY;

@DataJpaTest
class ClarificationRequestTest extends Specification {
    public static final String COURSE_NAME = "Software Engineering"
    public static final String ACRONYM = "ES"
    public static final String ACADEMIC_TERM = "2 SEM"

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    QuizRepository quizRepository

    @Autowired
    QuizQuestionRepository quizQuestionRepository

    @Autowired
    QuizAnswerRepository quizAnswerRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    QuestionAnswerRepository questionAnswerRepository

    @Autowired
    ClarificationRepository clarificationRepository

    @Autowired
    ClarificationService clarificationService

    def user1
    def courseExecution
    def quizQuestion
    def quizAnswer
    def questionAnswer
    def date
    def quiz

    def setup() {
        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        user1 = new User('name1', "username1", 1, User.Role.STUDENT)
        user1.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(user1)

        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setType(Quiz.QuizType.GENERATED)
        quiz.setCourseExecution(courseExecution)
        courseExecution.addQuiz(quiz)

        def question = new Question()
        question.setKey(1)
        question.setCourse(course)
        course.addQuestion(question)

        quizQuestion = new QuizQuestion(quiz, question, 0)
        quizAnswer = new QuizAnswer(user1, quiz)
        questionAnswer = new QuestionAnswer(quizAnswer, quizQuestion, 0)

        date = LocalDateTime.now()

        questionAnswerRepository.save(questionAnswer)
        userRepository.save(user1)
        quizRepository.save(quiz)
        questionRepository.save(question)
        quizQuestionRepository.save(quizQuestion)
        quizAnswerRepository.save(quizAnswer)
    }

    @DirtiesContext
    def "student requests a clarification"() {
        when:
        clarificationService.createClarificationRequest(userRepository.findAll().get(0).getId(), "Dúvida1"
                , questionAnswerRepository.findAll().get(0))
        def unansweredClarifications = clarificationService.getUnansweredClarification()
        def answeredClarifications = clarificationService.getAnsweredClarification()

        then: "exist 1 answered clarification and 0 unanswered clarification"
        unansweredClarifications.size() == 1;
        answeredClarifications.size() == 0;
    }

    @DirtiesContext
    def "different users create 1 clarification request each"() {
        given: "two users that answered a question"
        def user2 = new User('name2', "username2", 2, User.Role.STUDENT)
        user2.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(user2)
        def quizAnswer2 = new QuizAnswer(user2, quiz)
        def questionAnswer2 = new QuestionAnswer(quizAnswer2, quizQuestion, 0)
        userRepository.save(user2)
        questionAnswerRepository.save(questionAnswer2)

        when:
        clarificationService.createClarificationRequest(user1.getId(), "dúvida 1", questionAnswer)
        clarificationService.createClarificationRequest(user2.getId(), "dúvida 2", questionAnswer2)
        def unansweredClarifications = clarificationService.getUnansweredClarification()
        def text_user2 = clarificationService.findById(2).getStudentRequest()

        then:"the text of 'user2' must be 'dúvida2'"
        unansweredClarifications.size() == 2
        text_user2.equals("dúvida 2")
    }

    @DirtiesContext
    def "student create an empty clarification request"() {
        when:
        clarificationService.createClarificationRequest(userRepository.findAll().get(0).getId(), ""
                , questionAnswerRepository.findAll().get(0))

        then: "throw exception"
        TutorException exception = thrown()
        exception.getErrorMessage() == CLARIFICATION_TEXT_IS_EMPTY
    }

    @TestConfiguration
    static class AnswerServiceImplTestContextConfiguration {

        @Bean
        ClarificationService clarificationService1() {
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

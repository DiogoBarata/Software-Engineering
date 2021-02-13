package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.Clarification
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationService
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification
import java.time.LocalDateTime

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.CLARIFICATION_NOT_FOUND
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.CLARIFICATION_TEXT_IS_EMPTY
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_WITHOUT_PERMISSIONS

@DataJpaTest
class ClarificationResponseAndViewTest extends Specification {
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
    OptionRepository optionRepository

    @Autowired
    QuestionAnswerRepository questionAnswerRepository

    @Autowired
    ClarificationRepository clarificationRepository

    @Autowired
    ClarificationService clarificationService

    @Autowired
    AnswerService answerService

    def userStudentOne
    def userStudentSecond
    def userTeacher
    def courseExecution
    def quizQuestion
    def quizAnswer
    def quizAnswerSecond
    def date
    def quiz
    def clarification
    def clarificationSecond
    def questionAnswer
    def questionAnswerSecond

    def setup() {
        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        userStudentOne = new User('name', "username", 1, User.Role.STUDENT)
        userStudentOne.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(userStudentOne)

        userStudentSecond = new User('name_second', "username_second", 2, User.Role.STUDENT)
        userStudentSecond.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(userStudentSecond)

        userTeacher = new User('nameTeacher', "usernameTeacher", 3, User.Role.TEACHER)
        userTeacher.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(userTeacher)

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
        quizAnswer = new QuizAnswer(userStudentOne, quiz)
        questionAnswer = new QuestionAnswer(quizAnswer, quizQuestion, 0)

        quizAnswerSecond = new QuizAnswer(userStudentOne, quiz)
        questionAnswerSecond = new QuestionAnswer(quizAnswerSecond, quizQuestion, 0)

        date = LocalDateTime.now()

        questionAnswerRepository.save(questionAnswer)
        userRepository.save(userStudentOne)
        quizRepository.save(quiz)
        questionRepository.save(question)
        quizQuestionRepository.save(quizQuestion)
        quizAnswerRepository.save(quizAnswer)
        clarification = new Clarification("ClarificationRequestContent", userStudentOne.getUsername(), questionAnswerRepository.findAll().get(0))
        clarificationRepository.save(clarification)

        questionAnswerRepository.save(questionAnswerSecond)
        userRepository.save(userStudentSecond)
        questionRepository.save(question)
        quizQuestionRepository.save(quizQuestion)
        quizAnswerRepository.save(quizAnswerSecond)
    }

    def 'Successful clarification creation'() {
        when:
        clarificationService.changeClarification(clarificationRepository.findAll().get(0).getId(), "value", userTeacher)

        then: "the clarification is inside the repository"
        clarificationRepository.getUnansweredClarifications().size() == 0;
        clarificationRepository.getAnsweredClarifications().size() == 1;
        clarificationRepository.findAll().get(0).getTeacherClarification() == "value"
        clarificationRepository.findAll().get(0).getAnswerDate() != null
    }

    def 'Successful clarification of two creations'() {
        given:
        clarificationSecond = new Clarification("ClarificationRequestContentSecond", userStudentSecond.getUsername(), questionAnswerRepository.findAll().get(1))
        clarificationRepository.save(clarificationSecond)
        when:
        clarificationService.changeClarification(clarificationRepository.findAll().get(0).getId(), "valueOne", userTeacher)
        clarificationService.changeClarification(clarificationRepository.findAll().get(1).getId(), "valueSecond", userTeacher)
        then:
        clarificationRepository.getUnansweredClarifications().size() == 0;
        clarificationRepository.getAnsweredClarifications().size() == 2;
        clarificationRepository.findAll().get(0).getTeacherClarification() == "valueOne"
        clarificationRepository.findAll().get(0).getAnswerDate() != null
        clarificationRepository.findAll().get(1).getTeacherClarification() == "valueSecond"
        clarificationRepository.findAll().get(1).getAnswerDate() != null
    }

    def 'Create a clarification with a empty text'() {
        given:
        clarification = clarificationRepository.findAll().get(0)
        when:
        clarificationService.changeClarification(clarification.getId(), "", userTeacher)
        then:
        TutorException exception = thrown()
        exception.getErrorMessage() == CLARIFICATION_TEXT_IS_EMPTY
        clarificationRepository.getAnsweredClarifications().size() == 0;
    }

    def 'Clarification not found'(){
        when:
        clarificationService.changeClarification(3, "value", userTeacher)
        then:
        TutorException exception = thrown()
        exception.getErrorMessage() == CLARIFICATION_NOT_FOUND
    }

    def 'Student creates a clarification'() {
        when:
        clarificationService.changeClarification(clarificationRepository.findAll().get(0).getId(), "value", userStudentOne)
        then:
        TutorException exception = thrown()
        exception.getErrorMessage() == USER_WITHOUT_PERMISSIONS
        clarificationRepository.getAnsweredClarifications().size() == 0;
        clarificationRepository.getUnansweredClarifications().size() == 1;
    }

    def 'Student sees clarification sent by the teacher'(){
        given:
        def answer
        clarificationService.changeClarification(clarificationRepository.findAll().get(0).getId(), "value", userTeacher)
        when:
        answer = clarificationService.getAnswer(clarificationRepository.findAll().get(0).getId())
        then:
        clarificationRepository.getAnsweredClarifications().size() == 1;
        clarificationRepository.getUnansweredClarifications().size() == 0;
        answer == "value"
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
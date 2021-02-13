package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service

import io.swagger.models.auth.In
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
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationDto
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

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_NOT_FOUND

@DataJpaTest
class GetClarificationsByQuestionTest extends Specification {
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
        clarification.setPrivacy(Clarification.Privacy.PUBLIC)
        clarificationRepository.save(clarification)

        questionAnswerRepository.save(questionAnswerSecond)
        userRepository.save(userStudentSecond)
        questionRepository.save(question)
        quizQuestionRepository.save(quizQuestion)
        quizAnswerRepository.save(quizAnswerSecond)
    }

    def 'Get clarifications by certain question'() {
        given:
        clarificationSecond = new Clarification("ClarificationRequestContentSecond", userStudentSecond.getUsername(), questionAnswerRepository.findAll().get(1))
        clarificationRepository.save(clarificationSecond)
        Question questionTemp = questionRepository.findByKey(1)
                .orElseThrow({ -> new TutorException(QUESTION_NOT_FOUND) })
        QuizQuestion quizQuestionTemp = questionTemp.getQuizQuestions()[0];
        List<ClarificationDto> clarificationDtoList = new ArrayList<ClarificationDto>()
        when:
        clarificationDtoList = clarificationService.getClarificationsByQuestion(quizQuestionTemp.getId())
        then:
        clarificationDtoList.size() == 1
        clarificationService.getClarifications().size() == 2
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

package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User

import spock.lang.Specification

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@DataJpaTest
class CreateTournamentTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final int NUMBER_OF_QUESTIONS = 2
    public static final String USER_NAME = "Nuno Ramos"
    public static final String USER_USERNAME = "Miguel Silva"
    public static final int USER_KEY = 123
    public static final User.Role USER_ROLE = User.Role.STUDENT


    @Autowired
    TournamentService tournamentService

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    TopicRepository topicRepository

    @Autowired
    QuestionRepository questionRepository

    def tournament
    def startTime
    def endTime
    def formatter

    def setup() {
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        startTime = LocalDateTime.now()
        endTime = LocalDateTime.now().plusDays(1)

        def topic = new Topic()
        topic.setName("TESTE")
        topic.setCourse(null)
        topicRepository.save(topic)


        tournament = new Tournament()
        tournament.setId(1)
        startTime = LocalDateTime.now().plusDays(1)
        endTime = LocalDateTime.now().plusDays(2)
        tournament.setEnrolledStudents(new ArrayList<User>() as Set<User>)
    }

    def "create a tournament"() {
        given: 'correct information'
        tournament.setCreator(1)
        tournament.setNumberQuestions(0)
        tournament.setStartTime(startTime)
        tournament.setEndTime(endTime)
        tournament.setCourseExecutionId(1)
        tournament.setTopic(topicRepository.findAll().get(0).getId())

        when:
        tournamentService.createTournament(new TournamentDto(tournament))

        then: "the tournament is created correctly"
        tournamentRepository.count() == 1L
        def result = tournamentRepository.findAll().get(0)
        result.getId() != 0
        result.getStartTime() != null
        result.getEndTime().format(formatter) == endTime.format(formatter)
        result.getCourseExecutionId() != 0
        result.getCreatorId() != 0
        result.getEnrolledStudents() != null
        result.getTopicId() != 0
    }

    def "create a tournament with no user"() {
        given: 'no creator'
        tournament.setNumberQuestions(0)
        tournament.setStartTime(startTime)
        tournament.setEndTime(endTime)
        tournament.setCourseExecutionId(1)
        tournament.setTopic(topicRepository.findAll().get(0).getId())


        when:
        tournamentService.createTournament(new TournamentDto(tournament))

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_USER_IS_EMPTY
        tournamentRepository.count() == 0L
    }

    def "create a tournament no start date"() {
        given: 'no start date'
        tournament.setCreator(1)
        tournament.setNumberQuestions(0)
        tournament.setEndTime(endTime)
        tournament.setCourseExecutionId(1)
        tournament.setTopic(topicRepository.findAll().get(0).getId())


        when:
        tournamentService.createTournament(new TournamentDto(tournament))

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.INVALID_START_DATE
        tournamentRepository.count() == 0L
    }

    def "create a tournament no end date"() {
        given: 'no end date'
        tournament.setCreator(1)
        tournament.setNumberQuestions(0)
        tournament.setStartTime(startTime)
        tournament.setCourseExecutionId(1)
        tournament.setTopic(topicRepository.findAll().get(0).getId())


        when:
        tournamentService.createTournament(new TournamentDto(tournament))

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.INVALID_END_DATE
        tournamentRepository.count() == 0L
    }

    def "create a tournament no course"() {
        given: 'create no course given'
        tournament.setCreator(1)
        tournament.setNumberQuestions(0)
        tournament.setStartTime(startTime)
        tournament.setEndTime(endTime)
        tournament.setTopic(topicRepository.findAll().get(0).getId())


        when:
        tournamentService.createTournament(new TournamentDto(tournament))

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.COURSE_EXECUTION_NOT_FOUND
        tournamentRepository.count() == 0L
    }

    def "create a tournament invalid number of questions"() {
        given: 'create invalid number of questions given'
        tournament.setCreator(1)
        tournament.setStartTime(startTime)
        tournament.setEndTime(endTime)
        tournament.setCourseExecutionId(1)
        tournament.setTopic(topicRepository.findAll().get(0).getId())


        when:
        tournament.setNumberQuestions(-1)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.NOT_ENOUGH_QUESTIONS
        tournamentRepository.count() == 0L
    }

    def "create a tournament no topic"() {
        given: 'create no topic given'
        tournament.setCreator(1)
        tournament.setStartTime(startTime)
        tournament.setNumberQuestions(0)
        tournament.setEndTime(endTime)
        tournament.setCourseExecutionId(1)

        when:
        tournamentService.createTournament(new TournamentDto(tournament))

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOPIC_NOT_FOUND
        tournamentRepository.count() == 0L

    }


    @TestConfiguration
    static class TournamentServiceTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }

}
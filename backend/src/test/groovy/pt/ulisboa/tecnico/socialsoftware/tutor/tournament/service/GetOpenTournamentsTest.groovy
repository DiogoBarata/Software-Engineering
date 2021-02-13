package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
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
class GetOpenTournamentsTest extends Specification {

    @Autowired
    TournamentService tournamentService

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    TopicRepository topicRepository

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
        startTime = LocalDateTime.now().plusHours(1)
        endTime = LocalDateTime.now().plusDays(1)
        tournament.setStartTime(startTime)
        tournament.setEndTime(endTime)
        tournament.setCourseExecutionId(1)
        tournament.setTopic(topicRepository.findAll().get(0).getId())
        tournament.setCreator(1)
        tournament.setEnrolledStudents(new ArrayList<User>() as Set<User>)
        tournament.setNumberQuestions(0)
    }

    def "no open tournaments"() {
        given: 'no tournament exists'


        expect: "the tournament is created correctly"
        tournamentRepository.count() == 0L
    }

    def "one tournament but is in execution"() {
        given: 'one tournament started yesterday'
        tournament.setStartTime(LocalDateTime.now().minusDays(1))

        when:
        tournamentService.createTournament(new TournamentDto(tournament))

        then:
        tournamentRepository.count() == 1L
        tournamentRepository.findAllOpen().size() == 0
    }

    def "one valid tournament"() {
        given: 'one tournament starts in one hour'
        tournament.setStartTime(LocalDateTime.now().plusHours(1))

        when:
        tournamentService.createTournament(new TournamentDto(tournament))

        then:
        tournamentRepository.count() == 1L
        tournamentService.findAllOpen().size() == 1
        def result = tournamentService.findAllOpen().get(0)
        result.getId() != 0
        result.getStartTime() != null
        result.getEndTime().format(formatter) == endTime.format(formatter)
        result.getCourseExecutionId() != 0
        result.getCreatorId() != 0
        result.getEnrolledStudents() != null
        result.getTopicId() != 0
        result.getNumberQuestions() == 0
    }


    @TestConfiguration
    static class TournamentServiceTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }

}
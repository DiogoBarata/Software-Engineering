package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import spock.lang.Specification

import java.time.LocalDateTime

@DataJpaTest
class GetTournamentServiceSpockPerformanceTest extends Specification {
    static final int CREATOR_ID = 1
    static final LocalDateTime START_TIME = LocalDateTime.now().plusHours(1)
    static final LocalDateTime END_TIME = LocalDateTime.now().plusHours(2)
    static final int TOPIC_ID = 2
    static final int NUMBER_QUESTIONS = 3
    static final int COURSE_EXECUTION_ID = 5

    @Autowired
    TournamentService tournamentService

    @Autowired
    TournamentRepository tournamentRepository

    def "performance testing to get 1000 tournaments"() {
        given: "a tournament"
        def tournamentDto = new TournamentDto()
        tournamentDto.setCreatorId(CREATOR_ID)
        tournamentDto.setStartTime(START_TIME)
        tournamentDto.setEndTime(END_TIME)
        tournamentDto.setTopicId(TOPIC_ID)
        tournamentDto.setNumberQuestions(NUMBER_QUESTIONS)
        tournamentDto.setCourseExecutionId(COURSE_EXECUTION_ID)

        and: "a 1000 tournaments"
        1.upto(2, {
            tournamentRepository.save(new Tournament(tournamentDto))
        })

        when:
        1.upto(2, { tournamentService.getTournaments()})

        then:
        true
    }

    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }

    }
}

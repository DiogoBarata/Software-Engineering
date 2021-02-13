package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.StudentDto
import spock.lang.Specification
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@DataJpaTest
class EnrollStudentTest extends Specification {

    @Autowired
    TournamentService tournamentService

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    TopicRepository topicRepository

    @Autowired
    UserRepository userRepository

    def tournament
    def startTime
    def endTime
    def formatter

    def user

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


        tournamentService.createTournament(new TournamentDto(tournament))

        user = new User();
        user.setKey(1)
        user.setName("Nuno")
        user.setUsername("Miguel")
        user.setRole(User.Role.STUDENT)
        user.setId(1)
    }

    def "enroll successfully"() {
        given: 'tournament with correct time frame, user is student'
        user.setRole(User.Role.STUDENT)
        userRepository.save(user)

        when:
        tournamentService.enrollStudent(tournamentService.getTournaments()[0].getId(), user.getUsername());
        def result = tournamentService.getEnrolledStudents(tournamentService.getTournaments()[0].getId())
        user = new StudentDto(user)

        then: "the tournament is created correctly"
        result.size() == 1
        def enrolledStudent = result[0]
        enrolledStudent.getName() == user.getName()
        enrolledStudent.getUsername() == user.getUsername()
        enrolledStudent.getCreationDate() == user.getCreationDate()

    }

    def "enroll as a teacher"() {
        given: 'correct conditions except user is a teacher'
        user.setRole(User.Role.TEACHER)
        userRepository.save(user)

        when:
        tournamentService.enrollStudent(tournamentService.getTournaments()[0].getId(), user.getUsername());

        then: "user is unable to enroll, throws exception"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.USER_NOT_STUDENT
        tournamentService.getEnrolledStudents(tournamentService.getTournaments()[0].getId()).size() == 0
    }

    def "enroll twice into the same tournament"() {
        given: 'correct conditions, user attempts to enroll twice'
        user.setRole(User.Role.STUDENT)
        userRepository.save(user)

        when:
        tournamentService.enrollStudent(tournamentService.getTournaments()[0].getId(), user.getUsername());
        tournamentService.enrollStudent(tournamentService.getTournaments()[0].getId(), tournamentService.getTournaments()[0].getEnrolledStudents()[0].getUsername());

        then: "exception is thrown, user appears once, not twice"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.STUDENT_ALREADY_ENROLLED
        tournamentService.getEnrolledStudents(tournamentService.getTournaments()[0].getId()).size() == 1
    }


    @TestConfiguration
    static class TournamentServiceTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }

}
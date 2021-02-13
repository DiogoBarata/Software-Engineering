package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.codehaus.groovy.transform.tailrec.ReturnAdderForClosures;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.StudentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TournamentService {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> getTournaments() {
        return tournamentRepository.findAll().stream().map(TournamentDto::new).collect(Collectors.toList());
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> findAllOpen() {
        return tournamentRepository.findAllOpen(LocalDateTime.now()).stream().map(TournamentDto::new)
                .sorted(Comparator.comparing(TournamentDto::getId)).collect(Collectors.toList());
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> findAllEnrollable(String username, int executionId) {
        int userId = userRepository.findByUsername(username).getId();
        return tournamentRepository.findAllEnrollable(LocalDateTime.now(), userId, executionId).stream().map(TournamentDto::new)
                .sorted(Comparator.comparing(TournamentDto::getId)).collect(Collectors.toList());
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> findByCreatorId(int CreatorId) {
        return tournamentRepository.findByCreator(LocalDateTime.now(), CreatorId).stream().map(TournamentDto::new)
                .sorted(Comparator.comparing(TournamentDto::getId)).collect(Collectors.toList());
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> findById(int id) {
        return tournamentRepository.findById(id).stream().map(TournamentDto::new)
                .sorted(Comparator.comparing(TournamentDto::getId)).collect(Collectors.toList());
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto createTournament(TournamentDto tournamentDto) {
        Tournament tournament = tournamentRepository.findById(tournamentDto.getId()).orElse(null);
        if (tournament == null) {
            if (topicRepository.findById(tournamentDto.getTopicId()).orElse(null) == null) {
                throw new TutorException(ErrorMessage.TOPIC_NOT_FOUND, tournamentDto.getTopicId());
            }
            if (questionRepository.findByTopic(tournamentDto.getTopicId()).size() < tournamentDto.getNumberQuestions()) {
                throw new TutorException(ErrorMessage.NOT_ENOUGH_QUESTIONS_IN_TOPIC);
            }
            tournament = new Tournament(tournamentDto);
            tournamentRepository.save(tournament);
            return tournamentDto;
        } else {
            throw new TutorException(ErrorMessage.DUPLICATE_TOURNAMENT);
        }
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto enrollStudent(int tournamentId, String username) throws TutorException {
        Tournament tournament = tournamentRepository.findById(tournamentId).orElse(null);
        if (tournament == null) {
            throw new TutorException(ErrorMessage.TOURNAMENT_NOT_FOUND);
        } else {
            User user = userRepository.findByUsername(username);
            tournament.enrollStudent(user);
            tournamentRepository.save(tournament);
            return new TournamentDto(tournament);
        }
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<UserDto> getEnrolledStudents(int tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId).orElse(null);
        if (tournament == null) {
            return new ArrayList<>();
        }
        return tournament.getEnrolledStudents().stream().filter(user -> user.getRole().equals(User.Role.STUDENT))
                .sorted(Comparator.comparing(User::getUsername)).map(UserDto::new).collect(Collectors.toList());
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void cancelTournament(int tournamentId) {
        tournamentRepository.deleteEnrolledByTournamentId(tournamentId);
        tournamentRepository.deleteById(tournamentId);
        return;
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public int getEnrolledStudentsCount(int tournamentId) {
        return tournamentRepository.getEnrolledStudensCount(tournamentId);
    }
}

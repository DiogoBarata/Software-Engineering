package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(name = "tournaments")
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToMany(cascade = CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinTable(
        name = "enrolled_students",
        joinColumns = { @JoinColumn(name = "tournament_id")},
        inverseJoinColumns = { @JoinColumn(name = "user_id")}
    )
    private Set<User> enrolledStudents = new HashSet<>();
    private int creatorId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int topicId;
    private int numberQuestions;
    private int courseExecutionId;

    public Tournament() {
        enrolledStudents = new HashSet<User>();
    }

    public Tournament(TournamentDto tournamentDto) {
        setCreator(tournamentDto.getCreatorId());
        setStartTime(tournamentDto.getStartTime());
        setEndTime(tournamentDto.getEndTime());
        setTopic(tournamentDto.getTopicId());
        setNumberQuestions(tournamentDto.getNumberQuestions());
        setCourseExecutionId(tournamentDto.getCourseExecutionId());
    }

    // Main functions
    public void enrollStudent(User newUser) {
        if (!newUser.getRole().equals(User.Role.STUDENT)) {
            throw new TutorException(USER_NOT_STUDENT);
        }
        for (User enrolledStudent : enrolledStudents) {
            if (enrolledStudent.getId().equals(newUser.getId())) {
                throw new TutorException(STUDENT_ALREADY_ENROLLED);
            }
        }
        enrolledStudents.add(newUser);
    }

    // Getters
    public Set<User> getEnrolledStudents() {
        return enrolledStudents;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public int getTopicId() {
        return topicId;
    }

    public int getNumberQuestions() {
        return numberQuestions;
    }

    public int getCourseExecutionId() {
        return courseExecutionId;
    }

    public int getId() {
        return id;
    }

    public int getCreatorId() {
        return creatorId;
    }

    // Setters
    public void setEnrolledStudents(Set<User> enrolledStudents) {
        this.enrolledStudents = enrolledStudents;
    }

    public void setCreator(int creator) {

        if (creator <= 0) {
            throw new TutorException(TOURNAMENT_USER_IS_EMPTY);
        }
        this.creatorId = creator;
    }

    public void setStartTime(LocalDateTime startTime) {
        // This method exists for testing purposes, it is the same as setStartTime but
        // does not verify the input
        if (startTime == null) {
            throw new TutorException(INVALID_START_DATE);
        }
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        if (endTime == null || endTime.isBefore(LocalDateTime.now())
                || (startTime != null && endTime.isBefore(startTime))) {
            throw new TutorException(INVALID_END_DATE);
        }
        this.endTime = endTime;
    }

    public void setTopic(int topic) {
        if (topic < 0) {
            throw new TutorException(TOPIC_NOT_FOUND);
        }
        this.topicId = topic;
    }

    public void setNumberQuestions(int numberQuestions) {
        if (numberQuestions <0) {
            throw new TutorException(NOT_ENOUGH_QUESTIONS);
        }
        this.numberQuestions = numberQuestions;
    }

    public void setCourseExecutionId(int course) {
        if (course <= 0) {
            throw new TutorException(COURSE_EXECUTION_NOT_FOUND);
        }
        this.courseExecutionId = course;
    }

    public void setId(int id) {
        this.id = id;
    }
}

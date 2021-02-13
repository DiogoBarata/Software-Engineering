package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class TournamentDto  implements Serializable {


    @OneToMany(cascade = CascadeType.ALL, fetch= FetchType.LAZY, orphanRemoval=true)
    private Set<UserDto> enrolledStudents;
    private int creatorId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int topicId;
    private int numberQuestions;
    private int courseExecutionId;
    private int id;

    public TournamentDto() {
        enrolledStudents = new HashSet<UserDto>();
    }

    public TournamentDto(Tournament tournament) {
        this.enrolledStudents = tournament.getEnrolledStudents().stream().map(UserDto::new).collect(Collectors.toSet());
        this.creatorId = tournament.getCreatorId();
        this.startTime = tournament.getStartTime();
        this.endTime = tournament.getEndTime();
        this.topicId = tournament.getTopicId();
        this.numberQuestions = tournament.getNumberQuestions();
        this.courseExecutionId = tournament.getCourseExecutionId();
        this.id = tournament.getId();
    }

    public Set<UserDto> getEnrolledStudents() {
        return enrolledStudents;
    }

    public void setEnrolledStudents(Set<UserDto> enrolledStudents) {
        this.enrolledStudents = enrolledStudents;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creator) {
        this.creatorId = creator;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topic) {
        this.topicId = topic;
    }

    public int getNumberQuestions() {
        return numberQuestions;
    }

    public void setNumberQuestions(int numberQuestions) {
        this.numberQuestions = numberQuestions;
    }

    public int getCourseExecutionId() {
        return courseExecutionId;
    }

    public void setCourseExecutionId(int course) {
        this.courseExecutionId = course;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

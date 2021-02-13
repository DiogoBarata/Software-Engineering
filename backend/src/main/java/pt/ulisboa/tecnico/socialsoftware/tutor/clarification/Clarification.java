package pt.ulisboa.tecnico.socialsoftware.tutor.clarification;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;

import javax.persistence.*;
import java.time.LocalDateTime;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(name = "clarifications")
public class Clarification {
    public enum State {ANSWERED, NOT_ANSWERED}

    public enum Privacy {PUBLIC, PRIVATE}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private Clarification.State state = Clarification.State.NOT_ANSWERED;

    @Enumerated(EnumType.STRING)
    private Clarification.Privacy privacy = Clarification.Privacy.PRIVATE;

    @Column(name = "student_request")
    private String studentRequest;

    @Column(name = "teacher_clarification")
    private String teacherClarification;

    @Column(name = "additional_student_request")
    private String additionalStudentRequest;

    @Column(name = "additional_teacher_clarification")
    private String additionalTeacherClarification;

    @Column(name = "student_username")
    private String studentUsername;

    @Column(name = "request_date")
    private LocalDateTime requestDate;

    @Column(name = "answer_date")
    private LocalDateTime answerDate;

    @ManyToOne
    @JoinColumn(name = "question_clarification_id")
    private QuestionAnswer questionAnswer;

    public Clarification() {}

    public Clarification(QuestionAnswer questionAnswer, ClarificationDto clarificationDto){
        if(questionAnswer == null){
            throw new TutorException(ErrorMessage.QUESTION_ANSWER_NOT_FOUND);
        }
        if(clarificationDto == null){
            throw new TutorException(ErrorMessage.CLARIFICATION_NOT_FOUND);
        }
        validateClarificationDto(clarificationDto);

        this.id = clarificationDto.getId();
        this.state = clarificationDto.getState();
        this.studentRequest = clarificationDto.getStudentRequest();
        this.teacherClarification = clarificationDto.getTeacherClarification();
        this.additionalStudentRequest = clarificationDto.getAdditionalStudentRequest();
        this.additionalTeacherClarification = clarificationDto.getAdditionalTeacherClarification();
        this.studentUsername = clarificationDto.getStudentUsername();
        this.requestDate = LocalDateTime.now();
        this.answerDate = clarificationDto.getAnswerDateDate();
        this.questionAnswer = questionAnswer;
        questionAnswer.addClarification(this);
    }

    public Clarification(String text, String name, QuestionAnswer questAnswer) {
        if(text == null || text.isEmpty()){
            throw new TutorException(CLARIFICATION_TEXT_IS_EMPTY);
        }

        studentRequest = text;
        studentUsername = name;
        requestDate = LocalDateTime.now();
        questionAnswer = questAnswer;
        questAnswer.getClarifications().add(this);
    }

    private void validateClarificationDto(ClarificationDto clarificationDto) {
        if(clarificationDto.getStudentRequest() == null || clarificationDto.getStudentRequest().isEmpty()){
            throw new TutorException(CLARIFICATION_TEXT_IS_EMPTY);
        }
    }

    public void delete() {
        questionAnswer.getClarifications().remove(this);
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public State getState() { return state; }

    public void setState(State state) { this.state = state; }

    public String getStudentRequest() { return studentRequest; }

    public void setStudentRequest(String studentRequest) { this.studentRequest = studentRequest; }

    public String getTeacherClarification() { return teacherClarification; }

    public void setTeacherClarification(String teacherClarification) {
        this.teacherClarification = teacherClarification;
    }

    public String getStudentUsername() { return studentUsername; }

    public void setStudentUsername(String studentUsername) { this.studentUsername = studentUsername; }

    public LocalDateTime getRequestDate() { return requestDate; }

    public void setRequestDate(LocalDateTime requestDate) { this.requestDate = requestDate; }

    public LocalDateTime getAnswerDate() { return answerDate; }

    public void setAnswerDate(LocalDateTime answerDate) { this.answerDate = answerDate; }

    public QuestionAnswer getQuestionAnswer() { return questionAnswer; }

    public void setQuestionAnswer(QuestionAnswer questionAnswer) { this.questionAnswer = questionAnswer; }

    public void setAnswerDateAuto(){
        this.answerDate = LocalDateTime.now();
    }

    public CourseExecution getCourseExecution(){
        return this.getQuestionAnswer().getQuizAnswer().getQuiz().getCourseExecution();
    }

    public Privacy getPrivacy() { return privacy; }

    public void setPrivacy(Clarification.Privacy privacy) { this.privacy = privacy; }

    public String getAdditionalStudentRequest() {
        return additionalStudentRequest;
    }

    public void setAdditionalStudentRequest(String additionalStudentRequest) {
        this.additionalStudentRequest = additionalStudentRequest;
    }

    public String getAdditionalTeacherClarification() {
        return additionalTeacherClarification;
    }

    public void setAdditionalTeacherClarification(String additionalTeacherClarification) {
        this.additionalTeacherClarification = additionalTeacherClarification;
    }
}

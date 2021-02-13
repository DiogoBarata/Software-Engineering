package pt.ulisboa.tecnico.socialsoftware.tutor.clarification;

import org.springframework.data.annotation.Transient;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.QuestionAnswerDto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClarificationDto implements Serializable {
    private Integer id;
    private Clarification.State state;
    private Clarification.Privacy privacy;
    private String studentRequest;
    private String teacherClarification;
    private String additionalStudentRequest;
    private String additionalTeacherClarification;
    private String studentUsername;
    private String requestDate = null;
    private String answerDate = null;
    private QuestionAnswerDto questionAnswer;

    @Transient
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ClarificationDto(){

    }

    public ClarificationDto(Clarification clarification) {
        this.id = clarification.getId();
        this.state = clarification.getState();
        this.privacy = clarification.getPrivacy();
        this.studentRequest = clarification.getStudentRequest();
        this.teacherClarification = clarification.getTeacherClarification();
        this.additionalStudentRequest = clarification.getAdditionalStudentRequest();
        this.additionalTeacherClarification = clarification.getAdditionalTeacherClarification();
        this.studentUsername = clarification.getStudentUsername();
        this.questionAnswer = new QuestionAnswerDto(clarification.getQuestionAnswer());

        if (clarification.getRequestDate() != null)
            this.requestDate = clarification.getRequestDate().format(formatter);
        if (clarification.getAnswerDate() != null)
            this.answerDate = clarification.getAnswerDate().format(formatter);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Clarification.State getState() {
        return state;
    }

    public void setState(Clarification.State state) {
        this.state = state;
    }

    public String getStudentRequest() {
        return studentRequest;
    }

    public void setStudentRequest(String studentRequest) {
        this.studentRequest = studentRequest;
    }

    public String getTeacherClarification() {
        return teacherClarification;
    }

    public void setTeacherClarification(String teacherClarification) {
        this.teacherClarification = teacherClarification;
    }

    public String getStudentUsername() {
        return studentUsername;
    }

    public void setStudentUsername(String studentUsername) {
        this.studentUsername = studentUsername;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getAnswerDate() {
        return answerDate;
    }

    public void setAnswerDate(String answerDate) {
        this.answerDate = answerDate;
    }

    public DateTimeFormatter getFormatter() {
        return formatter;
    }

    public void setFormatter(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    public LocalDateTime getRequestDateDate() {
        if (getRequestDate() == null || getRequestDate().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(getRequestDate(), formatter);
    }

    public LocalDateTime getAnswerDateDate() {
        if (getAnswerDate() == null || getAnswerDate().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(getAnswerDate(), formatter);
    }

    public QuestionAnswerDto getQuestionAnswerDto() {
        return questionAnswer;
    }

    public Clarification.Privacy getPrivacy() {
        return privacy;
    }

    public void setPrivacy(Clarification.Privacy privacy) {
        this.privacy = privacy;
    }

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

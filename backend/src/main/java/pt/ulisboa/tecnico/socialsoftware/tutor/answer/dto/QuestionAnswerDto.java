package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.Clarification;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;


import java.io.Serializable;
import java.util.List;

public class QuestionAnswerDto implements Serializable {
    private Integer id;
    private Integer timeTaken;
    private Integer sequence;
    private Integer quizAnswerId;
    private Integer quizQuestionId;
    private Integer optionId;
    private List<Clarification> clarifications;
    private QuestionDto question;
    private OptionDto option;



    public QuestionAnswerDto() {}

    public QuestionAnswerDto(QuestionAnswer questionAnswer) {
        this.question = new QuestionDto(questionAnswer.getQuizQuestion().getQuestion());

        if(questionAnswer.getOption() != null)
            this.option = new OptionDto(questionAnswer.getOption());
    }

    public QuestionDto getQuestion() {
        return question;
    }

    public void setQuestion(QuestionDto question) {
        this.question = question;
    }

    public OptionDto getOption() {
        return option;
    }
    public List<Clarification> getClarifications() { return clarifications; }

    public void setClarifications(List<Clarification> clarifications) { this.clarifications = clarifications; }

    public void addClarification(Clarification clarification) {this.clarifications.add(clarification); }

    public void setOption(OptionDto option) {
        this.option = option;
    }

    @Override
    public String toString() {
        return "QuestionAnswerDto{" +
                "id=" + id +
                ", timeTaken=" + timeTaken +
                ", sequence=" + sequence +
                ", quizAnswerId=" + quizAnswerId +
                ", quizQuestionId=" + quizQuestionId +
                ", optionId=" + optionId +
                '}';
    }
}
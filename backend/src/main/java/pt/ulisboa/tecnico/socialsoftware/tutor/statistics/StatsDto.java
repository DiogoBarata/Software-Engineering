package pt.ulisboa.tecnico.socialsoftware.tutor.statistics;

import java.io.Serializable;

public class StatsDto implements Serializable {
    private Integer totalQuizzes = 0;
    private Integer totalAnswers = 0;
    private Integer totalUniqueQuestions = 0;
    private float correctAnswers = 0;
    private float improvedCorrectAnswers = 0;
    private Integer uniqueCorrectAnswers = 0;
    private Integer uniqueWrongAnswers = 0;
    private Integer totalAvailableQuestions = 0;
    private Integer numberOfClarifications = 0;
    private Integer numberOfResponses = 0;

    private Integer totalSubmittedQuestions = 0;
    private Integer totalApprovedQuestions = 0;
    private Integer totalDisapprovedQuestions = 0;

    private boolean dashboard = false;

    public boolean getDashboard(){return dashboard;}
    public void setDashboard(boolean dashboard){this.dashboard = dashboard;}

    public Integer getTotalSubmittedQuestions(){ return totalSubmittedQuestions;}

    public void setTotalSubmittedQuestions(Integer totalSubmittedQuestions){
        this.totalSubmittedQuestions = totalSubmittedQuestions;
    }

    public Integer getTotalApprovedQuestions(){ return totalApprovedQuestions;}

    public void setTotalApprovedQuestions(Integer totalApprovedQuestions){
        this.totalApprovedQuestions = totalApprovedQuestions;
    }

    public Integer getTotalDisapprovedQuestions(){ return totalDisapprovedQuestions;}

    public void setTotalDisapprovedQuestions(Integer totalDisapprovedQuestions){
        this.totalDisapprovedQuestions = totalDisapprovedQuestions;
    }

    public Integer getTotalQuizzes() {
        return totalQuizzes;
    }

    public void setTotalQuizzes(Integer totalQuizzes) {
        this.totalQuizzes = totalQuizzes;
    }

    public Integer getTotalAnswers() {
        return totalAnswers;
    }

    public void setTotalAnswers(Integer totalAnswers) {
        this.totalAnswers = totalAnswers;
    }

    public Integer getTotalUniqueQuestions() {
        return totalUniqueQuestions;
    }

    public void setTotalUniqueQuestions(Integer totalUniqueQuestions) {
        this.totalUniqueQuestions = totalUniqueQuestions;
    }

    public float getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(float correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public float getImprovedCorrectAnswers() {
        return improvedCorrectAnswers;
    }

    public void setImprovedCorrectAnswers(float improvedCorrectAnswers) {
        this.improvedCorrectAnswers = improvedCorrectAnswers;
    }

    public Integer getUniqueCorrectAnswers() {
        return uniqueCorrectAnswers;
    }

    public void setUniqueCorrectAnswers(Integer uniqueCorrectAnswers) {
        this.uniqueCorrectAnswers = uniqueCorrectAnswers;
    }

    public Integer getUniqueWrongAnswers() {
        return uniqueWrongAnswers;
    }

    public void setUniqueWrongAnswers(Integer uniqueWrongAnswers) {
        this.uniqueWrongAnswers = uniqueWrongAnswers;
    }

    public Integer getTotalAvailableQuestions() {
        return totalAvailableQuestions;
    }

    public void setTotalAvailableQuestions(Integer totalAvailableQuestions) {
        this.totalAvailableQuestions = totalAvailableQuestions;
    }

    @Override
    public String toString() {
        return "StatsDto{" +
                "totalQuizzes=" + totalQuizzes +
                ", totalAnswers=" + totalAnswers +
                ", totalUniqueQuestions=" + totalUniqueQuestions +
                ", correctAnswers=" + correctAnswers +
                ", improvedCorrectAnswers=" + improvedCorrectAnswers +
                ", uniqueCorrectAnswers=" + uniqueCorrectAnswers +
                ", uniqueWrongAnswers=" + uniqueWrongAnswers +
                ", totalSubmittedQuestions=" + totalSubmittedQuestions +
                ", totalApprovedQuestions=" + totalApprovedQuestions +
                ", totalDisapprovedQuestions=" + totalDisapprovedQuestions +
                '}';
    }

    public Integer getNumberOfClarifications() {
        return numberOfClarifications;
    }

    public void setNumberOfClarifications(Integer numberOfClarifications) {
        this.numberOfClarifications = numberOfClarifications;
    }

    public Integer getNumberOfResponses() {
        return numberOfResponses;
    }

    public void setNumberOfResponses(Integer numberOfResponses) {
        this.numberOfResponses = numberOfResponses;
    }
}

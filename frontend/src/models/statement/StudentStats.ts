export default class StudentStats {
  totalQuizzes!: number;
  totalAnswers!: number;
  totalUniqueQuestions!: number;
  correctAnswers!: number;
  improvedCorrectAnswers!: number;
  totalAvailableQuestions!: number;
  totalSubmittedQuestions!: number;
  totalApprovedQuestions!: number;
  totalDisapprovedQuestions!: number;
  dashboard!: boolean;

  numberOfClarifications!: number;
  numberOfResponses!: number;

  uniqueCorrectAnswers!: number;
  uniqueWrongAnswers!: number;

  constructor(jsonObj?: StudentStats) {
    if (jsonObj) {
      this.totalQuizzes = jsonObj.totalQuizzes;
      this.totalAnswers = jsonObj.totalAnswers;
      this.totalUniqueQuestions = jsonObj.totalUniqueQuestions;
      this.correctAnswers = jsonObj.correctAnswers;
      this.improvedCorrectAnswers = jsonObj.improvedCorrectAnswers;
      this.uniqueCorrectAnswers = jsonObj.uniqueCorrectAnswers;
      this.uniqueWrongAnswers = jsonObj.uniqueWrongAnswers;
      this.totalAvailableQuestions = jsonObj.totalAvailableQuestions;

      this.totalSubmittedQuestions = jsonObj.totalSubmittedQuestions;
      this.totalApprovedQuestions = jsonObj.totalApprovedQuestions;
      this.totalDisapprovedQuestions = jsonObj.totalDisapprovedQuestions;
      this.dashboard = jsonObj.dashboard;

      this.numberOfClarifications = jsonObj.numberOfClarifications;
      this.numberOfResponses = jsonObj.numberOfResponses;

    }
  }
}

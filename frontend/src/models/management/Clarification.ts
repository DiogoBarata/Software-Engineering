import { QuestionAnswer } from '@/models/management/QuestionAnswer';

export default class Clarification {
  id: number | null = null;
  studentRequest: string = '';
  teacherClarification: string = '';
  additionalStudentRequest: string = '';
  additionalTeacherClarification: string = '';
  studentUsername: string = '';
  requestDate: string = '';
  answerDate: string = '';
  questionAnswerDto: QuestionAnswer | undefined;
  state: string = 'NOT_ANSWERED';
  privacy: string = 'PRIVATE';

  constructor(jsonObj?: Clarification) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.studentRequest = jsonObj.studentRequest;
      this.teacherClarification = jsonObj.teacherClarification;
      this.additionalStudentRequest = jsonObj.additionalStudentRequest;
      this.additionalTeacherClarification = jsonObj.additionalTeacherClarification;
      this.studentUsername = jsonObj.studentUsername;
      this.requestDate = jsonObj.requestDate;
      this.answerDate = jsonObj.answerDate;
      this.questionAnswerDto = jsonObj.questionAnswerDto;
      this.state = jsonObj.state;
      this.privacy = jsonObj.privacy;
    }
  }
}

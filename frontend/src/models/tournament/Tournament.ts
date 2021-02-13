export default class Tournament {
	id!: number;
	courseExecutionId!: number;
	topicId!: number;
	startTime: Date | undefined;
	endTime: Date | undefined;
	numberQuestions!: number;
	creatorId!: number;
	enrolledStudents: number[] = [];

	constructor(jsonObj?: Tournament) {
		if (jsonObj) {
			this.id = jsonObj.id;
			this.courseExecutionId = jsonObj.courseExecutionId;
			this.topicId = jsonObj.topicId;
			this.startTime = jsonObj.startTime;
			this.endTime = jsonObj.endTime;
			this.numberQuestions = jsonObj.numberQuestions;
			this.creatorId = jsonObj.creatorId;
			this.enrolledStudents = jsonObj.enrolledStudents;
		}
	}
}

import AnswerDetails from '@/models/management/questions/AnswerDetails';
import OpenAnswerQuestionDetails from '@/models/management/questions/OpenAnswerQuestionDetails';
import { QuestionTypes, convertToLetter } from '@/services/QuestionHelpers';

export default class OpenAnswerAnswerDetails extends AnswerDetails {
  answer!: string;
  constructor(jsonObj?: OpenAnswerAnswerDetails) {
    super(QuestionTypes.OpenAnswer);
    if (jsonObj) {
      this.answer = jsonObj.answer;
    }
  }

  isCorrect(questionDetails: OpenAnswerQuestionDetails): boolean {
    return this.answer === questionDetails.answer;
  }

  answerRepresentation(): string {
    return this.answer;
  }
}
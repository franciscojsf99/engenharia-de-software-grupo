import StatementQuestionDetails from '@/models/statement/questions/StatementQuestionDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';

export default class OpenAnswerStatementQuestionDetails extends StatementQuestionDetails {
  answer: string = '';

  constructor(jsonObj?: OpenAnswerStatementQuestionDetails) {
    super(QuestionTypes.OpenAnswer);
    if (jsonObj) {
      this.answer = jsonObj.answer || this.answer;
    }
  }
}

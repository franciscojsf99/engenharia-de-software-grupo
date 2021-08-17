import AnswerDetails from '@/models/management/questions/AnswerDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';
import ItemCombinationAnswerCombination from '@/models/management/questions/ItemCombinationAnswerCombination';

export default class ItemCombinationAnswerDetails extends AnswerDetails {
  combinations: ItemCombinationAnswerCombination[] = [];
  correct: boolean = false;

  constructor(jsonObj?: ItemCombinationAnswerDetails) {
    super(QuestionTypes.ItemCombination);
    if (jsonObj) {
      this.combinations = jsonObj.combinations;
      this.correct = jsonObj.correct;
    }
  }

  isCorrect(): boolean {
    return this.correct;
  }
  answerRepresentation(): string {
    return 'Open result';
  }
}

import StatementCorrectAnswerDetails from '@/models/statement/questions/StatementCorrectAnswerDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';
import ItemCombinationAnswerCombination from '@/models/management/questions/ItemCombinationAnswerCombination';

export default class ItemCombinationStatementCorrectAnswerDetails extends StatementCorrectAnswerDetails {
  public correctCombinations!: ItemCombinationAnswerCombination[];

  constructor(jsonObj?: ItemCombinationStatementCorrectAnswerDetails) {
    super(QuestionTypes.ItemCombination);
    if (jsonObj) {
      this.correctCombinations = jsonObj.correctCombinations || [];
    }
  }
}

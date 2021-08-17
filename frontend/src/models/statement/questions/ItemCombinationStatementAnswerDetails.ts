import StatementAnswerDetails from '@/models/statement/questions/StatementAnswerDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';
import ItemCombinationStatementCorrectAnswerDetails from '@/models/statement/questions/ItemCombinationStatementCorrectAnswerDetails';
import ItemCombinationStatementAnswerDetailsCombination
  from '@/models/statement/questions/ItemCombinationStatementAnswerDetailsCombination';

export default class ItemCombinationStatementAnswerDetails extends StatementAnswerDetails {
  public combinations!: ItemCombinationStatementAnswerDetailsCombination[];

  constructor(jsonObj?: ItemCombinationStatementAnswerDetails) {
    super(QuestionTypes.ItemCombination);
    if (jsonObj) {
      this.combinations = jsonObj.combinations || [];
    }
  }

  isQuestionAnswered(): boolean {
    return this.combinations != null && this.combinations.length > 0;
  }

  isAnswerCorrect(
    correctAnswerDetails: ItemCombinationStatementCorrectAnswerDetails
  ): boolean {
    let size = 0;
    correctAnswerDetails.correctCombinations.forEach(correctCombination => {
      this.combinations.forEach(combination => {
        if(correctCombination.rightCombinationId === combination.rightOptionId &&
          correctCombination.leftCombinationId ===
            combination.leftOptionId
        ) {
          size++;
        }
      });
    });
    return correctAnswerDetails.correctCombinations.length === size && this.combinations.length === size;
  }
}

import StatementQuestionDetails from '@/models/statement/questions/StatementQuestionDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';
import { _ } from 'vue-underscore';
import StatementCombinationOption from '@/models/statement/StatementCombinationOption';
import { CombinationOptionType } from '@/models/management/CombinationOption';

export default class ItemCombinationStatementQuestionDetails extends StatementQuestionDetails {
  leftOptions: StatementCombinationOption[] = [];
  rightOptions: StatementCombinationOption[] = [];

  constructor(jsonObj?: ItemCombinationStatementQuestionDetails) {
    super(QuestionTypes.ItemCombination);
    if (jsonObj) {
      if (jsonObj.leftOptions) {
        this.leftOptions = _.shuffle(
          jsonObj.leftOptions.map(
            (combinationOption: StatementCombinationOption) =>
              new StatementCombinationOption(combinationOption)
          )
        );
      }
      if (jsonObj.rightOptions) {
        this.rightOptions = _.shuffle(
          jsonObj.rightOptions.map(
            (combinationOption: StatementCombinationOption) =>
              new StatementCombinationOption(combinationOption)
          )
        );
      }
    }
  }

  getRightOptionsContent(): string[] {
    const rightOptionsContent: string[] = [];
    this.rightOptions.forEach((option) => rightOptionsContent.push(option.content));
    return rightOptionsContent;
  }

  getLeftCombinationOptionContentById(id: number): string {
    return this.leftOptions.filter(combinationOption => combinationOption.combinationOptionId === id)[0].content;
  }

  getRightCombinationOptionContentById(id: number): string {
    return this.rightOptions.filter(combinationOption => combinationOption.combinationOptionId === id)[0].content;
  }
}

import CombinationOption, {CombinationOptionType,} from '@/models/management/CombinationOption';
import QuestionDetails from '@/models/management/questions/QuestionDetails';
import {QuestionTypes} from '@/services/QuestionHelpers';
import Combination from '@/models/management/Combination';

export default class ItemCombinationQuestionDetails extends QuestionDetails {
  options: CombinationOption[] = [];
  combinations: Combination[] = [];

  constructor(jsonObj?: ItemCombinationQuestionDetails) {
    super(QuestionTypes.ItemCombination);

    const leftOption = new CombinationOption();
    leftOption.type = CombinationOptionType.LEFT;

    this.options = [leftOption];
    if (jsonObj) {
      this.options = jsonObj.options.map(
        (option: CombinationOption) => new CombinationOption(option)
      );
      this.combinations = jsonObj.combinations.map(
        (combination: Combination) => new Combination(combination)
      );
    }
  }

  setAsNew(): void {
    this.options.forEach((option) => {
      option.id = null;
    });
    this.options.forEach((combination) => {
      combination.id = null;
    });
  }

  getLeftOptions(): CombinationOption[] {
    return this.options.filter(
      (option) => option.type === CombinationOptionType.LEFT
    );
  }
  getRightOptions(): CombinationOption[] {
    return this.options.filter(
      (option) => option.type === CombinationOptionType.RIGHT
    );
  }

  getRightOptionsContent(): string[] {
    const rightOptions = this.getRightOptions();
    const rightOptionsContent: string[] = [];
    rightOptions.forEach((option) => rightOptionsContent.push(option.content));
    return rightOptionsContent;
  }

  getLeftCombinationOptionContentById(id: number): string {
    return this.options.filter(combinationOption => combinationOption.type === CombinationOptionType.LEFT &&
      combinationOption.id === id)[0].content;
  }

  getRightCombinationOptionContentById(id: number): string {
    return this.options.filter(combinationOption => combinationOption.type === CombinationOptionType.RIGHT &&
      combinationOption.id === id)[0].content;
  }
}

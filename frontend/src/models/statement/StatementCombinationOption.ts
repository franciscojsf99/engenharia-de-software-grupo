import { CombinationOptionType } from '@/models/management/CombinationOption';

export default class StatementCombinationOption {
  combinationOptionId!: number;
  content!: string;
  type: CombinationOptionType | undefined;

  constructor(jsonObj?: StatementCombinationOption) {
    if (jsonObj) {
      this.combinationOptionId = jsonObj.combinationOptionId;
      this.content = jsonObj.content;
      this.type = jsonObj.type;
    }
  }

  public toString = (): string => {
    return this.content;
  };
}

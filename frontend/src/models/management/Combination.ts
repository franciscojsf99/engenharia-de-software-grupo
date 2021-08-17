import CombinationOption from '@/models/management/CombinationOption';

export default class Combination {
  id: number | null = null;
  leftOption: CombinationOption = new CombinationOption();
  rightOption: CombinationOption = new CombinationOption();

  constructor(jsonObj?: Combination) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.leftOption = new CombinationOption(jsonObj.leftOption);
      this.rightOption = new CombinationOption(jsonObj.rightOption);
    }
  }
}

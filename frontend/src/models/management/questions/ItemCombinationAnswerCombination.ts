export default class ItemCombinationAnswerCombination {
  leftCombinationId!: number;
  rightCombinationId!: number;
  correct: boolean | null = null;

  constructor(jsonObj?: ItemCombinationAnswerCombination) {
    if (jsonObj) {
      this.leftCombinationId = jsonObj.leftCombinationId;
      this.rightCombinationId = jsonObj.rightCombinationId;
      this.correct = jsonObj.correct;
    }
  }
}

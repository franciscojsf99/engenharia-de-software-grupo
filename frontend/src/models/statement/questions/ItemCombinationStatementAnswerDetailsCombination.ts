export default class ItemCombinationStatementAnswerDetailsCombination {
  public leftOptionId: number | null = null;
  public rightOptionId: number | null = null;

  constructor(jsonObj?: ItemCombinationStatementAnswerDetailsCombination) {
    if (jsonObj) {
      this.leftOptionId = jsonObj.leftOptionId;
      this.rightOptionId = jsonObj.rightOptionId;
    }
  }
}

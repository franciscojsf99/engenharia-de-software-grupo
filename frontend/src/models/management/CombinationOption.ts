export enum CombinationOptionType {
  RIGHT = 'RIGHT',
  LEFT = 'LEFT',
}

export default class CombinationOption {
  id: number | null = null;
  content: string = '';
  type: CombinationOptionType | undefined;

  constructor(jsonObj?: CombinationOption) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.content = jsonObj.content;
      this.type = jsonObj.type;
    }
  }

  public toString = (): string => {
    return this.content;
  };
}

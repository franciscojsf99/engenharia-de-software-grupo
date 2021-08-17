<template>
  <div>
    <span v-html="convertMarkDown(toBold('Left Options:'))" />
    <ul id="leftOptions">
      <li v-for="option in getLeftOptions()" :key="option.id">
        <span v-html="convertMarkDown(option.content)" />
      </li>
    </ul>
    <span v-html="convertMarkDown(toBold('Right Options:'))" />
    <ul id="rightOptions">
      <li v-for="option in getRightOptions()" :key="option.id">
        <span v-html="convertMarkDown(option.content)" />
      </li>
    </ul>
    <span v-html="convertMarkDown(toBold('Combinations:'))" />
    <ul id="combinations">
      <li
        v-for="combination in questionDetails.combinations"
        :key="combination.id"
      >
        <span
          v-html="
            convertMarkDown(
              combination.leftOption.content +
                ' ' +
                toBold('->') +
                ' ' +
                combination.rightOption.content
            )
          "
        />
      </li>
    </ul>
    <div v-if="studentAnswered()">
      <span v-html="convertMarkDown(toBold('Answer:'))" />
      <ul id="studentAnswers">
        <li
          v-for="(combination, index) in answerDetails.combinations"
          :key="index"
        >
          <span
            v-html="
              convertMarkDown(
                getLeftContent(combination.leftCombinationId) +
                  ' ' +
                  toBold('->') +
                  ' ' +
                  getRightContent(combination.rightCombinationId)
              )
            "
          />
        </li>
      </ul>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Image from '@/models/management/Image';
import ItemCombinationQuestionDetails from '@/models/management/questions/ItemCombinationQuestionDetails';
import { CombinationOptionType } from '@/models/management/CombinationOption';
import ItemCombinationAnswerDetails from '@/models/management/questions/ItemCombinationAnswerDetails';

@Component
export default class ItemCombinationView extends Vue {
  @Prop() readonly questionDetails!: ItemCombinationQuestionDetails;
  @Prop() readonly answerDetails?: ItemCombinationAnswerDetails;

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
  toBold(text: string): string {
    return '**' + text + '**';
  }
  getLeftOptions() {
    return this.questionDetails.options.filter(
      (op) => op.type === CombinationOptionType.LEFT
    );
  }
  getRightOptions() {
    return this.questionDetails.options.filter(
      (op) => op.type === CombinationOptionType.RIGHT
    );
  }

  studentAnswered(): boolean {
    return this.answerDetails != undefined;
  }

  getRightContent(id: number): string {
    return this.questionDetails.getRightCombinationOptionContentById(id);
  }

  getLeftContent(id: number): string {
    return this.questionDetails.getLeftCombinationOptionContentById(id);
  }
}
</script>

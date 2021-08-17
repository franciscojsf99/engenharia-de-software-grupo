<!--
Used on:
  - ResultComponent.vue
-->

<template>
  <div>
    <v-row>
      <b v-if="answerDetails.combinations.length != 0" class="centerNoBorder">
        <p data-cy="StudentAnswers">Student Answers</p>
      </b>
    </v-row>
    <div class="item-combination-answer-result">
      <div
        v-for="(option, index) in answerDetails.combinations"
        :key="index"
        :data-cy="`AnswerCombination${index + 1}`"
        :class="{ correct: isCorrect(option) }"
      >
        <v-row>
          <v-col cols="5">
            <p class="center" :data-cy="`LeftOption${index + 1}`">
              {{ getLeftContent(option.leftOptionId) }}
            </p>
          </v-col>
          <v-col cols="2">
            <p class="center" :data-cy="`Arrow${index + 1}`">-></p>
          </v-col>
          <v-col cols="5">
            <p class="center" :data-cy="`LeftOption${index + 1}`">
              {{ getRightContent(option.rightOptionId) }}
            </p>
          </v-col>
        </v-row>
      </div>
    </div>
    <v-row>
      <b v-if="getMissingCombinations().length !== 0" class="centerNoBorder">
        <p data-cy="MissingCombinations">Missing Combinations</p>
      </b>
    </v-row>
    <div
      v-for="(option, index) in getMissingCombinations()"
      :key="index"
      :data-cy="`MissingCombination${index + 1}`"
      class="missingWarning"
    >
      <v-row>
        <v-col cols="5">
          <p class="center" :data-cy="`LeftOption${index + 1}`">
            {{ getLeftContent(option.leftCombinationId) }}
          </p>
        </v-col>
        <v-col cols="2">
          <p class="center" :data-cy="`Arrow${index + 1}`">-></p>
        </v-col>
        <v-col cols="5">
          <p class="center" :data-cy="`LeftOption${index + 1}`">
            {{ getRightContent(option.rightCombinationId) }}
          </p>
        </v-col>
      </v-row>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model, Emit } from 'vue-property-decorator';
import ItemCombinationStatementQuestionDetails from '@/models/statement/questions/ItemCombinationStatementQuestionDetails';
import ItemCombinationStatementAnswerDetails from '@/models/statement/questions/ItemCombinationStatementAnswerDetails';
import ItemCombinationStatementCorrectAnswerDetails from '@/models/statement/questions/ItemCombinationStatementCorrectAnswerDetails';
import ItemCombinationStatementAnswerDetailsCombination from '@/models/statement/questions/ItemCombinationStatementAnswerDetailsCombination';
import ItemCombinationAnswerCombination from '@/models/management/questions/ItemCombinationAnswerCombination';

@Component
export default class ItemCombinationAnswerResult extends Vue {
  @Prop(ItemCombinationStatementQuestionDetails)
  readonly questionDetails!: ItemCombinationStatementQuestionDetails;
  @Prop(ItemCombinationStatementAnswerDetails)
  answerDetails!: ItemCombinationStatementAnswerDetails;
  @Prop(ItemCombinationStatementCorrectAnswerDetails)
  readonly correctAnswerDetails!: ItemCombinationStatementCorrectAnswerDetails;

  isCorrect(option: ItemCombinationStatementAnswerDetailsCombination): boolean {
    const correctCombinations: ItemCombinationAnswerCombination[] = this
      .correctAnswerDetails.correctCombinations;

    for (let i = 0; i < correctCombinations.length; i++) {
      const correctCombination = correctCombinations[i];
      if (
        correctCombination.leftCombinationId ==
          option.leftOptionId &&
        correctCombination.rightCombinationId == option.rightOptionId
      ) {
        return true;
      }
    }
    return false;
  }

  getRightContent(id: number): string {
    return this.questionDetails.getRightCombinationOptionContentById(id);
  }

  getLeftContent(id: number): string {
    return this.questionDetails.getLeftCombinationOptionContentById(id);
  }

  getMissingCombinations(): ItemCombinationAnswerCombination[] {
    const missingCombinationsInAnswer: ItemCombinationAnswerCombination[] = [];
    this.correctAnswerDetails.correctCombinations.forEach(
      (correctCombination) => {
        let ok = false;
        this.answerDetails.combinations.forEach((combination) => {
          if (
            correctCombination.leftCombinationId ===
              combination.leftOptionId &&
            correctCombination.rightCombinationId ===
              combination.rightOptionId
          ) {
            ok = true;
          }
        });
        if(ok === false) {
          missingCombinationsInAnswer.push(correctCombination);
        }
      }
    );
    return missingCombinationsInAnswer;
  }
}
</script>

<style lang="scss" scoped>
.center {
  margin: 20px;
  border: 3px solid darkgray;
  padding: 15px;
}
.centerNoBorder {
  margin: 25px auto 0;
  padding: 15px;
}
.missingWarning {
  margin: 20px;
  border: 3px solid darkgray;
  background-color: #ffb30b;
  color: white;
}
.item-combination-answer-result {
  background-color: white;
  height: 100%;

  & > div {
    margin: 20px;
    border: 3px solid darkgray;

    &.correct {
      background-color: #299455;
      color: white;
    }
    &:not(.correct) {
      background-color: #cf2323;
      color: white;
    }
  }
}
</style>

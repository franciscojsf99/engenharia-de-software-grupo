<!--
Used on:
  - QuestionComponent.vue
-->

<template>
  <div>
    <v-row
      v-for="(option, index) in questionDetails.leftOptions"
      :key="index"
      data-cy="questionOptionsInput"
    >
      <v-col cols="6">
        <p class="center"
          :data-cy="`LeftOption${index + 1}`"
        >{{ option.content }}</p>
      </v-col>
      <v-col cols="5">
        <v-container fluid>
          <v-select
            v-model="selectedItems[index]"
            :items="questionDetails.rightOptions"
            label="Selected options"
            multiple
            :data-cy="`RightOptionsDropdown${index + 1}`"
            @change="updateCombinations"
          ></v-select>
        </v-container>
      </v-col>
    </v-row>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model, Emit } from 'vue-property-decorator';
import ItemCombinationStatementQuestionDetails from '@/models/statement/questions/ItemCombinationStatementQuestionDetails';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Image from '@/models/management/Image';
import ItemCombinationStatementAnswerDetails from '@/models/statement/questions/ItemCombinationStatementAnswerDetails';
import ItemCombinationStatementCorrectAnswerDetails from '@/models/statement/questions/ItemCombinationStatementCorrectAnswerDetails';
import StatementCombinationOption from '@/models/statement/StatementCombinationOption';
import ItemCombinationStatementAnswerDetailsCombination
  from '@/models/statement/questions/ItemCombinationStatementAnswerDetailsCombination';

@Component
export default class ItemCombinationAnswer extends Vue {
  @Prop(ItemCombinationStatementQuestionDetails)
  readonly questionDetails!: ItemCombinationStatementQuestionDetails;
  @Prop(ItemCombinationStatementAnswerDetails)
  answerDetails!: ItemCombinationStatementAnswerDetails;

  selectedItems: StatementCombinationOption[][] = [];

  updateCombinations() {
    this.answerDetails.combinations = [];
    const combinations: ItemCombinationStatementAnswerDetailsCombination[] = [];
    this.selectedItems.forEach((rightOptions, leftIndex) => {
      const leftOption: StatementCombinationOption = this.questionDetails.leftOptions[
          leftIndex
          ];
      rightOptions.forEach((rightOption) => {
        const combination: ItemCombinationStatementAnswerDetailsCombination = new ItemCombinationStatementAnswerDetailsCombination();
        combination.leftOptionId = leftOption.combinationOptionId;
        combination.rightOptionId = rightOption.combinationOptionId;
        combinations.push(combination);
      });
    });
    this.answerDetails.combinations = combinations;
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>

<style lang="scss" scoped>
.center {
  margin: 20px auto;
  width: 50%;
  border: 3px solid darkgray;
  padding: 15px;
}

.unanswered {
  .correct {
    .option-content {
      background-color: #333333;
      color: rgb(255, 255, 255) !important;
    }

    .option-letter {
      background-color: #333333 !important;
      color: rgb(255, 255, 255) !important;
    }
  }
}

.correct-question {
  .correct {
    .option-content {
      background-color: #299455;
      color: rgb(255, 255, 255) !important;
    }

    .option-letter {
      background-color: #299455 !important;
      color: rgb(255, 255, 255) !important;
    }
  }
}

.incorrect-question {
  .wrong {
    .option-content {
      background-color: #cf2323;
      color: rgb(255, 255, 255) !important;
    }

    .option-letter {
      background-color: #cf2323 !important;
      color: rgb(255, 255, 255) !important;
    }
  }
  .correct {
    .option-content {
      background-color: #333333;
      color: rgb(255, 255, 255) !important;
    }

    .option-letter {
      background-color: #333333 !important;
      color: rgb(255, 255, 255) !important;
    }
  }
}
</style>

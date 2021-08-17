<template>
  <div class="item-combination-options">
    <v-row
      v-for="(option, index) in sQuestionDetails.getLeftOptions()"
      :key="index"
      data-cy="questionOptionsInput"
    >
      <v-col cols="6">
        <v-textarea
          v-model="option.content"
          :label="`Left Option ${index + 1}`"
          :data-cy="`LeftOption${index + 1}`"
          rows="1"
          auto-grow
        ></v-textarea>
      </v-col>
      <v-col cols="5">
        <v-container fluid>
          <v-select
            v-model="selectedItems[index]"
            :items="sQuestionDetails.getRightOptions()"
            label="Selected options"
            multiple
            :data-cy="`RightOptionsDropdown${index + 1}`"
            @change="updateCombinations"
          ></v-select>
        </v-container>
      </v-col>
      <v-col>
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              :data-cy="`DeleteLeft${index + 1}`"
              small
              class="ma-1 action-button"
              v-on="on"
              @click="removeOption(index)"
              color="red"
              >close</v-icon
            >
          </template>
          <span>Remove Option</span>
        </v-tooltip>
      </v-col>
    </v-row>

    <v-row>
      <v-btn
        class="ma-auto"
        color="blue darken-1"
        @click="addLeftOption"
        data-cy="AddLeftButton"
        >Add Left Option</v-btn
      >
      <v-btn
        class="ma-auto"
        color="blue darken-1"
        @click="manageRightOptions"
        data-cy="ManageRight"
        >Manage Right Options</v-btn
      >
    </v-row>

    <manage-right-options
      v-if="rightOptions"
      v-model="rightOptions"
      :questionDetails="sQuestionDetails"
      :selectedItems="selectedItems"
    />
  </div>
</template>

<script lang="ts">
import { Component, PropSync, Vue } from 'vue-property-decorator';
import CombinationOption, {
  CombinationOptionType,
} from '@/models/management/CombinationOption';
import ItemCombinationQuestionDetails from '@/models/management/questions/ItemCombinationQuestionDetails';
import ManageRightOptions from '@/components/item-combination/ManageRightOptions.vue';
import Combination from '@/models/management/Combination';

@Component({
  components: {
    'manage-right-options': ManageRightOptions,
  },
})
export default class ItemCombinationCreate extends Vue {
  @PropSync('questionDetails', { type: ItemCombinationQuestionDetails })
  sQuestionDetails!: ItemCombinationQuestionDetails;
  rightOptions: boolean | null = null;
  selectedItems: CombinationOption[][] = [];

  mounted() {
    const leftOptions: CombinationOption[] = this.sQuestionDetails.getLeftOptions();
    const rightOptions: CombinationOption[] = this.sQuestionDetails.getRightOptions();
    this.sQuestionDetails.options = leftOptions.concat(rightOptions);
    this.sQuestionDetails.getLeftOptions().forEach((option, index) => {
      this.selectedItems.push([]);
      this.sQuestionDetails.combinations.forEach((combination) => {
        if (combination.leftOption.content === option.content) {
          this.selectedItems[index].push(combination.rightOption);
        }
      });
    });
  }

  addLeftOption() {
    const option: CombinationOption = new CombinationOption();
    option.type = CombinationOptionType.LEFT;
    this.sQuestionDetails.options.unshift(option);
    this.selectedItems.unshift([]);
    this.updateCombinations();
  }

  getLeftOptions(): CombinationOption[] {
    return this.sQuestionDetails.options.filter(
      (option) => (option.type = CombinationOptionType.LEFT)
    );
  }

  manageRightOptions() {
    this.rightOptions = true;
  }
  updateCombinations() {
    this.sQuestionDetails.combinations = [];
    const combinations: Combination[] = [];
    this.selectedItems.forEach((rightOptions, leftIndex) => {
      const leftOption: CombinationOption = this.sQuestionDetails.options[
        leftIndex
      ];
      rightOptions.forEach((rightOption) => {
        const combination: Combination = new Combination();
        combination.leftOption = leftOption;
        combination.rightOption = rightOption;
        combinations.push(combination);
        this.sQuestionDetails.combinations = combinations;
      });
    });
  }
  removeOption(index: number) {
    this.selectedItems.splice(index, 1);
    this.sQuestionDetails.options.splice(index, 1);
    this.updateCombinations();
  }
}
</script>

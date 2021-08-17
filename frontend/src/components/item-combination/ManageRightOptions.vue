<template>
  <v-dialog
      :value="dialog"
      @input="$emit('dialog', false)"
      @keydown.esc="$emit('dialog', false)"
      max-width="60%"
      max-height="70%"
  >
    <v-card class="px-5" data-cy="createOrEditQuestionDialog">
      <v-card-title>
        <span class="headline"> Manage Right Options </span>
      </v-card-title>
    </v-card>

    <v-card class="px-5" data-cy="createOrEditQuestionDialog">
      <div class="item-combination-right-options">
        <v-row
          v-for="(option, index) in sQuestionDetails.getRightOptions()"
          :key="index"
          data-cy="ManageRightOptionsInput"
        >
          <v-col cols="10">
            <v-textarea
              v-model="option.content"
              :label="`Right Option ${index + 1}`"
              :data-cy="`RightOption${index + 1}`"
              rows="1"
              auto-grow
            ></v-textarea>
          </v-col>
          <v-col>
            <v-tooltip bottom>
              <template v-slot:activator="{ on }">
                <v-icon
                  :data-cy="`DeleteRight${index + 1}`"
                  small
                  class="ma-1 action-button"
                  v-on="on"
                  @click="
                    removeOption(
                      index + sQuestionDetails.getLeftOptions().length
                    )
                  "
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
            @click="addRightOption"
            data-cy="AddRightButton"
            >Add Right Option</v-btn
          >
        </v-row>
      </div>
      <v-card-actions>
        <v-spacer />
        <v-btn
          color="green darken-1"
          @click="$emit('dialog', false)"
          data-cy="ManageOK"
          >OK</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, PropSync, Vue, Watch } from 'vue-property-decorator';
import CombinationOption, {
  CombinationOptionType,
} from '@/models/management/CombinationOption';
import ItemCombinationQuestionDetails from '@/models/management/questions/ItemCombinationQuestionDetails';
import Combination from '@/models/management/Combination';

@Component
export default class ManageRightOptions extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @PropSync('questionDetails', { type: ItemCombinationQuestionDetails })
  sQuestionDetails!: ItemCombinationQuestionDetails;
  @PropSync('selectedItems', { type: Array })
  sSelectedItems!: CombinationOption[][];

  addRightOption() {
    const option: CombinationOption = new CombinationOption();
    option.type = CombinationOptionType.RIGHT;
    this.sQuestionDetails.options.push(option);
    this.updateCombinations();
  }
  getRightOptions(): CombinationOption[] {
    return this.sQuestionDetails.options.filter(
        (option) => (option.type = CombinationOptionType.RIGHT)
    );
  }
  updateCombinations() {
    this.sQuestionDetails.combinations = [];
    const combinations: Combination[] = [];
    this.sSelectedItems.forEach((rightOptions, leftIndex) => {
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
    const option: CombinationOption = this.sQuestionDetails.options[index];
    this.sSelectedItems.forEach((rightOptions, arrayIndex) => {
      rightOptions.forEach((rightOption, rightIndex) => {
        if (rightOption.content === option.content) {
          rightOptions.splice(rightIndex, 1);
        }
      });
    });
    this.sQuestionDetails.options.splice(index, 1);
    this.updateCombinations();
  }
}
</script>

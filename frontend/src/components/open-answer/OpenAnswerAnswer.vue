<!--
Used on:
  - QuestionComponent.vue
  - ResultComponent.vue
-->

<template>
  <div class="correct_answer">
    <v-form ref="form" lazy-validation>
      <v-textarea
        solo
        v-model="answerDetails.answer"
        label="Answer"
        :rules="[(v) => !!v || 'Question answer is required']"
        auto-grow
        required
        data-cy="questionAnswerInput"
        rows="4"
      ></v-textarea>
    </v-form>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model, Emit } from 'vue-property-decorator';
import OpenAnswerStatementQuestionDetails from '@/models/statement/questions/OpenAnswerStatementQuestionDetails';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Image from '@/models/management/Image';
import OpenAnswerStatementAnswerDetails from '@/models/statement/questions/OpenAnswerStatementAnswerDetails';
import OpenAnswerStatementCorrectAnswerDetails from '@/models/statement/questions/OpenAnswerStatementCorrectAnswerDetails';

@Component
export default class OpenAnswerAnswer extends Vue {
  @Prop(OpenAnswerStatementQuestionDetails)
  readonly questionDetails!: OpenAnswerStatementQuestionDetails;
  @Prop(OpenAnswerStatementAnswerDetails)
  answerDetails!: OpenAnswerStatementAnswerDetails;
  @Prop(OpenAnswerStatementCorrectAnswerDetails)
  readonly correctAnswerDetails?: OpenAnswerStatementCorrectAnswerDetails;

  get isReadonly() {
    return !!this.correctAnswerDetails;
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>

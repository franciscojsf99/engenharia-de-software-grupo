<!--
Used on:
  - QuestionComponent.vue
  - ResultComponent.vue
-->

<template>
  <div class="correct_answer">
    <v-form ref="form" lazy-validation>
      <v-textarea
        v-model="answerDetails.answer"
        label="Student's Answer"
        auto-grow
        readonly
        data-cy="studentsAnswer"
        rows="4"
      ></v-textarea>
    </v-form>
    <v-form ref="form" lazy-validation>
      <v-textarea
        v-model="correctAnswerDetails.correctAnswer"
        label="Teacher's Answer"
        auto-grow
        readonly
        data-cy="teacherAnswer"
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
export default class OpenAnswerResult extends Vue {
  @Prop(OpenAnswerStatementQuestionDetails)
  readonly questionDetails!: OpenAnswerStatementQuestionDetails;
  @Prop(OpenAnswerStatementAnswerDetails)
  readonly answerDetails!: OpenAnswerStatementAnswerDetails;
  @Prop(OpenAnswerStatementCorrectAnswerDetails)
  readonly correctAnswerDetails!: OpenAnswerStatementCorrectAnswerDetails;

  get isReadonly() {
    return !!this.correctAnswerDetails;
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>

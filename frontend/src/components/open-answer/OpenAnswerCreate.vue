<template>
  <div class="correct_answer">
    <v-form ref="form" lazy-validation>
      <v-textarea
        v-model="sQuestionDetails.answer"
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
import { Component, Model, PropSync, Vue, Watch } from 'vue-property-decorator';
import OpenAnswerQuestionDetails from '@/models/management/questions/OpenAnswerQuestionDetails';

@Component
export default class OpenAnswerCreate extends Vue {
  @PropSync('questionDetails', { type: OpenAnswerQuestionDetails })
  sQuestionDetails!: OpenAnswerQuestionDetails;
}
</script>

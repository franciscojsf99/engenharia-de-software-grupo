<template>
    <ul>
      <span
        v-html="convertMarkDown('**[★]** ' + questionDetails.answer)"
        ref="myCmView"
        :answer="questionDetails.answer"
        :editable="false"
      />
      <span
        v-if="answerDetails && answerDetails.answer == questionDetails.answer"
        v-html="convertMarkDown('**[S]** ' + answerDetails.answer)"
        ref="myCmView"
        :answer="questionDetails.answer"
        :editable="false"
        v-bind:class="[questionDetails.answer == answerDetails ? 'font-weight-bold' : '']"
      />
      <span
        v-else-if="answerDetails && questionDetails.answer != answerDetails.answer"
        v-html="convertMarkDown('**[x]** ' + answerDetails.answer)"
        ref="myCmView"
        :answer="questionDetails.answer"
        :editable="false"
      />
    </ul>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Image from '@/models/management/Image';
import OpenAnswerQuestionDetails from '@/models/management/questions/OpenAnswerQuestionDetails';
import OpenAnswerAnswerDetails from '@/models/management/questions/OpenAnswerAnswerDetails';

@Component
export default class OpenAnswerView extends Vue {
  @Prop() readonly questionDetails!: OpenAnswerQuestionDetails;
  @Prop() readonly answerDetails?: OpenAnswerAnswerDetails;

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>

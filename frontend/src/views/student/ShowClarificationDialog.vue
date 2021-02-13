<template>
  <v-dialog
    v-model="dialog"
    @input="$emit('close-dialog')"
    @keydown.esc="$emit('close-dialog')"
    max-width="75%"
    max-height="80%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">{{
          clarification.questionAnswerDto.question.title
        }}</span>
      </v-card-title>

      <v-card-text class="text-left">
        <v-layout column wrap>
          <show-question :question="clarification.questionAnswerDto.question" />
          <v-flex>
            <p><b>Question:</b> {{ clarification.studentRequest }}</p>
          </v-flex>
          <v-flex>
            <p><b>Answer:</b> {{ clarification.teacherClarification }}</p>
          </v-flex>
          <v-flex v-if="clarification.additionalStudentRequest !== ''">
            <p><b>Question:</b> {{ clarification.additionalStudentRequest }}</p>
          </v-flex>
          <v-flex v-if="clarification.additionalStudentRequest !== ''">
            <p>
              <b>Answer:</b> {{ clarification.additionalTeacherClarification }}
            </p>
          </v-flex>
          <textarea
            v-if="
              clarification.additionalStudentRequest === '' &&
                clarification.teacherClarification !== ''
            "
            v-model="additionalRequest"
            placeholder="Write your additional clarification request"
            data-cy="additionalRequest"
          >
          </textarea>
        </v-layout>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn
          v-if="
            clarification.additionalStudentRequest === '' &&
              clarification.teacherClarification !== ''
          "
          dark
          color="blue draken-1"
          @click="additionalClarification"
          data-cy="additionalRequestButton"
          >Additional Clarification</v-btn
        >
        <v-btn dark color="blue darken-1" @click="$emit('close-dialog')"
          >Close</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import Clarification from '@/models/management/Clarification';
import ShowQuestion from '@/views/teacher/questions/ShowQuestion.vue';
import RemoteServices from '@/services/RemoteServices';

@Component({
  components: {
    'show-question': ShowQuestion
  }
})
export default class ShowClarificationDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Clarification, required: true })
  readonly clarification!: Clarification;

  additionalRequest: string = '';

  async additionalClarification() {
    this.clarification.additionalStudentRequest = this.additionalRequest;

    if (this.clarification && !this.clarification.additionalStudentRequest) {
      await this.$store.dispatch('error', 'You must write a question');
      return;
    }

    try {
      await RemoteServices.additionalClarification(
        this.clarification,
        this.clarification.id
      );
      this.$emit('close-dialog');
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }
}
</script>

<style scoped></style>

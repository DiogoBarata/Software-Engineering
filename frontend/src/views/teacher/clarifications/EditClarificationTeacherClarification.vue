<template>
  <v-dialog
    :value="dialog"
    @input="$emit('close-dialog')"
    @keydown.esc="$emit('close-dialog')"
    max-width="75%"
    max-height="80%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">
          Add Response
        </span>
      </v-card-title>

      <v-card-text class="text-left" v-if="inputClarification">
        <v-container grid-list-md fluid>
          <v-layout column wrap>
            <v-flex xs24 sm12 md8>
              <p>
                <b>Title:</b>
                {{ inputClarification.questionAnswerDto.question.title }}
              </p>
            </v-flex>
            <v-flex xs24 sm12 md8>
              <p>
                <b>Question:</b>
                {{ inputClarification.questionAnswerDto.question.content }}
              </p>
            </v-flex>
            <v-flex xs24 sm12 md8>
              <p>
                <b>Student's Username:</b>
                {{ inputClarification.studentUsername }}
              </p>
            </v-flex>
            <v-flex xs24 sm12 md8>
              <p>
                <b>Student's Question:</b>
                {{ inputClarification.studentRequest }}
              </p>
            </v-flex>
            <v-flex
              v-if="inputClarification.additionalStudentRequest !== ''"
              xs24
              sm12
              md8
            >
              <p>
                <b>Teacher's Response:</b>
                {{ inputClarification.teacherClarification }}
              </p>
            </v-flex>
            <v-flex
              v-if="inputClarification.additionalStudentRequest !== ''"
              xs24
              sm12
              md8
            >
              <p>
                <b>Additional Student's Question:</b>
                {{ inputClarification.additionalStudentRequest }}
              </p>
            </v-flex>
            <v-flex
              v-if="inputClarification.additionalStudentRequest === ''"
              xs24
              sm12
              md8
            >
              <v-text-field
                v-model="inputClarification.teacherClarification"
                label="Response"
                data-cy="Response"
              />
            </v-flex>
            <v-flex
              v-if="inputClarification.additionalStudentRequest !== ''"
              xs24
              sm12
              md8
            >
              <v-text-field
                v-model="inputClarification.additionalTeacherClarification"
                label="Response"
                data-cy="additionalResponse"
              />
            </v-flex>
          </v-layout>
        </v-container>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn
          color="blue darken-1"
          @click="$emit('close-dialog')"
          data-cy="cancelButton"
          >Cancel</v-btn
        >
        <v-btn
          v-if="inputClarification.additionalStudentRequest === ''"
          color="blue darken-1"
          @click="saveClarification"
          data-cy="saveButton"
          >Save</v-btn
        >
        <v-btn
          v-if="inputClarification.additionalStudentRequest !== ''"
          color="blue darken-1"
          @click="additionalResponse"
          data-cy="additional saveButton"
          >Save</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import Clarification from '@/models/management/Clarification';
import RemoteServices from '@/services/RemoteServices';
@Component
export default class EditClarificationTeacherClarification extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Clarification, required: true })
  readonly clarification!: Clarification;
  inputClarification!: Clarification;

  created() {
    this.inputClarification = this.clarification;
  }
  async saveClarification() {
    if (!this.inputClarification.teacherClarification) {
      await this.$store.dispatch('error', 'Clarification must have response');
      return;
    }

    if (this.inputClarification.id != null) {
      try {
        const result = await RemoteServices.updateClarification(
          this.inputClarification.id,
          this.inputClarification.teacherClarification
        );
        this.$emit('edited-clarification', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  async additionalResponse() {
    if (!this.inputClarification.additionalTeacherClarification) {
      await this.$store.dispatch('error', 'Clarification must have response');
      return;
    }

    try {
      await RemoteServices.additionalResponse(
        this.inputClarification,
        this.inputClarification.id
      );
      this.$emit('edited-clarification');
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }
}
</script>

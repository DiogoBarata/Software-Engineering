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
          Clarification Request
        </span>
      </v-card-title>

      <v-card-text class="text-left" v-if="editClarification">
        <v-layout column wrap>
          <v-flex xs24 sm12 md8>
            <p><b>User:</b> {{ editClarification.studentUsername }}</p>
          </v-flex>
          <textarea
            v-model="editClarification.studentRequest"
            placeholder="Write your question"
            data-cy="request"
          >
          </textarea>
        </v-layout>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn
          color="blue darken-1"
          @click="$emit('close-dialog')"
          data-cy="cancel button"
          >Cancel</v-btn
        >
        <v-btn
          color="blue darken-1"
          @click="saveClarification"
          data-cy="save button"
          >Save</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '../../../services/RemoteServices';
import Clarification from '../../../models/management/Clarification';
import StatementAnswer from '@/models/statement/StatementAnswer';

@Component
export default class EditClarificationDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Clarification, required: true })
  readonly clarification!: Clarification;
  @Prop({ type: StatementAnswer, required: true })
  readonly answer!: StatementAnswer;

  editClarification!: Clarification;
  statementAnswer!: StatementAnswer;

  created() {
    this.editClarification = new Clarification(this.clarification);
    this.statementAnswer = new StatementAnswer(this.answer);
    this.editClarification.studentUsername = this.$store.getters.getUser.username;
  }

  async saveClarification() {
    if (this.editClarification && !this.editClarification.studentRequest) {
      await this.$store.dispatch('error', 'You must write a question');
      return;
    }

    try {
      await RemoteServices.createClarification(
        this.editClarification,
        this.statementAnswer.id
      );
      this.$emit('close-dialog');
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }
}
</script>

<style scoped></style>

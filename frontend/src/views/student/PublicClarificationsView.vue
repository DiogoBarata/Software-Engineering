<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="clarifications"
      :hide-default-footer="true"
      :mobile-breakpoint="0"
      :search="search"
      disable-pagination
      multi-sort
    >
      <template v-slot:top>
        <v-card-title>
          <v-text-field
            v-model="search"
            append-icon="search"
            label="Search"
            class="mx-2"
          />
        </v-card-title>
      </template>
    </v-data-table>
  </v-card>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Clarification from '@/models/management/Clarification';
import Question from '@/models/management/Question';
import StatementQuestion from '@/models/statement/StatementQuestion';

@Component
export default class PublicClarificationView extends Vue {
  clarifications: Clarification[] = [];
  quizQuestionId: number | null = null;
  search: string = '';
  headers: object = [
    {
      text: 'Student Username',
      value: 'studentUsername',
      align: 'center'
    },
    {
      text: 'Request Date',
      value: 'requestDate',
      align: 'center'
    },
    {
      text: 'Question of student',
      value: 'studentRequest',
      align: 'center'
    },
    {
      text: 'Answer of Teacher',
      value: 'teacherClarification',
      align: 'center'
    },
    {
      text: 'Answer Date',
      value: 'answerDate',
      align: 'center'
    }
  ];

  editQuestion!: StatementQuestion;

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.clarifications = await RemoteServices.getPublicClarificationsByQuestion(
        this.$route.params.id
      );
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>

<style scoped></style>

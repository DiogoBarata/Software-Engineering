<template>
  <div class="container">
    <h2>Create Tournament</h2>
    <v-container class="create-buttons">
      <v-container>
        <v-row>
          <v-col cols="12" sm="6">
            <v-flex xs24 sm12 md8>
              <v-text-field v-model="tournament.topicId" label="TopicId" data-cy="topicId" />
            </v-flex>
            <v-datetime-picker
              label="*Start Time"
              format="yyyy-MM-dd HH:mm"
              v-model="tournament.startTime"
              date-format="yyyy-MM-dd"
              time-format="HH:mm"
              data-cy="startTime"
            ></v-datetime-picker>
            <v-spacer></v-spacer>
            <v-datetime-picker
              label="*Conclusion Time"
              v-model="tournament.endTime"
              date-format="yyyy-MM-dd"
              time-format="HH:mm"
              data-cy="endTime"
            ></v-datetime-picker>
            <p class="pl-0">Number of Questions</p>
            <v-btn-toggle
              data-cy="numberQuestions"
              v-model="tournament.numberQuestions"
              class="button-group"
            >
              <v-btn text value="5">5</v-btn>
              <v-btn text value="10">10</v-btn>
              <v-btn text value="20">20</v-btn>
            </v-btn-toggle>
          </v-col>
        </v-row>
      </v-container>
      <v-container>
        <v-btn
          @click="createTournament"
          depressed
          color="primary"
          data-cy="createButton"
        >Create Tournament</v-btn>
      </v-container>
    </v-container>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Watch } from 'vue-property-decorator';
import Tournament from '@/models/tournament/Tournament';
import TopicConjunction from '@/models/management/TopicConjunction';
import Assessment from '@/models/management/Assessment';
import RemoteServices from '@/services/RemoteServices';
import Question from '@/models/management/Question';
import Topic from '@/models/management/Topic';

@Component
export default class CreateTournamentsView extends Vue {
  tournament: Tournament = new Tournament();

  async created() {
    this.tournament.enrolledStudents = [];
  }

  async createTournament() {
    try {
      let updatedTournament: Tournament = await RemoteServices.saveTournament(
        this.tournament
      );
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }
}
</script>

<style lang="scss" scoped>
.create-buttons {
  width: 80% !important;
  background-color: white;
  border-width: 10px;
  border-style: solid;
  border-color: #818181;
}

.button-group {
  flex-wrap: wrap;
  justify-content: center;
}
</style>

<template>
  <div class="container">
    <h2>Available Tournaments</h2>
    <ul>
      <li class="list-header">
        <div class="col">Topic</div>
        <div class="col">Available since</div>
        <div class="col">Available until</div>
        <div class="col last-col"></div>
      </li>
      <li
        class="list-row"
        v-for="tournament in tournaments"
        :key="tournament.startTime"
        @click="enrollStudent(tournament)"
      >
        <div class="col">{{ tournament.topicId }}</div>
        <div class="col">
          {{ tournament.startTime[0].toString() + "-" + tournament.startTime[1].toString() + "-" +
          tournament.startTime[2].toString() + " " + tournament.startTime[3].toString() + ":" +
          tournament.startTime[4].toString()}}
        </div>
        <div class="col">
          {{ tournament.endTime[0].toString() + "-" + tournament.endTime[1].toString() + "-" +
          tournament.endTime[2].toString() + " " + tournament.endTime[3].toString() + ":" +
          tournament.endTime[4].toString()}}
        </div>
        <div class="col last-col">
          <i class="fas fa-chevron-circle-right"></i>
        </div>
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Tournament from '@/models/tournament/Tournament';

@Component
export default class AvailableTournamentsView extends Vue {
  tournaments: Tournament[] = [];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.tournaments = (
        await RemoteServices.getEnrollableTournaments()
      ).reverse();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async enrollStudent(tournament: Tournament) {
    await this.$store.dispatch('loading');
    try {
      await RemoteServices.enrollStudent(tournament.id);
      let numberEnrollStudents = await RemoteServices.getEnrolledStudentsCount(
        tournament.id
      );
      if (numberEnrollStudents === 1) {
        console.log(numberEnrollStudents);
        RemoteServices.createTournamentQuiz(tournament);
      }
      this.tournaments = (
        await RemoteServices.getEnrollableTournaments()
      ).reverse();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>

<style lang="scss" scoped>
.container {
  max-width: 1000px;
  margin-left: auto;
  margin-right: auto;
  padding-left: 10px;
  padding-right: 10px;

  h2 {
    font-size: 26px;
    margin: 20px 0;
    text-align: center;
    small {
      font-size: 0.5em;
    }
  }

  ul {
    overflow: hidden;
    padding: 0 5px;

    li {
      border-radius: 3px;
      padding: 15px 10px;
      display: flex;
      justify-content: space-between;
      margin-bottom: 10px;
    }

    .list-header {
      background-color: #1976d2;
      color: white;
      font-size: 14px;
      text-transform: uppercase;
      letter-spacing: 0.03em;
      text-align: center;
    }

    .col {
      flex-basis: 25% !important;
      margin: auto; /* Important */
      text-align: center;
    }

    .list-row {
      background-color: #ffffff;
      box-shadow: 0 0 9px 0 rgba(0, 0, 0, 0.1);
      display: flex;
    }
  }
}
</style>

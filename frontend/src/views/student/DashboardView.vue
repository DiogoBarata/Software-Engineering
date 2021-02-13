<template>
  <div class="container">
    <h2>Dashboard</h2>
    <div v-if="stats != null" class="stats-container">
      <div class="items">
        <div class="icon-wrapper" ref="totalSubmittedQuestions">
          <animated-number :number="stats.totalSubmittedQuestions" />
        </div>
        <div class="project-name">
          <p>Total Submitted Questions</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="totalApprovedQuestions">
          <animated-number :number="stats.totalApprovedQuestions" />
        </div>
        <div class="project-name">
          <p>Total Approved Questions</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="totalDisapprovedQuestions">
          <animated-number :number="stats.totalDisapprovedQuestions" />
        </div>
        <div class="project-name">
          <p>Total Disapproved Questions</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="totalQuizzes">
          <animated-number :number="stats.numberOfClarifications" />
        </div>
        <div class="project-name">
          <p>Number of Clarification Requests</p>
        </div>
      </div>
        <div class="items">
            <div class="icon-wrapper">
                <v-container center fluid>
                    <v-switch
                            v-model="stats.dashboard"
                            inset="true"
                            :label="'Toggle Dashboard'"
                            :input-value="stats.dashboard"
                            @change="changeDashboard(stats.dashboard)"
                    ></v-switch>
                </v-container>
            </div>
        </div>
        <div class="items">
        <div class="icon-wrapper" ref="totalAnswers">
          <animated-number :number="stats.numberOfResponses" />
        </div>
        <div class="project-name">
          <p>Number of Clarification Answers</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import AnimatedNumber from '@/components/AnimatedNumber.vue';
import StudentStats from '@/models/statement/StudentStats';

@Component({
  components: { AnimatedNumber }
})

export default class DashboardView extends Vue {
  stats: StudentStats | null = null;

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.stats = await RemoteServices.getUserStats();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
  async changeDashboard(newDashboard: boolean) {
    await this.$store.dispatch('loading');
    try {
      await RemoteServices.setUserDashboard(!newDashboard);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>

<style lang="scss" scoped>
.stats-container {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  justify-content: center;
  align-items: stretch;
  align-content: center;
  height: 100%;

  .items {
    background-color: rgba(255, 255, 255, 0.75);
    color: #1976d2;
    border-radius: 5px;
    flex-basis: 25%;
    margin: 20px;
    cursor: pointer;
    transition: all 0.6s;
  }
}

.icon-wrapper,
.project-name {
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-wrapper {
  font-size: 100px;
  transform: translateY(0px);
  transition: all 0.6s;
}

.icon-wrapper {
  align-self: end;
}

.project-name {
  align-self: start;
}
.project-name p {
  font-size: 24px;
  font-weight: bold;
  letter-spacing: 2px;
  transform: translateY(0px);
  transition: all 0.5s;
}

.items:hover {
  border: 3px solid black;

  & .project-name p {
    transform: translateY(-10px);
  }
  & .icon-wrapper i {
    transform: translateY(5px);
  }
}
</style>

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
      <template v-slot:item.action="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="showClarification(item)"
              data-cy="showClarification"
              >visibility</v-icon
            >
          </template>
          <span>
            Show Clarification
          </span>
        </v-tooltip>
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="deleteClarification(item)"
              color="red"
              data-cy="deleteClarification"
              >delete</v-icon
            >
          </template>
          <span>Delete Clarification</span>
        </v-tooltip>
      </template>
    </v-data-table>
    <show-clarification
      v-if="currentClarification"
      v-model="showClarificationDialog"
      :clarification="currentClarification"
      v-on:close-dialog="closeDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Clarification from '@/models/management/Clarification';
import RemoteServices from '@/services/RemoteServices';
import ShowClarificationDialog from '@/views/student/ShowClarificationDialog.vue';

@Component({
  components: {
    'show-clarification': ShowClarificationDialog
  }
})
export default class ClarificationsView extends Vue {
  clarifications: Clarification[] = [];
  currentClarification: Clarification | null = null;
  showClarificationDialog: boolean = false;
  search: string = '';
  headers: object = [
    {
      text: 'Title',
      value: 'questionAnswerDto.question.title',
      align: 'center'
    },
    {
      text: 'Request Date',
      value: 'requestDate',
      align: 'center'
    },
    {
      text: 'Answer Date',
      value: 'answerDate',
      align: 'center'
    },
    {
      text: 'Status',
      value: 'state',
      align: 'center'
    },
    {
      text: 'Actions',
      value: 'action',
      align: 'center',
      sortable: false
    }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.clarifications = await RemoteServices.getClarifications();
      console.log(this.clarifications);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  showClarification(clarification: Clarification) {
    this.currentClarification = clarification;
    this.showClarificationDialog = true;
  }

  closeDialog() {
    this.currentClarification = null;
    this.showClarificationDialog = false;
  }

  async deleteClarification(clarificationToDelete: Clarification) {
    if (confirm('Are you sure you want to delete this clarification?')) {
      try {
        await RemoteServices.deleteClarification(clarificationToDelete.id);
        this.clarifications = this.clarifications.filter(
          clarification => clarification.id != clarificationToDelete.id
        );
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>

<style lang="scss" scoped></style>

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
              @click="editClarification(item)"
              data-cy="editClarification"
              >edit</v-icon
            >
          </template>
          <span>
            Respond
          </span>
        </v-tooltip>
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="editPrivacyClarification(item)"
              data-cy="privacyClarification"
              >Save</v-icon
            >
          </template>
          <span>
            Change Privacy's value
          </span>
        </v-tooltip>
      </template>
    </v-data-table>
    <edit-clarification-teacher-clarification
      v-if="currentClarification"
      v-model="editClarificationDialog"
      :clarification="currentClarification"
      v-on:edited-clarification="onSaveClarification"
      v-on:close-dialog="onCloseDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Clarification from '@/models/management/Clarification';
import EditClarificationTeacherClarification from '@/views/teacher/clarifications/EditClarificationTeacherClarification.vue';
@Component({
  components: {
    'edit-clarification-teacher-clarification': EditClarificationTeacherClarification
  }
})
export default class ClarificationsView extends Vue {
  clarifications: Clarification[] = [];
  currentClarification: Clarification | null = null;
  editClarificationDialog: boolean = false;
  search: string = '';
  headers: object = [
    {
      text: 'Title',
      value: 'questionAnswerDto.question.title',
      align: 'center'
    },
    {
      text: 'Question',
      value: 'questionAnswerDto.question.content',
      align: 'center'
    },
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
      text: 'Privacy',
      value: 'privacy',
      align: 'center'
    },
    {
      text: 'State',
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
      this.clarifications = await RemoteServices.getUnansweredClarifications();
      console.log(this.clarifications);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  editClarification(clarification: Clarification) {
    this.currentClarification = clarification;
    this.editClarificationDialog = true;
  }

  async onSaveClarification() {
    this.editClarificationDialog = false;
    this.currentClarification = null;
    await this.$store.dispatch('loading');
    try {
      this.clarifications = await RemoteServices.getUnansweredClarifications();
      console.log(this.clarifications);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  onCloseDialog() {
    this.editClarificationDialog = false;
    this.currentClarification = null;
  }

  async editPrivacyClarification(clarification: Clarification) {
    if (clarification.id != null) {
      try {
        const result = await RemoteServices.updatePrivacyClarification(
          clarification.id,
          clarification
        );
        this.$emit('edited-clarification', result);
        this.onSaveClarification();
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>

<style lang="scss" scoped></style>

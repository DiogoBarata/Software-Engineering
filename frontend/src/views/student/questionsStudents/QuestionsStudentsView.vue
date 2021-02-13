<template>
  <v-card class="table">
    <v-data-table
            :headers="headers"
            :custom-filter="customFilter"
            :items="questions"
            :search="search"
            multi-sort
            :mobile-breakpoint="0"
            :items-per-page="15"
            :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
    >
      <template v-slot:top>
        <v-card-title>
          <v-text-field
                  v-model="search"
                  append-icon="search"
                  label="Search"
                  data-cy="search"
                  class="mx-2"
          />

          <v-spacer />
          <v-btn color="primary" dark @click="newQuestion"
                 data-cy="newQuestion">New Question</v-btn>
        </v-card-title>
      </template>

      <template v-slot:item.content="{ item }">
        <p
                v-html="convertMarkDownNoFigure(item.content, null)"
                @click="showQuestionDialog(item)"
        /></template>

      <template v-slot:item.topics="{ item }">
        <edit-question-topics
                :question="item"
                :topics="topics"
                v-on:question-changed-topics="onQuestionChangedTopics"
                data-cy="changeTopics"
        />
      </template>

      <template v-slot:item.image="{ item }">
        <v-file-input
                show-size
                dense
                small-chips
                @change="handleFileUpload($event, item)"
                accept="image/*"
        />
      </template>

      <template v-slot:item.action="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon small class="mr-2" v-on="on" @click="showUserDialog(item)"
                    data-cy="showUser"
            >fas fa-user</v-icon
            >
          </template>
          <span>Show User</span>
        </v-tooltip>
        <v-tooltip bottom v-if="item.numberOfAnswers === 0 && item.state === 'DISAPPROVED'" >
          <template v-slot:activator="{ on }">
            <v-icon small class="mr-2" v-on="on" @click="editQuestion(item)"
                    data-cy="editQuestion"
            >edit</v-icon
            >
          </template>
          <span>Edit Question</span>
        </v-tooltip>
        <v-tooltip bottom v-if="item.justification !== ''" >
          <template v-slot:activator="{ on }">
            <v-icon
                    small
                    class="mr-2"
                    v-on="on"
                    @click="showJustification(item)"
            >info</v-icon>
          </template>
          <span>Show Justification</span>
        </v-tooltip>
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
                    small
                    class="mr-2"
                    v-on="on"
                    @click="showQuestionDialog(item)"
                    data-cy="showQuestion"
            >visibility</v-icon
            >
          </template>
          <span>Show Question</span>
        </v-tooltip>
        <v-tooltip bottom>

        </v-tooltip>
      </template>
    </v-data-table>
    <edit-question-dialog
            v-if="currentQuestion"
            v-model="editQuestionDialog"
            :question="currentQuestion"
            v-on:save-question="onSaveQuestion"
    />
    <show-user-dialog
            v-if="currentQuestion"
            :dialog="userDialog"
            :question="currentQuestion"
            v-on:close-show-user-dialog="onCloseShowUserDialog"
    />

    <show-justification
            v-if="currentQuestion"
            v-model="justificationDialog"
            :question="currentQuestion"
            v-on:close-show-justification="onCloseShowJustification"
    />

    <show-question-dialog
            v-if="currentQuestion"
            :dialog="questionDialog"
            :question="currentQuestion"
            v-on:close-show-question-dialog="onCloseShowQuestionDialog"
    />
  </v-card>
</template>

<script lang="ts">
  import { Component, Vue, Watch } from 'vue-property-decorator';
  import RemoteServices from '@/services/RemoteServices';
  import { convertMarkDownNoFigure } from '@/services/ConvertMarkdownService';
  import Question from '@/models/management/Question';
  import Image from '@/models/management/Image';
  import Topic from '@/models/management/Topic';
  import ShowQuestionDialog from '@/views/student/questionsStudents/ShowQuestionDialog.vue';
  import EditQuestionDialog from '@/views/student/questionsStudents/EditQuestionDialog.vue';
  import EditQuestionTopics from '@/views/student/questionsStudents/EditQuestionTopics.vue';
  import ShowUser from '@/views/student/questionsStudents/ShowUser.vue';
  import ShowJustificationDialog from "@/views/student/questionsStudents/ShowJustificationDialog.vue";


  @Component({
    components: {
      'show-user-dialog': ShowUser,
      'show-question-dialog': ShowQuestionDialog,
      'edit-question-dialog': EditQuestionDialog,
      'edit-question-topics': EditQuestionTopics,
      'show-justification': ShowJustificationDialog
    }
  })
  export default class QuestionsStudentsView extends Vue {
    questions: Question[] = [];
    topics: Topic[] = [];
    currentQuestion: Question | null = null;
    editQuestionDialog: boolean = false;
    justificationDialog: boolean = false;
    questionDialog: boolean = false;
    userDialog: boolean = false;
    search: string = '';
    statusList = ['DISABLED', 'AVAILABLE', 'REMOVED'];

    headers: object = [
      { text: 'Title', value: 'title', align: 'center' },
      { text: 'Question', value: 'content', align: 'left' },
      {
        text: 'Topics',
        value: 'topics',
        align: 'center',
        sortable: false

      },
      { text: 'Status', value: 'status', align: 'center' },
      { text: 'State', value: 'state', align: 'center' },
      {
        text: 'Creation Date',
        value: 'creationDate',
        align: 'center'
      },
      {
        text: 'Image',
        value: 'image',
        align: 'center',
        sortable: false
      },
      {
        text: 'Actions',
        value: 'action',
        align: 'center',
        sortable: false
      }
    ];

    @Watch('editQuestionDialog')
    closeError() {
      if (!this.editQuestionDialog) {
        this.currentQuestion = null;
      }
    }

    async created() {
      await this.$store.dispatch('loading');
      try {
        [this.topics, this.questions] = await Promise.all([
          RemoteServices.getTopics(),
          RemoteServices.getQuestionsStudents(),
        ]);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
      await this.$store.dispatch('clearLoading');

    }

    customFilter(value: string, search: string, question: Question) {
      // noinspection SuspiciousTypeOfGuard,SuspiciousTypeOfGuard
      return (
              search != null &&
              JSON.stringify(question)
                      .toLowerCase()
                      .indexOf(search.toLowerCase()) !== -1
      );
    }

    convertMarkDownNoFigure(text: string, image: Image | null = null): string {
      return convertMarkDownNoFigure(text, image);
    }

    onQuestionChangedTopics(questionId: Number, changedTopics: Topic[]) {
      let question = this.questions.find(
              (question: Question) => question.id == questionId
      );
      if (question) {
        question.topics = changedTopics;
      }
    }

    async handleFileUpload(event: File, question: Question) {
      if (question.id) {
        try {
          const imageURL = await RemoteServices.uploadImage(event, question.id);
          question.image = new Image();
          question.image.url = imageURL;
          confirm('Image ' + imageURL + ' was uploaded!');
        } catch (error) {
          await this.$store.dispatch('error', error);
        }
      }
    }

    showQuestionDialog(question: Question) {
      this.currentQuestion = question;
      this.questionDialog = true;
    }

    showUserDialog(question: Question) {
      this.currentQuestion = question;
      this.userDialog = true;
    }

    onCloseShowQuestionDialog() {
      this.questionDialog = false;

    }

    onCloseShowUserDialog() {
      this.userDialog = false;

    }

    newQuestion() {
      this.currentQuestion = new Question();
      this.editQuestionDialog = true;
    }

    editQuestion(question: Question) {
      this.currentQuestion = question;
      this.editQuestionDialog = true;
    }

    async onSaveQuestion(question: Question) {
      this.questions = this.questions.filter(q => q.id !== question.id);
      this.questions.unshift(question);
      this.editQuestionDialog = false;
      this.currentQuestion = null;
    }

    showJustification(question: Question){
      this.currentQuestion = question;
      this.justificationDialog = true;
    }
    onCloseShowJustification() {
      this.justificationDialog = false;
    }

  }
</script>

<style lang="scss" scoped>
  .question-textarea {
    text-align: left;

    .CodeMirror,
    .CodeMirror-scroll {
      min-height: 200px !important;
    }
  }
  .option-textarea {
    text-align: left;

    .CodeMirror,
    .CodeMirror-scroll {
      min-height: 100px !important;
    }
  }
</style>

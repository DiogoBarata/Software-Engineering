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
                            class="mx-2"
                            data-cy= "searchBar"
                    />
                </v-card-title>
            </template>

            <template v-slot:item.content="{ item }">
                <p
                        v-html="convertMarkDownNoFigure(item.content, null)"
                        @click="showQuestionDialog(item)"
                />
            </template>

            <template v-slot:item.topics="{ item }">
                <edit-question-topics
                        :question="item"
                        :topics="topics"
                        v-on:question-changed-topics="onQuestionChangedTopics"
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

            <template v-slot:item.review="{ item}">
                <v-tooltip bottom v-if="item.state === 'SUBMITTED' || item.state === 'APPROVED'">
                    <template v-slot:activator="{ on }">
                        <v-icon
                                small
                                class="mr-2"
                                v-on="on"
                                @click="showApproveQuestionDialog(item)"
                                data-cy="Approved"
                        >fas fa-thumbs-up</v-icon
                        >
                    </template>
                    <span>Approved</span>
                </v-tooltip>

                <v-tooltip bottom v-if="item.state === 'SUBMITTED' || item.state === 'DISAPPROVED'">
                    <template v-slot:activator="{ on }">
                        <v-icon
                                small
                                class="mr-2"
                                v-on="on"
                                @click="showDisapproveQuestionDialog(item)"
                                data-cy="Disapproved"
                        >fas fa-thumbs-down</v-icon
                        >
                    </template>
                    <span>Disapproved</span>
                </v-tooltip>

            </template>

            <template v-slot:item.action="{ item }">
                <v-tooltip bottom>
                    <template v-slot:activator="{ on }">
                        <v-icon
                                small
                                class="mr-2"
                                v-on="on"
                                @click="showQuestionDialog(item)"
                        >visibility</v-icon
                        >
                    </template>
                    <span>Show Question</span>
                </v-tooltip>
                <v-tooltip bottom v-if="item.numberOfAnswers === 0">
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
            </template>
        </v-data-table>

        <edit-question-dialog
                v-if="currentQuestion"
                v-model="editQuestionDialog"
                :question="currentQuestion"
                v-on:save-question="onSaveQuestion"
        />
        <show-question-dialog
                v-if="currentQuestion"
                v-model="questionDialog"
                :question="currentQuestion"
                v-on:close-show-question-dialog="onCloseShowQuestionDialog"
        />
        <approve-question-dialog
            v-if="currentQuestion"
            v-model="approveQuestionDialog"
            :question="currentQuestion"
            v-on:save-question="onSaveQuestion"
        />
        <disapprove-question-dialog
            v-if="currentQuestion"
            v-model="disapproveQuestionDialog"
            :question="currentQuestion"
            v-on:save-question="onSaveQuestion"
        />
        <show-justification
                v-if="currentQuestion"
                v-model="justificationDialog"
                :question="currentQuestion"
                v-on:close-show-justification="onCloseShowJustification"
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
    import ShowQuestionDialog from '@/views/teacher/questions/ShowQuestionDialog.vue';
    import EditQuestionDialog from '@/views/teacher/questions/EditQuestionDialog.vue';
    import ShowQuestionTopics from '@/views/teacher/submittedQuestions/ShowQuestionTopics.vue';
    import ApproveQuestionDialog from "@/views/teacher/submittedQuestions/ApproveQuestionDialog.vue";
    import DisapproveQuestionDialog from "@/views/teacher/submittedQuestions/DisapproveQuestionDialog.vue";
    import ShowJustificationDialog from "@/views/student/questionsStudents/ShowJustificationDialog.vue";

    @Component({
        components: {
            'show-question-dialog': ShowQuestionDialog,
            'edit-question-dialog': EditQuestionDialog,
            'edit-question-topics': ShowQuestionTopics,
            'approve-question-dialog':ApproveQuestionDialog,
            'disapprove-question-dialog':DisapproveQuestionDialog,
            'show-justification': ShowJustificationDialog
        }
    })
    export default class QuestionsView extends Vue {
        questions: Question[] = [];
        topics: Topic[] = [];
        currentQuestion: Question | null = null;
        editQuestionDialog: boolean = false;
        questionDialog: boolean = false;
        approveQuestionDialog: boolean = false;
        disapproveQuestionDialog: boolean = false;
        justificationDialog: boolean = false;
        search: string = '';

        headers: object = [
            { text: 'Title', value: 'title', align: 'center' },
            { text: 'Question', value: 'content', align: 'left' },
            {
                text: 'Topics',
                value: 'topics',
                align: 'center',
                sortable: false
            },
            {
                text: 'Image',
                value: 'image',
                align: 'center',
                sortable: false
            },
            {
                text: 'Review',
                value: 'review',
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
                    RemoteServices.getSubmittedQuestions()
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

        onQuestionChangedTopics(questionId: Number, changedTopics: Topic[]) {
            let question = this.questions.find(
                (question: Question) => question.id == questionId
            );
            if (question) {
                question.topics = changedTopics;
            }
        }

        showQuestionDialog(question: Question) {
            this.currentQuestion = question;
            this.questionDialog = true;
        }

        onCloseShowQuestionDialog() {
            this.questionDialog = false;
        }

        showJustification(question: Question){
            this.currentQuestion = question;
            this.justificationDialog = true;
        }

        onCloseShowJustification() {
            this.justificationDialog = false;
        }

        editQuestion(question: Question) {
            this.currentQuestion = question;
            this.editQuestionDialog = true;
        }

        showApproveQuestionDialog(question: Question){
            this.currentQuestion = question;
            this.approveQuestionDialog = true
        }

        showDisapproveQuestionDialog(question: Question){
            this.currentQuestion = question;
            this.disapproveQuestionDialog = true
        }

        async onSaveQuestion(question: Question) {
            this.questions = this.questions.filter(q => q.id !== question.id);
            this.questions.unshift(question);
            this.editQuestionDialog = false;
            this.approveQuestionDialog = false;
            this.disapproveQuestionDialog = false;
            this.currentQuestion = null;
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

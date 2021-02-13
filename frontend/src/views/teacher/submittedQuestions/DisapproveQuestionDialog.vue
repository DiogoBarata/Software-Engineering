<template>
    <v-dialog
            :value="dialog"
            @input="$emit('dialog', false)"
            @keydown.esc="$emit('dialog', false)"
            max-width="75%"
            max-height="80%"
    >
        <v-card>
            <v-card-title>
                <span class="headline">
                  Why did you disapprove this question?
                </span>
            </v-card-title>
            <v-card-text class="text-left" v-if="editQuestion">
                <v-container grid-list-md fluid>
                    <v-layout column wrap>
                        <v-flex xs24 sm12 md12>
                            <v-textarea
                                    outline
                                    rows="10"
                                    v-model="editQuestion.justification"
                                    label="Justification"
                                    data-cy="justificationInput"
                            ></v-textarea>
                        </v-flex>
                    </v-layout>
                </v-container>
            </v-card-text>
            <v-card-actions>
                <v-spacer />
                <v-btn color="blue darken-1" @click="$emit('dialog', false)"
                >Cancel</v-btn
                >
                <v-btn color="blue darken-1" @click="saveQuestion"
                       data-cy="closeJustificationDisapproved"
                >Disapprove Question</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script lang="ts">
    import { Component, Model, Prop, Vue } from 'vue-property-decorator';
    import Question from '@/models/management/Question';
    import RemoteServices from '@/services/RemoteServices';

    @Component
    export default class DisapproveQuestionDialog extends Vue {
        @Model('dialog', Boolean) dialog!: boolean;
        @Prop({ type: Question, required: true }) readonly question!: Question;

        editQuestion!: Question;

        created() {
            this.editQuestion = new Question(this.question);
        }

        async saveQuestion() {
            if (
                this.editQuestion &&
                (!this.editQuestion.justification)
            ) {
                await this.$store.dispatch(
                    'error',
                    'Justification is empty'
                );
                return;
            }
            else if (this.editQuestion && this.editQuestion.state === 'DISAPPROVED') {
                await this.$store.dispatch(
                    'error',
                    'Question was already disapproved'
                );
                return;
            }
            else if (this.editQuestion && this.editQuestion.id != null) {
                try {
                    await RemoteServices.setQuestionState(this.editQuestion.id, 'DISAPPROVED');
                    const result = await RemoteServices.giveJustification(this.editQuestion.id, this.editQuestion.justification);
                    this.$emit('save-question', result);
                } catch (error) {
                    await this.$store.dispatch('error', error);
                }
            }
        }
    }
</script>

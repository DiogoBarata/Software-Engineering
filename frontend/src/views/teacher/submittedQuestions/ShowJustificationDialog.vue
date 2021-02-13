<template>
    <v-dialog
            :value="dialog"
            @keydown.esc="$emit('dialog', false)"
            max-width="75%"
    >
        <v-card>
            <v-card-title>
                <span class="headline">This questions was {{ question.state }} because: </span>
            </v-card-title>

            <v-card-text class="text-left">
                <span class="text-left">{{question.justification}}</span>
            </v-card-text>

            <v-card-actions>
                <v-spacer />
                <v-btn dark color="blue darken-1" @click="$emit('dialog')"
                       data-cy="closeJustification"
                >close</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Vue, Prop } from 'vue-property-decorator';
import Question from '@/models/management/Question';

@Component
    export default class ShowJustificationDialog extends Vue{
        @Model('dialog', Boolean) dialog!: boolean;
        @Prop({ type: Question, required: true }) readonly question!: Question;
    }
</script>

<style scoped>

</style>
describe('Justification Showcase', () => {
    beforeEach(() => {
        cy.questionForTeacher('Test-Title','Content-Title','Content-Option');
        cy.demoTeacherLoginPpa();
        cy.contains('Management').click();
        cy.get('[data-cy="teacherQuestions"]').click();
    })

    afterEach(() => {
        cy.contains('Logout').click();
    })

    it('Student submits question, teacher login, approves and gives justification', () => {
       cy.giveJustification('submited','DISABLED','Just a Random Phrase for the Justification','Test-Title');
       cy.deleteQuestion('Test-Title');
    });


});

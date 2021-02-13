describe('Teacher walkthrough', () => {
    beforeEach(() => {
        cy.demoStudentLogin()
    })

    afterEach(() => {
        cy.contains('Logout').click()
    })

    it('Solve quizz', () => {
        cy.createSolvedQuiz('Microservices')
    })

    it('login update clarification', () => {
        cy.createClarification('Clarification Request', '0/5')
        cy.contains('Logout').click()
        cy.demoTeacherLogin()
        cy.updateClarification('Demo-Student', 'resposta')
        cy.contains('Logout').click()
        cy.demoStudentLogin()
        cy.deleteClarification('ANSWERED')
    })

    it('login update clarification and tries to send empty response', () => {
        cy.createClarification('Clarification Request', '0/5')
        cy.contains('Logout').click()
        cy.demoTeacherLogin()
        cy.updateClarificationWithoutWrite('Demo-Student')

        cy.closeErrorMessageTeacher()

        cy.log('close dialog')
        cy.get('[data-cy="cancelButton"]').click()

        cy.updateClarification('Demo-Student', 'resposta')

        cy.contains('Logout').click()
        cy.demoStudentLogin()
        cy.deleteClarification('ANSWERED')
    })
});
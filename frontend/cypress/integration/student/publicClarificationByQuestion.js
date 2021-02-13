describe('Student walktrough', () => {
    beforeEach(() => {
        cy.demoStudentLogin()
    })

    afterEach(() => {
        cy.get('[data-cy="logoutButton"]').click()
    })

    it('Solve quizz', () => {
        cy.createSolvedQuiz('Performance Scenarios and Tactics')
    })

    it('Change privacy and see ClarificationList by Question', () => {
        cy.createClarification('Clarification Request', 'Performance Scenarios and Tactics')
        cy.contains('Logout').click()
        cy.demoTeacherLogin()
        cy.updatePrivacy('PRIVATE')
        cy.contains('Logout').click()
        cy.demoStudentLogin()
        cy.ClarificationListByQuestion('Performance Scenarios and Tactics')
        cy.contains('Logout').click()
        cy.demoStudentLogin()
        cy.deleteClarification('NOT_ANSWERED')
    })
})
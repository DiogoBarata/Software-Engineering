describe('Student walktrough', () => {
    beforeEach(() => {
        cy.demoStudentLogin()
    })

    afterEach(() => {
        cy.get('[data-cy="logoutButton"]').click()
    })

    it('Solve quizz', () => {
        cy.createSolvedQuiz('Designing an architecture')
    })

    it('add additional Clarification Request and Response', () => {
        cy.createClarification('Clarification Request', 'Designing an architecture')
        cy.contains('Logout').click()
        cy.demoTeacherLogin()
        cy.updateClarification('Demo-Student', 'resposta')
        cy.contains('Logout').click()
        cy.demoStudentLogin()
        cy.addAdditionalClarification('Additional request', 'ANSWERED')
        cy.demoTeacherLogin()
        cy.addAdditionalResponse('Demo-Student', 'segunda resposta')
        cy.contains('Logout').click()
        cy.demoStudentLogin()
        cy.deleteClarification('ANSWERED')
    })
})
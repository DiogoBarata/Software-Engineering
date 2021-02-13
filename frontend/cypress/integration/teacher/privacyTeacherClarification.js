describe('Teacher walktrough', () => {
    beforeEach(() => {
        cy.demoStudentLogin()
    })

    afterEach(() => {
        cy.contains('Logout').click()
    })

    it('Solve quizz', () => {
        cy.createSolvedQuiz('Quality attributes and scenarios')
    })

    it('Change privacy', () => {
        cy.createClarification('Clarification Request', '0/5')
        cy.contains('Logout').click()
        cy.demoTeacherLogin()
        cy.updatePrivacy('PRIVATE')
        cy.contains('Logout').click()
        cy.demoStudentLogin()
        cy.deleteClarification('NOT_ANSWERED')
    })
})
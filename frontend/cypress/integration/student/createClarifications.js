describe('Student walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin()
  })

  afterEach(() => {
    cy.get('[data-cy="logoutButton"]').click()
  })

  it('Solve quizz', () => {
    cy.createSolvedQuiz('Allocation viewtype')
  })

  it('login creates and deletes a clarification', () => {
    cy.createClarification('Clarification Request', '0/5')
    cy.deleteClarification('NOT_ANSWERED')
  });

  it('login creates two course executions and deletes it', () => {
    cy.createClarification('Clarification Request', '0/5')

    cy.log('try to create with the same name')
    cy.createClarification('Clarification Request', '0/5')

    cy.closeErrorMessage()

    cy.log('close dialog')
    cy.get('[data-cy="cancel button"]').click()

    cy.deleteClarification('NOT_ANSWERED')
  });

  it('login creates a clarification with an empty request', () => {
    cy.createClarificationWithoutRequest()

    cy.createErrorMessage()

    cy.get('[data-cy="cancel button"]').click()
  });

});
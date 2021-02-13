describe('Questions walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin()
    cy.contains('Questions').click()
  })

  afterEach(() => {
    cy.contains('Logout').click()
  })

  it('login creates a question and edits the content after it was disapproved', () => {
    cy.createQuestion('Demo Question','Test?','test')
    cy.wait(800)
    cy.giveDisapprovedJustification('Just a Random Phrase for the Justification 1')
    cy.wait(800)
    cy.contains('Logout').click()
    cy.demoStudentLogin()
    cy.contains('Questions').click()
    cy.updateQuestion('Question Test')
    cy.showQuestion()
    cy.giveApprovedJustification('Just a Random Phrase for the Justification 2')
    cy.contains('Management').click();
    cy.get('[data-cy="teacherQuestions"]').click();
    cy.deleteQuestion('Demo Question')

  });

  it('teacher adds an approved question to the available list', () => {
    cy.createQuestion('Demo Question','Test?','test')
    cy.wait(800)
    cy.giveApprovedJustification('Just a Random Phrase for the Justification 2')
    cy.contains('Management').click();
    cy.get('[data-cy="teacherQuestions"]').click();
    cy.changeAvailable('Demo Question', 'AVAILABLE')
    cy.deleteQuestion('Demo Question')

  });

  it('teacher edits an approved question', () => {
    cy.createQuestion('Demo Question','Test?','test')
    cy.wait(800)
    cy.giveApprovedJustification('Just a Random Phrase for the Justification 2')
    cy.updateQuestion('Question Test')
    cy.contains('Management').click();
    cy.get('[data-cy="teacherQuestions"]').click();
    cy.deleteQuestion('Demo Question')

  });

});

describe('Student walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin()
    cy.contains('Questions').click()
  })

  afterEach(() => {
  cy.deleteForStudent('Demo Question')
    cy.contains('Logout').click()
  })

  it('login creates a new question', () => {
    cy.createQuestion('Demo Question','Test?','test')


  });

  it('login creates a question and checks user', () => {
    cy.createQuestion('Demo Question','Test?','test')

    cy.showUser()


  });


});

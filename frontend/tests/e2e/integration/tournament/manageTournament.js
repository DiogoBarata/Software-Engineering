describe('Tournament Walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
    cy.get('[data-cy="tournament"').click();
    cy.get('[data-cy="createTournament"').click();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('login creates and deletes a tournament', () => {
    cy.createTournament('82');
  });
});
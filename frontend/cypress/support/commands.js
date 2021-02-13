// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add("login", (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add("drag", { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add("dismiss", { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite("visit", (originalFn, url, options) => { ... })
/// <reference types="Cypress" />
Cypress.Commands.add('demoAdminLogin', () => {
    cy.visit('/');
    cy.get('[data-cy="adminButton"]').click();
    cy.contains('Administration').click();
    cy.contains('Manage Courses').click();
});

Cypress.Commands.add('demoStudentLogin', () => {
    cy.visit('/');
    cy.get('[data-cy="studentButton"]').click();
});

Cypress.Commands.add('demoTeacherLogin', () => {
    cy.visit('/')
    cy.get('[data-cy="teacherButton"]').click()
    cy.contains('Management').click()
    cy.contains('Clarifications').click()
})

Cypress.Commands.add('updateClarification', (studentUsername, teacherResponse) => {
    cy.contains(studentUsername)
        .parent()
        .should('have.length', 1)
        .children()
        .should('have.length', 7)
        .find('[data-cy="editClarification"]')
        .click()
    cy.get('[data-cy="Response"]').type(teacherResponse)
    cy.get('[data-cy="saveButton"]').click()
})

Cypress.Commands.add('updatePrivacy', (currentPrivacy) => {
    cy.contains(currentPrivacy)
        .parent()
        .should('have.length', 1)
        .children()
        .should('have.length', 7)
        .find('[data-cy="privacyClarification"]')
        .click()
})

Cypress.Commands.add('addAdditionalClarification', (content, quizTitle) => {
    cy.get('[data-cy="quizzesButton"]').click()

    cy.contains('Clarifications').click()
    cy.contains(quizTitle)
        .parent()
        .should('have.length', 1)
        .children()
        .should('have.length', 5)
        .find('[data-cy="showClarification"]')
        .click()
    cy.get('[data-cy="additionalRequest"]').type(content)
})

Cypress.Commands.add('addAdditionalResponse', (studentUsername, teacherResponse) => {
    cy.contains(studentUsername)
        .parent()
        .should('have.length', 1)
        .children()
        .should('have.length', 7)
        .find('[data-cy="editClarification"]')
        .click()
    cy.get('[data-cy="Response"]').type(teacherResponse)
    cy.get('[data-cy="saveButton"]').click()
})
Cypress.Commands.add('updateClarificationWithoutWrite', (studentUsername, teacherResponse) => {
    cy.contains(studentUsername)
        .parent()
        .should('have.length', 1)
        .children()
        .should('have.length', 7)
        .find('[data-cy="editClarification"]')
        .click()
    cy.get('[data-cy="saveButton"]').click()
})

Cypress.Commands.add('closeErrorMessageTeacher', () => {
    cy.contains('Clarification must have response')
        .parent()
        .find('button')
        .click()
})

Cypress.Commands.add('createCourseExecution', (name, acronym, academicTerm) => {
    cy.get('[data-cy="createButton"]').click();
    cy.get('[data-cy="Name"]').type(name);
    cy.get('[data-cy="Acronym"]').type(acronym);
    cy.get('[data-cy="AcademicTerm"]').type(academicTerm);
    cy.get('[data-cy="saveButton"]').click();
});

Cypress.Commands.add('createTournament', (topicId, startDay, endDay) => {
    cy.get('[data-cy="topicId"').type(topicId); //topic
    cy.get('input[id="input-73"]').click();  //id for start time
    cy.get('[class="v-icon notranslate mdi mdi-chevron-right theme--light"]').click(); //next month
    cy.wait(500);
    cy.get('button[class="v-btn v-btn--text v-btn--rounded theme--light"]').contains(1).click(); //day
    cy
        .get('button[class="v-btn v-btn--flat v-btn--text theme--light v-size--default green--text text--darken-1"]')
        .click(); //ok

    cy.get('input[id="input-78"]').click(); //id for end time
    cy.get('[class="v-icon notranslate mdi mdi-chevron-right theme--light"]:visible').click(); //next month
    cy.wait(500);
    cy.get('button[class="v-btn v-btn--text v-btn--rounded theme--light"]:visible').contains(2).click(); //day
    cy
        .get(
            'button[class="v-btn v-btn--flat v-btn--text theme--light v-size--default green--text text--darken-1"]:visible'
        )
        .click(); //ok

    cy.get('[data-cy="numberQuestions"]').contains(5).click();
    cy.get('[data-cy="createButton"]').click(); //create
});

Cypress.Commands.add('closeErrorMessage', (name, acronym, academicTerm) => {
    cy.contains('Error').parent().find('button').click();
});

Cypress.Commands.add('deleteCourseExecution', (acronym) => {
    cy
        .contains(acronym)
        .parent()
        .should('have.length', 1)
        .children()
        .should('have.length', 7)
        .find('[data-cy="deleteCourse"]')
        .click();
});

Cypress.Commands.add('createFromCourseExecution', (name, acronym, academicTerm) => {
    cy.contains(name)
        .parent()
        .should('have.length', 1)
        .children()
        .should('have.length', 7)
        .find('[data-cy="createFromCourse"]')
        .click()
    cy.get('[data-cy="Acronym"]').type(acronym)
    cy.get('[data-cy="AcademicTerm"]').type(academicTerm)
    cy.get('[data-cy="saveButton"]').click()
})

Cypress.Commands.add('demoStudentLogin', () => {
    cy.visit('/')
    cy.get('[data-cy="studentButton"]').click()
})

Cypress.Commands.add('createClarification', (request, title) => {
    cy.get('[data-cy="quizzesButton"]').click()
    cy.contains('Solved').click()
    cy.contains(title).click()
    cy.get('[data-cy="createButton"]').click()
    cy.get('[data-cy="request"]').type(request)
    cy.get('[data-cy="save button"]').click()
})

Cypress.Commands.add('ClarificationListByQuestion', (title) => {
    cy.get('[data-cy="quizzesButton"]').click()
    cy.contains('Solved').click()
    cy.contains(title).click()
    cy.get('[data-cy="publicButton"]').click()
    cy.wait(4000)
})

Cypress.Commands.add('createClarificationWithoutRequest', () => {
    cy.get('[data-cy="quizzesButton"]').click()
    cy.contains('Solved').click()
    cy.contains('Allocation viewtype').click()
    cy.get('[data-cy="createButton"]').click()
    cy.get('[data-cy="save button"]').click()
})

Cypress.Commands.add('deleteClarification', (quizTitle) => {
    cy.get('[data-cy="quizzesButton"]').click()

    cy.contains('Clarifications').click()
    cy.contains(quizTitle)
        .parent()
        .should('have.length', 1)
        .children()
        .should('have.length', 5)
        .find('[data-cy="deleteClarification"]')
        .click()
})

Cypress.Commands.add('createErrorMessage', (request) => {
    cy.contains('You must write a question')
        .parent()
        .find('button')
        .click()
})

Cypress.Commands.add('createSolvedQuiz', (titleName) => {
    cy.get('[data-cy="quizzesButton"]').click()
    cy.contains('Available').click()
    cy.contains(titleName).click()
    cy.get('[data-cy="endQuiz"]').click()
    cy.get('[data-cy="sure"]').click()
})

Cypress.Commands.add('demoTeacherLoginPpa', () => {
    cy.visit('/');
    cy.get('[data-cy="teacherButton"]').click();
})

Cypress.Commands.add('showUser', (title) => {

    cy.get('[data-cy="showUser"]').click()
})

Cypress.Commands.add('createQuestion', (title, content, options) => {
    cy.get('[data-cy="newQuestion"]').click()
    cy.wait(500)
    cy.get('[data-cy="Title"]').type(title,{force:true})
    cy.get('[data-cy="Question"]').type(content)
    cy.get('[data-cy="options"').eq(0).type(options)
    cy.get('[data-cy="options"').eq(1).type(options)
    cy.get('[data-cy="options"').eq(2).type(options)
    cy.get('[data-cy="options"').eq(3).type(options)
    cy.get('[data-cy="Correct"').eq(3).click({force:true})
    cy.get('[data-cy="saveQuestion"]').click()
})

Cypress.Commands.add('giveJustification',(search,status,justification,title)=>{
    cy.get('[data-cy= "searchBar"]').type(search,{waitForAnimations: false})
    cy.wait(8000)
    cy.get('[data-cy= "changeStatus"]').click({force: true})
    cy.contains(status).click()
    cy.get('[data-cy="justificationInput"]').clear().type(justification,
        {waitForAnimations: false})
    cy.get('[data-cy ="saveJustification"]').click()
    cy.wait(1000)
    cy.get('[data-cy="searchBar"]').clear().type(title,{waitForAnimations: false})
    cy.get('[data-cy="showJustification"]').click({force: true})
    cy.wait(1500)
    cy.get('[data-cy="closeJustification"]').click()
})

Cypress.Commands.add('deleteQuestion',(title)=>{
    cy.get('[data-cy="searchBar"]').clear()
    cy.get('[data-cy="searchBar"]').type(title,{waitForAnimations: false})
    cy.wait(8000)
    cy.get('[data-cya="deleteQuestion"]').click()
})

Cypress.Commands.add('questionForTeacher',(title,question,option)=>{
    cy.demoStudentLogin()
    cy.contains('Questions').click()
    cy.createQuestion(title,question,option)
    cy.wait(500)
    cy.contains('Logout').click()
})

Cypress.Commands.add('deleteForStudent',(title)=>{
    cy.contains('Logout').click()
    cy.demoTeacherLoginPpa()
    cy.contains('Management').click();
    cy.get('[data-cy="teacherQuestions"]').click();
    cy.get('[data-cy="searchBar"]').type(title,{waitForAnimations: false})
    cy.wait(8000)
    cy.get('[data-cya="deleteQuestion"]').click()
})

Cypress.Commands.add('updateQuestion', (content) => {
    cy.get('[data-cy="editQuestion"]').click()
    cy.wait(500)
    cy.get('[data-cy="Question"]').clear(content)
    cy.get('[data-cy="Question"]').type(content)
    cy.get('[data-cy="saveQuestion"]').click()
})

Cypress.Commands.add('showQuestion', (title) => {

    cy.get('[data-cy="showQuestion"]').click()
})

Cypress.Commands.add('giveDisapprovedJustification',(justification)=>{
    cy.contains('Logout').click()
    cy.demoTeacherLoginPpa()
    cy.contains('Management').click();
    cy.get('[data-cy="submittedQuestions"]').click();
    cy.get('[data-cy="Disapproved"]').click({force: true})
    cy.wait(800)
    cy.get('[data-cy="justificationInput"]').clear().type(justification,
        {waitForAnimations: false})
    cy.get('[data-cy="closeJustificationDisapproved"]').click()
    cy.wait(1000)
})

Cypress.Commands.add('giveApprovedJustification',(justification)=>{
    cy.contains('Logout').click()
    cy.demoTeacherLoginPpa()
    cy.contains('Management').click();
    cy.get('[data-cy="submittedQuestions"]').click();
    cy.get('[data-cy="Approved"]').click({force: true})
    cy.wait(800)
    cy.get('[data-cy="justificationInput"]').clear().type(justification,
        {waitForAnimations: false})
    cy.get('[data-cy="closeJustificationApproved"]').click()
    cy.wait(1000)
})

Cypress.Commands.add('changeAvailable',(search,status)=> {
    cy.get('[data-cy= "searchBar"]').type(search, {waitForAnimations: false})
    cy.wait(800)
    cy.get('[data-cy= "changeStatus"]').click({force: true})
    cy.contains(status).click()

})
describe('Student Answers a question and sees results Walk-through', () => {
    function validateQuestion(
      title,
      content,
      answer,
    ) {
      cy.get('[data-cy="showQuestionDialog"]')
        .should('be.visible')
        .within(($ls) => {
          cy.get('.headline').should('contain', title);
          cy.get('span > p').should('contain', content);
          cy.get('span > p').should('contain', answer);
        });
    }
  
    function validateQuestionFull(
      title,
      content,
      answer,
    ) {
      cy.log('Validate question with show dialog. ' + answer);
  
      cy.get('[data-cy="questionTitleGrid"]').first().click();
  
      validateQuestion(title, content, answer);
  
      cy.get('button').contains('close').click();
    }
  
    before(() => {
      cy.cleanMultipleChoiceQuestionsByName('Cypress Question Example');
      cy.cleanCodeFillInQuestionsByName('Cypress Question Example');
      cy.cleanOpenAnswerQuestionsByName('Cypress Question Example');
    });
    after(() => {
      cy.cleanOpenAnswerQuestionsByName('Cypress Question Example');
    });
  
    beforeEach(() => {
      cy.demoTeacherLogin();
      cy.server();
      cy.route('GET', '/courses/*/questions').as('getQuestions');
      cy.route('GET', '/courses/*/topics').as('getTopics');
      cy.get('[data-cy="managementMenuButton"]').click();
      cy.get('[data-cy="questionsTeacherMenuButton"]').click();
  
      cy.wait('@getQuestions').its('status').should('eq', 200);
  
      cy.wait('@getTopics').its('status').should('eq', 200);
    });
  
    afterEach(() => {
      cy.logout();
    });
  
    it('Creates a new open answer in question', function () {
      cy.get('button').contains('New Question').click();
  
      cy.get('[data-cy="createOrEditQuestionDialog"]')
        .parent()
        .should('be.visible');
  
      cy.get('span.headline').should('contain', 'New Question');
  
  
      cy.createOpenAnswerQuestion('Cypress Question Example - 01',
      'Cypress Question Example - Content - 01',
      'Cypress Answer Example - Answer - 01');
  
      cy.route('POST', '/courses/*/questions/').as('postQuestion');
  
      cy.get('button').contains('Save').click();
  
      cy.wait('@postQuestion').its('status').should('eq', 200);
  
      cy.get('[data-cy="questionTitleGrid"]')
        .first()
        .should('contain', 'Cypress Question Example - 01');
  
      validateQuestionFull(
        'Cypress Question Example - 01',
        'Cypress Question Example - Content - 01',
        'Cypress Answer Example - Answer - 01'
      );

      cy.createOpenAnswerQuestion('Cypress Question Example - 02',
      'Cypress Question Example - Content - 02',
      'Cypress Answer Example - Answer - 02');
  
      cy.route('POST', '/courses/*/questions/').as('postQuestion');
  
      cy.get('button').contains('Save').click();
  
      cy.wait('@postQuestion').its('status').should('eq', 200);
  
      cy.get('[data-cy="questionTitleGrid"]')
        .first()
        .should('contain', 'Cypress Question Example - 02');
  
      validateQuestionFull(
        'Cypress Question Example - 02',
        'Cypress Question Example - Content - 02',
        'Cypress Answer Example - Answer - 02'
      );

      cy.createQuizzWith2Questions(
          'Quiz Title Example',
          'Cypress Question Example - 02',
          'Cypress Question Example - 01',
      );

      cy.get('[data-cy="logoutButton"]').click({ force: true });

      cy.visit('/');
      cy.get('[data-cy="demoStudentLoginButton"]').click();

      cy.get('[data-cy="quizzesStudentMenuButton"]').click();
      cy.contains('Available').click();
    
      cy.contains('Quiz Title Example').click();
        
      cy.get('[data-cy="questionAnswerInput"]')
          .type('Cypress Answer Example - Answer - 02', {force: true})
          .click({force: true});
    
      cy.get('[data-cy="nextQuestionButton"]').click();

      cy.get('[data-cy="questionAnswerInput"]')
      .type('Cypress Answer Example - Answer - 02', {force: true})
      .click({force: true});

      cy.get('[data-cy="endQuizButton"]').click();
      cy.get('[data-cy="confirmationButton"]').click();

      cy.get('[data-cy="nextQuestionButton"]').click();
    });
  });
  
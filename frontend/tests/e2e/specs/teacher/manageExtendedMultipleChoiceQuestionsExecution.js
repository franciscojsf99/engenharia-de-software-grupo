describe('Manage Multiple Choice Questions Walk-through', () => {
  function validateQuestion(
    title,
    content,
    optionPrefix = 'Option ',
    correctIndexes = [2,3],
    importance = 0,
    importanceUpdated = false
  ) {
    cy.get('[data-cy="showQuestionDialog"]')
      .should('be.visible')
      .within(($ls) => {
        cy.get('.headline').should('contain', title);
        cy.get('span > p').should('contain', content);
        cy.get('li').each(($el, index, $list) => {
          cy.get($el).should('contain', optionPrefix + index);
          if (correctIndexes.includes(index)) {
            if (importance == 0) {
              cy.get($el).should('contain', '[★]');
            }
            else {
              if (index == correctIndexes[0] && importanceUpdated) {
                cy.get($el).should('contain', '[★] Option ' + index + ' (Importance: 2)');
              }
              else if (index == correctIndexes[0]) {
                cy.get($el).should('contain', '[★] Option ' + index + ' (Importance: 1)');
              }
              else if (index == correctIndexes[1] && importanceUpdated) {
                cy.get($el).should('contain', '[★] Option ' + index + ' (Importance: 1)');
              }
              else /*(index == correctIndexes[1])*/{
                cy.get($el).should('contain', '[★] Option ' + index + ' (Importance: 2)');
              }
            }
          } else {
            cy.get($el).should('not.contain', '[★]');
          }
        });
      });
  }

  function validateQuestionFull(
    title,
    content,
    optionPrefix = 'Option ',
    correctIndexes = [2,3],
    importance = 0,
    importanceUpdated = false
  ) {
    cy.log('Validate question with show dialog. ' + correctIndexes);

    cy.get('[data-cy="questionTitleGrid"]').first().click();
    validateQuestion(title, content, optionPrefix, correctIndexes, importance, importanceUpdated);

    cy.get('button').contains('close').click();
  }

  before(() => {
    cy.cleanMultipleChoiceQuestionsByName('Cypress Question Example');
    cy.cleanCodeFillInQuestionsByName('Cypress Question Example');
  });
  after(() => {
    cy.cleanMultipleChoiceQuestionsByName('Cypress Question Example');
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

  it('Creates a new multiple choice question', function () {
    cy.get('button').contains('New Question').click();

    cy.get('[data-cy="createOrEditQuestionDialog"]')
      .parent()
      .should('be.visible');

    cy.get('span.headline').should('contain', 'New Question');

    cy.get(
      '[data-cy="questionTitleTextArea"]'
    ).type('Cypress Question Example - 01', { force: true });
    cy.get(
      '[data-cy="questionQuestionTextArea"]'
    ).type('Cypress Question Example - Content - 01', { force: true });

    cy.get('[data-cy="questionOptionsInput"')
      .should('have.length', 4)
      .each(($el, index, $list) => {
        cy.get($el).within(($ls) => {
          if (index === 2 || index === 3) {
            cy.get(`[data-cy="Switch${index + 1}"]`).check({ force: true });
          }
          cy.get(`[data-cy="Option${index + 1}"]`).type('Option ' + index);
        });
      })
    cy.get(`[data-cy="Option${3} importance"]`).type('1');
    cy.get(`[data-cy="Option${4} importance"]`).type('2');


    cy.route('POST', '/courses/*/questions/').as('postQuestion');

    cy.get('button').contains('Save').click();

    cy.wait('@postQuestion').its('status').should('eq', 200);

    cy.get('[data-cy="questionTitleGrid"]')
      .first()
      .should('contain', 'Cypress Question Example - 01');

    validateQuestionFull(
      'Cypress Question Example - 01',
      'Cypress Question Example - Content - 01'
    );
  });
  it('Can view question with importance (with click)', function () {

      validateQuestionFull(
          'Cypress Question Example - 01',
          'Cypress Question Example - Content - 01',
          'Option ',
          [2,3],
          1,
          false
      );
  });

  it('Can update importance (with right-click)', function () {
      cy.route('PUT', '/questions/*').as('updateQuestion');

      cy.get('[data-cy="questionTitleGrid"]').first().rightclick();

      cy.get('[data-cy="createOrEditQuestionDialog"]')
        .parent()
        .should('be.visible')
        .within(($list) => {
          cy.get('span.headline').should('contain', 'Edit Question');

          cy.get('[data-cy="questionTitleTextArea"]')
            .clear({ force: true })
            .type('Cypress Question Example - 01 - Edited', { force: true });

          cy.get(`[data-cy="Option${3} importance"]`).clear({ force: true }).type('2');
          cy.get(`[data-cy="Option${4} importance"]`).clear({ force: true }).type('1');

          cy.get('button').contains('Save').click();
        });

      cy.wait('@updateQuestion').its('status').should('eq', 200);

      cy.get('[data-cy="questionTitleGrid"]')
        .first()
        .should('contain', 'Cypress Question Example - 01 - Edited');

      validateQuestionFull(
          'Cypress Question Example - 01 - Edited',
          'Cypress Question Example - Content - 01',
          'Option ',
          [2,3],
          1,
          true
      );
    });

  it('Can update importance (with button)', function () {
    cy.route('PUT', '/questions/*').as('updateQuestion');

    cy.get('tbody tr')
        .first()
        .within(($list) => {
          cy.get('button').contains('edit').click();
        });

    cy.get('[data-cy="createOrEditQuestionDialog"]')
        .parent()
        .should('be.visible')
        .within(($list) => {
          cy.get('span.headline').should('contain', 'Edit Question');

          cy.get('[data-cy="questionTitleTextArea"]')
              .clear({ force: true })
              .type('Cypress Question Example - 01 - Edited02', { force: true });

          cy.get(`[data-cy="Option${3} importance"]`).clear({ force: true }).type('1');
          cy.get(`[data-cy="Option${4} importance"]`).clear({ force: true }).type('2');

          cy.get('button').contains('Save').click();
        });

    cy.wait('@updateQuestion').its('status').should('eq', 200);

    cy.get('[data-cy="questionTitleGrid"]')
        .first()
        .should('contain', 'Cypress Question Example - 01 - Edited02');

    validateQuestionFull(
        'Cypress Question Example - 01 - Edited02',
        'Cypress Question Example - Content - 01',
        'Option ',
        [2,3],
        1,
        false
    );
  });

    it('Can update correct options (with right-click)', function () {
        cy.route('PUT', '/questions/*').as('updateQuestion');

        cy.get('[data-cy="questionTitleGrid"]').first().rightclick();

        cy.get('[data-cy="createOrEditQuestionDialog"]')
            .parent()
            .should('be.visible')
            .within(($list) => {
                cy.get('span.headline').should('contain', 'Edit Question');

                cy.get('[data-cy="questionTitleTextArea"]')
                    .clear({ force: true })
                    .type('Cypress Question Example - 01 - EditedCorrect', { force: true });
                

                cy.get(`[data-cy="Switch${1}"]`).check({force: true});
                cy.get(`[data-cy="Switch${2}"]`).check({force: true});
                cy.get(`[data-cy="Switch${3}"]`).uncheck({force: true});
                cy.get(`[data-cy="Switch${4}"]`).uncheck({force: true});

                cy.get(`[data-cy="Option${1} importance"]`).clear({ force: true }).type('1');
                cy.get(`[data-cy="Option${2} importance"]`).clear({ force: true }).type('2');

                cy.get('button').contains('Save').click();
            });

        cy.wait('@updateQuestion').its('status').should('eq', 200);

        cy.get('[data-cy="questionTitleGrid"]')
            .first()
            .should('contain', 'Cypress Question Example - 01 - EditedCorrect');

        validateQuestionFull(
            'Cypress Question Example - 01 - EditedCorrect',
            'Cypress Question Example - Content - 01',
            'Option ',
            [0,1],
            1,
            false
        );
    });

    it('Can update correct options (with button)', function () {
        cy.route('PUT', '/questions/*').as('updateQuestion');

        cy.get('tbody tr')
            .first()
            .within(($list) => {
                cy.get('button').contains('edit').click();
            });

        cy.get('[data-cy="createOrEditQuestionDialog"]')
            .parent()
            .should('be.visible')
            .within(($list) => {
                cy.get('span.headline').should('contain', 'Edit Question');

                cy.get('[data-cy="questionTitleTextArea"]')
                    .clear({ force: true })
                    .type('Cypress Question Example - 01 - EditedCorrect02', { force: true });

                cy.get(`[data-cy="Switch${1}"]`).uncheck({force: true});
                cy.get(`[data-cy="Switch${2}"]`).uncheck({force: true});
                cy.get(`[data-cy="Switch${3}"]`).check({force: true});
                cy.get(`[data-cy="Switch${4}"]`).check({force: true});

                cy.get(`[data-cy="Option${3} importance"]`).clear({ force: true }).type('1');
                cy.get(`[data-cy="Option${4} importance"]`).clear({ force: true }).type('2');

                cy.get('button').contains('Save').click();
            });

        cy.wait('@updateQuestion').its('status').should('eq', 200);

        cy.get('[data-cy="questionTitleGrid"]')
            .first()
            .should('contain', 'Cypress Question Example - 01 - EditedCorrect02');

        validateQuestionFull(
            'Cypress Question Example - 01 - EditedCorrect02',
            'Cypress Question Example - Content - 01',
            'Option ',
            [2,3],
            1,
            false
        );
    });

  it('Creates a new multiple choice question with 10 options', function () {
    cy.get('button').contains('New Question').click();

    cy.get('[data-cy="createOrEditQuestionDialog"]')
        .parent()
        .should('be.visible');

    cy.get('span.headline').should('contain', 'New Question');

    cy.get(
        '[data-cy="questionTitleTextArea"]'
    ).type('Cypress Question Example - 01 (10 Options)', { force: true });
    cy.get('[data-cy="questionQuestionTextArea"]').type(
        'Cypress Question Example - Content - 01 (10 Options)',
        {
          force: true,
        }
    );

    cy.get('[data-cy="addOptionMultipleChoice"]').click({ force: true }); // 5
    cy.get('[data-cy="addOptionMultipleChoice"]').click({ force: true }); // 6
    cy.get('[data-cy="addOptionMultipleChoice"]').click({ force: true }); // 7
    cy.get('[data-cy="addOptionMultipleChoice"]').click({ force: true }); // 8
    cy.get('[data-cy="addOptionMultipleChoice"]').click({ force: true }); // 9
    cy.get('[data-cy="addOptionMultipleChoice"]').click({ force: true }); // 10

    cy.get('[data-cy="questionOptionsInput"')
        .should('have.length', 10)
        .each(($el, index, $list) => {
          cy.get($el).within(($ls) => {
            if (index == 4 || index === 6) {
              cy.get(`[data-cy="Switch${index + 1}"]`).check({ force: true });
            }
            cy.get(`[data-cy="Option${index + 1}"]`).type('Option10 ' + index);
          });
        });
    cy.get(`[data-cy="Option${5} importance"]`).type('1');
    cy.get(`[data-cy="Option${7} importance"]`).type('2');

    cy.route('POST', '/courses/*/questions/').as('postQuestion');

    cy.get('button').contains('Save').click();

    cy.wait('@postQuestion').its('status').should('eq', 200);

    cy.get('[data-cy="questionTitleGrid"]')
        .first()
        .should('contain', 'Cypress Question Example - 01');

    validateQuestionFull(
        'Cypress Question Example - 01 (10 Options)',
        'Cypress Question Example - Content - 01 (10 Options)',
        'Option10 ',
        [4,6]
    );
  });
});

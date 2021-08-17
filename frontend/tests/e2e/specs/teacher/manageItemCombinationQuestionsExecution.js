describe('Manage Item Combination Questions Walk-through', () => {
  function validateQuestion(
    title,
    content,
    leftOptions,
    rightOptions,
    combinations
  ) {
    cy.get('[data-cy="showQuestionDialog"]')
      .should('be.visible')
      .within(($ls) => {
        cy.get('.headline').should('contain', title);
        cy.get('span > p').should('contain', content);
        cy.get('span > p').should('contain', 'Left Options:');
        cy.get('#leftOptions').children().should('have.length', 2).each(($el, index, $list) => {
          cy.get($el).should('contain', leftOptions[index]);
        });
        cy.get('span > p').should('contain', 'Right Options:');
        cy.get('#rightOptions').children().each(($el, index, $list) => {
          cy.get($el).should('contain', rightOptions[index]);
        });
        cy.get('span > p').should('contain', 'Combinations:');
        cy.get('#combinations').children().each(($el, index, $list) => {
          cy.get($el).should('contain', combinations[index]);
        });
      });
  }

  function validateQuestionFull(
    title,
    content,
    leftOptions,
    rightOptions,
    combinations
  ) {
    cy.log('Validate question with show dialog.');

    cy.get('[data-cy="questionTitleGrid"]').first().click();

    validateQuestion(title, content, leftOptions, rightOptions, combinations);

    cy.get('button').contains('close').click();
  }

  before(() => {
    cy.cleanMultipleChoiceQuestionsByName('Cypress Question Example');
    cy.cleanCodeFillInQuestionsByName('Cypress Question Example');
    cy.cleanOpenAnswerQuestionsByName('Cypress Question Example');
    cy.cleanItemCombinationQuestionsByName('Cypress Question Example');
  });
  after(() => {
    cy.cleanItemCombinationQuestionsByName('Cypress Question Example');
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

  it('Creates a new item combination question', function () {
    cy.get('button').contains('New Question').click();

    cy.get('[data-cy="createOrEditQuestionDialog"]')
      .parent()
      .should('be.visible');

    cy.get('span.headline').should('contain', 'New Question');

    cy.get('[data-cy="questionTypeInput"]')
      .type('item_combination', {force: true})
      .click({force: true});

    cy.get(
      '[data-cy="questionTitleTextArea"]'
    ).type('Cypress Question Example - 01', { force: true });
    cy.get(
      '[data-cy="questionQuestionTextArea"]'
    ).type('Cypress Question Example - Content - 01', { force: true });

    cy.get('[data-cy=AddLeftButton]').click({ force: true });

    cy.setLeftOptions(2);

    cy.get('[data-cy=ManageRight]').click({ force: true });

    cy.get('[data-cy=AddRightButton]').click({ force: true });
    cy.get('[data-cy=AddRightButton]').click({ force: true });

    cy.setRightOptions(2);

    cy.get('[data-cy=ManageOK]').click({ force: true });

    cy.get('[data-cy=RightOptionsDropdown1]').type('{downarrow}{enter}{esc}', { force: true });
    cy.get('[data-cy=RightOptionsDropdown2]').type('{downarrow}{downarrow}{enter}{esc}', { force: true });

    cy.route('POST', '/courses/*/questions/').as('postQuestion');

    cy.get('button').contains('Save').click();

    cy.wait('@postQuestion').its('status').should('eq', 200);

    cy.get('[data-cy="questionTitleGrid"]')
      .first()
      .should('contain', 'Cypress Question Example - 01');

    validateQuestionFull(
      'Cypress Question Example - 01',
      'Cypress Question Example - Content - 01',
      ['Left Option 1', 'Left Option 2'],
      ['Right Option 1', 'Right Option 2'],
      ['Left Option 1 -> Right Option 1', 'Left Option 2 -> Right Option 2']
    );
  });

  it('Can view item combination question (with button)', function () {
    cy.get('tbody tr')
      .first()
      .within(($list) => {
        cy.get('button').contains('visibility').click();
      });

    validateQuestion(
      'Cypress Question Example - 01',
      'Cypress Question Example - Content - 01',
      ['Left Option 1', 'Left Option 2'],
      ['Right Option 1', 'Right Option 2'],
      ['Left Option 1 -> Right Option 1', 'Left Option 2 -> Right Option 2']
    );

    cy.get('button').contains('close').click();
  });

  it('Can view item combination question (with click)', function () {
    cy.get('[data-cy="questionTitleGrid"]').first().click();

    validateQuestion(
      'Cypress Question Example - 01',
      'Cypress Question Example - Content - 01',
      ['Left Option 1', 'Left Option 2'],
      ['Right Option 1', 'Right Option 2'],
      ['Left Option 1 -> Right Option 1', 'Left Option 2 -> Right Option 2']
    );

    cy.get('button').contains('close').click();
  });

  it('Can update item combination question title (with right-click)', function () {
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

        cy.get('button').contains('Save').click();
      });

    cy.wait('@updateQuestion').its('status').should('eq', 200);

    cy.get('[data-cy="questionTitleGrid"]')
      .first()
      .should('contain', 'Cypress Question Example - 01 - Edited');

    validateQuestionFull(
      (title = 'Cypress Question Example - 01 - Edited'),
      (content = 'Cypress Question Example - Content - 01'),
      ['Left Option 1', 'Left Option 2'],
      ['Right Option 1', 'Right Option 2'],
      ['Left Option 1 -> Right Option 1', 'Left Option 2 -> Right Option 2']
    );
  });

  it('Can update item combination question content (with button)', function () {
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

        cy.get('[data-cy="questionQuestionTextArea"]')
          .clear({ force: true })
          .type('Cypress New Content For Question!', { force: true });

        cy.get('button').contains('Save').click();
      });

    cy.wait('@updateQuestion').its('status').should('eq', 200);

    validateQuestionFull(
      (title = 'Cypress Question Example - 01 - Edited'),
      (content = 'Cypress New Content For Question!'),
      ['Left Option 1', 'Left Option 2'],
      ['Right Option 1', 'Right Option 2'],
      ['Left Option 1 -> Right Option 1', 'Left Option 2 -> Right Option 2']
    );
  });

  it('Can update item combination question combinations (with right-click)', function () {
    cy.route('PUT', '/questions/*').as('updateQuestion');

    cy.get('[data-cy="questionTitleGrid"]').first().rightclick();

    cy.get('[data-cy="createOrEditQuestionDialog"]')
      .parent()
      .should('be.visible')
      .within(($list) => {
        cy.get('span.headline').should('contain', 'Edit Question');

        cy.get('[data-cy=RightOptionsDropdown1]').type('{downarrow}{downarrow}{enter}{esc}', { force: true });

        cy.get('button').contains('Save').click();
      });

    cy.wait('@updateQuestion').its('status').should('eq', 200);

    validateQuestionFull(
      (title = 'Cypress Question Example - 01 - Edited'),
      (content = 'Cypress New Content For Question!'),
      ['Left Option 1', 'Left Option 2'],
      ['Right Option 1', 'Right Option 2'],
      ['Left Option 1 -> Right Option 1'
        , 'Left Option 1 -> Right Option 2'
        , 'Left Option 2 -> Right Option 2']
    );
  });

  it('Can update item combination question combinations (with button)', function () {
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

        cy.get('[data-cy=RightOptionsDropdown1]').type('{downarrow}{downarrow}{enter}{esc}', { force: true });

        cy.get('button').contains('Save').click();
      });

    cy.wait('@updateQuestion').its('status').should('eq', 200);

    validateQuestionFull(
      (title = 'Cypress Question Example - 01 - Edited'),
      (content = 'Cypress New Content For Question!'),
      ['Left Option 1', 'Left Option 2'],
      ['Right Option 1', 'Right Option 2'],
      ['Left Option 1 -> Right Option 1'
        , 'Left Option 2 -> Right Option 2']
    );
  });

  it('Can duplicate item combination question', function () {
    cy.get('tbody tr')
      .first()
      .within(($list) => {
        cy.get('button').contains('cached').click();
      });

    cy.get('[data-cy="createOrEditQuestionDialog"]')
      .parent()
      .should('be.visible');

    cy.get('span.headline').should('contain', 'New Question');

    cy.get('[data-cy="questionTitleTextArea"]')
      .should('have.value', 'Cypress Question Example - 01 - Edited')
      .type('{end} - DUP', { force: true });
    cy.get('[data-cy="questionQuestionTextArea"]').should(
      'have.value',
      'Cypress New Content For Question!'
    );

    cy.get('[data-cy="questionOptionsInput"]')
      .should('have.length', 2)
      .each(($el, index, $list) => {
        cy.get($el).within(($ls) => {
          cy.get(`[data-cy="LeftOption${index + 1}"]`).should('have.value', `Left Option ${index + 1}`);
        });
      });

    cy.get('[data-cy=ManageRight]').click({ force: true });

    cy.get('[data-cy="ManageRightOptionsInput"]')
      .should('have.length', 2)
      .each(($el, index, $list) => {
        cy.get($el).within(($ls) => {
          cy.get(`[data-cy="RightOption${index + 1}"]`).should('have.value', `Right Option ${index + 1}`);
        });
      });

    cy.route('POST', '/courses/*/questions/').as('postQuestion');

    cy.get('[data-cy=ManageOK]').click({ force: true });

    cy.get('button').contains('Save').click();

    cy.wait('@postQuestion').its('status').should('eq', 200);

    cy.get('[data-cy="questionTitleGrid"]')
      .first()
      .should('contain', 'Cypress Question Example - 01 - Edited - DUP');

    validateQuestionFull(
      'Cypress Question Example - 01 - Edited - DUP',
      'Cypress New Content For Question!',
      ['Left Option 1', 'Left Option 2'],
      ['Right Option 1', 'Right Option 2'],
      ['Left Option 1 -> Right Option 1'
        , 'Left Option 2 -> Right Option 2']
    );
  });

  it('Can delete created item combination question', function () {
    cy.route('DELETE', '/questions/*').as('deleteQuestion');
    cy.get('tbody tr')
      .first()
      .within(($list) => {
        cy.get('button').contains('delete').click();
      });

    cy.wait('@deleteQuestion').its('status').should('eq', 200);
  });
});

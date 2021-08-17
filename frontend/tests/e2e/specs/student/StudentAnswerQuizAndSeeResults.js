describe('Student Answer Quiz and See Results', () => {
    function validateQuestion(
        title,
        content,
        leftOptions,
        rightOptions,
        combinations
    ) {
        cy.get('[data-cy="resultComponent"]')
            .should('be.visible')
            .within(($ls) => {
                cy.get('[data-cy="StudentAnswers"]').should('contain', "Student Answers");
                console.log(cy.get('[data-cy="AnswerCombination1"]'));
                console.log(cy.get('[data-cy="AnswerCombination1"]'));
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
        cy.teacherCreateItemCombinationQuestionAndQuiz();
        cy.logout();
        cy.demoStudentLogin();
    });

    afterEach(() => {
        cy.logout();
    });

    it('Answer quiz with item combination question and see results', function () {
        cy.get('[data-cy="quizzesStudentMenuButton"]').click();
        cy.contains('Available').click();
        cy.get('[data-cy="availableQuizzesList"]').children().should('have.length', 2).each(($el, index, $list) => {
            if(index == 1) {
                cy.get($el).click();
            }
        });
        cy.get('[data-cy=RightOptionsDropdown1]').type('{downarrow}{enter}{esc}', { force: true });
        cy.get('[data-cy=RightOptionsDropdown2]').type('{downarrow}{downarrow}{enter}{esc}', { force: true });
        cy.get('[data-cy=endQuizButton]').click();
        cy.get('[data-cy=confirmationButton]').click();

    });
});

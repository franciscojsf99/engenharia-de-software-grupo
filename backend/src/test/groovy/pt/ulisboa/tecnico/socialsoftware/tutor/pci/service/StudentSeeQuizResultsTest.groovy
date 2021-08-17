package pt.ulisboa.tecnico.socialsoftware.tutor.pci.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.ItemCombinationAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.ItemCombinationStatementAnswerDetailsCombinationDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.ItemCombinationStatementAnswerDetailsDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementQuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Combination
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CombinationOption
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler

@DataJpaTest
class StudentSeeQuizResultsTest extends SpockTest {

    def user
    def quizQuestion
    def question
    def leftCombinationOption
    def rightCombinationOption
    def combination
    def quizAnswer
    def date
    def quiz

    def setup() {
        createExternalCourseAndExecution()

        user = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        user.addCourse(externalCourseExecution)
        userRepository.save(user)

        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle("Quiz Title")
        quiz.setType(Quiz.QuizType.PROPOSED.toString())
        quiz.setCourseExecution(externalCourseExecution)
        quiz.setAvailableDate(DateHandler.now())
        quizRepository.save(quiz)

        def question = new Question()
        question.setKey(1)
        question.setTitle("Question Title")
        question.setCourse(externalCourse)
        def questionDetails = new ItemCombinationQuestion()
        question.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        questionRepository.save(question)

        quizQuestion = new QuizQuestion(quiz, question, 0)
        quizQuestionRepository.save(quizQuestion)

        leftCombinationOption = new CombinationOption()
        leftCombinationOption.setContent(LEFT_COMBINATION_OPTION_1_CONTENT)
        leftCombinationOption.setType(CombinationOption.Type.LEFT)
        leftCombinationOption.setQuestionDetails(questionDetails)
        combinationOptionRepository.save(leftCombinationOption)

        rightCombinationOption = new CombinationOption()
        rightCombinationOption.setContent(RIGHT_COMBINATION_OPTION_1_CONTENT)
        rightCombinationOption.setType(CombinationOption.Type.RIGHT)
        rightCombinationOption.setQuestionDetails(questionDetails)
        combinationOptionRepository.save(rightCombinationOption)

        combination = new Combination(leftCombinationOption, rightCombinationOption)
        combination.setQuestionDetails(questionDetails)
        combinationRepository.save(combination)

        date = DateHandler.now()

        quizAnswer = new QuizAnswer(user, quiz)
        quizAnswerRepository.save(quizAnswer)
    }

    def 'conclude quiz with answer, before conclusionDate and see result'() {
        given: 'a quiz with future conclusionDate'
        quiz.setConclusionDate(DateHandler.now().plusDays(2))
        and: 'an answer'
        def statementQuizDto = new StatementQuizDto()
        statementQuizDto.id = quiz.getId()
        statementQuizDto.quizAnswerId = quizAnswer.getId()
        def statementAnswerDto = new StatementAnswerDto()
        def combinationAnswerDto = new ItemCombinationStatementAnswerDetailsCombinationDto(leftCombinationOption.getId(),
                rightCombinationOption.getId())
        def combinationsAnswerDto = new ArrayList<ItemCombinationStatementAnswerDetailsCombinationDto>()
        combinationsAnswerDto.add(combinationAnswerDto)
        def itemCombinationAnswerDto = new ItemCombinationStatementAnswerDetailsDto()
        itemCombinationAnswerDto.setCombinations(combinationsAnswerDto)
        statementAnswerDto.setAnswerDetails(itemCombinationAnswerDto)
        statementAnswerDto.setSequence(0)
        statementAnswerDto.setTimeTaken(100)
        statementAnswerDto.setQuestionAnswerId(quizAnswer.getQuestionAnswers().get(0).getId())
        statementQuizDto.getAnswers().add(statementAnswerDto)

        when:
        def correctAnswers = answerService.concludeQuiz(statementQuizDto)

        then: 'the value is createQuestion and persistent'
        quizAnswer.isCompleted()
        questionAnswerRepository.findAll().size() == 1
        def questionAnswer = questionAnswerRepository.findAll().get(0)
        questionAnswer.getQuizAnswer() == quizAnswer
        quizAnswer.getQuestionAnswers().contains(questionAnswer)
        questionAnswer.getQuizQuestion() == quizQuestion
        quizQuestion.getQuestionAnswers().contains(questionAnswer)
        ((ItemCombinationAnswer) questionAnswer.getAnswerDetails()).getCombinations().get(0).getLeftOption() == leftCombinationOption
        ((ItemCombinationAnswer) questionAnswer.getAnswerDetails()).getCombinations().get(0).getRightOption() == rightCombinationOption
        ((ItemCombinationAnswer) questionAnswer.getAnswerDetails()).isCorrect()
        and: 'the return value is OK'
        correctAnswers.size() == 1
        def correctAnswerDto = correctAnswers.get(0)
        correctAnswerDto.getSequence() == 0
        leftCombinationOption.getId() == correctAnswerDto.getCorrectAnswerDetails()
                .getCorrectCombinations().get(0).getLeftCombinationId()
        rightCombinationOption.getId() == correctAnswerDto.getCorrectAnswerDetails()
                .getCorrectCombinations().get(0).getRightCombinationId()
    }

    def 'conclude quiz with empty answer, before conclusionDate and see result'() {
        given: 'a quiz with future conclusionDate'
        quiz.setConclusionDate(DateHandler.now().plusDays(2))
        and: 'an answer'
        def statementQuizDto = new StatementQuizDto()
        statementQuizDto.id = quiz.getId()
        statementQuizDto.quizAnswerId = quizAnswer.getId()
        def statementAnswerDto = new StatementAnswerDto()
        def combinationsAnswerDto = new ArrayList<ItemCombinationStatementAnswerDetailsCombinationDto>()
        def itemCombinationAnswerDto = new ItemCombinationStatementAnswerDetailsDto()
        itemCombinationAnswerDto.setCombinations(combinationsAnswerDto)
        statementAnswerDto.setAnswerDetails(itemCombinationAnswerDto)
        statementAnswerDto.setSequence(0)
        statementAnswerDto.setTimeTaken(100)
        statementAnswerDto.setQuestionAnswerId(quizAnswer.getQuestionAnswers().get(0).getId())
        statementQuizDto.getAnswers().add(statementAnswerDto)

        when:
        def correctAnswers = answerService.concludeQuiz(statementQuizDto)

        then: 'the value is createQuestion and persistent'
        quizAnswer.isCompleted()
        questionAnswerRepository.findAll().size() == 1
        def questionAnswer = questionAnswerRepository.findAll().get(0)
        questionAnswer.getQuizAnswer() == quizAnswer
        quizAnswer.getQuestionAnswers().contains(questionAnswer)
        questionAnswer.getQuizQuestion() == quizQuestion
        quizQuestion.getQuestionAnswers().contains(questionAnswer)
        ((ItemCombinationAnswer) questionAnswer.getAnswerDetails()).isCorrect() == false
        correctAnswers.size() == 1
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
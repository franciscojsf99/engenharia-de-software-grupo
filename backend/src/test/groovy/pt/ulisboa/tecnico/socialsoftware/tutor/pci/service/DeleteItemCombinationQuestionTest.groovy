package pt.ulisboa.tecnico.socialsoftware.tutor.pci.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Combination
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CombinationOption
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion

@DataJpaTest
class DeleteItemCombinationQuestionTest extends SpockTest {

    def question
    def leftCombinationOption
    def rightCombinationOption
    def combination

    def setup() {
        createExternalCourseAndExecution()

        question = new Question()
        question.setKey(1)
        question.setTitle(QUESTION_1_TITLE)
        question.setContent(QUESTION_1_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        question.setCourse(externalCourse)
        def questionDetails = new ItemCombinationQuestion()
        question.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        questionRepository.save(question)

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
    }

    def "remove an item combination question"() {
        when:
        questionService.removeQuestion(question.getId())

        then: "the question is removeQuestion"
        questionRepository.count() == 0L
        combinationOptionRepository.count() == 0L
        combinationRepository.count() == 0L
    }

    def "remove an item combination question which is in a quiz"() {
        given: "a question with answers"
        Quiz quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle(QUIZ_TITLE)
        quiz.setType(Quiz.QuizType.PROPOSED.toString())
        quiz.setAvailableDate(LOCAL_DATE_BEFORE)
        quiz.setCourseExecution(externalCourseExecution)
        quiz.setOneWay(true)
        quizRepository.save(quiz)

        QuizQuestion quizQuestion= new QuizQuestion()
        quizQuestion.setQuiz(quiz)
        quizQuestion.setQuestion(question)
        quizQuestionRepository.save(quizQuestion)

        when:
        questionService.removeQuestion(question.getId())

        then: "the question an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.QUESTION_IS_USED_IN_QUIZ
    }

    def "remove an item combination question which has topics"() {
        given: 'a question with topics'
        def topicDto = new TopicDto()
        topicDto.setName("name1")
        def topicOne = new Topic(externalCourse, topicDto)
        topicDto.setName("name2")
        def topicTwo = new Topic(externalCourse, topicDto)
        question.getTopics().add(topicOne)
        topicOne.getQuestions().add(question)
        question.getTopics().add(topicTwo)
        topicTwo.getQuestions().add(question)
        topicRepository.save(topicOne)
        topicRepository.save(topicTwo)

        when:
        questionService.removeQuestion(question.getId())

        then:
        questionRepository.count() == 0L
        combinationOptionRepository.count() == 0L
        combinationRepository.count() == 0L
        topicRepository.count() == 2L
        topicOne.getQuestions().size() == 0
        topicTwo.getQuestions().size() == 0
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}

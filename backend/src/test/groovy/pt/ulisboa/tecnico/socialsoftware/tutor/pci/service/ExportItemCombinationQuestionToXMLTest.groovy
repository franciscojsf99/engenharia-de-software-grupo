package pt.ulisboa.tecnico.socialsoftware.tutor.pci.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Combination
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CombinationOption
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question

@DataJpaTest
class ExportItemCombinationQuestionToXMLTest extends SpockTest {

    def question
    def leftCombinationOption1
    def leftCombinationOption2
    def rightCombinationOption1
    def rightCombinationOption2
    def combination

    def setup() {
        createExternalCourseAndExecution()

        given: "create a question"
        question = new Question()
        question.setCourse(externalCourse)
        question.setKey(1)
        question.setTitle(QUESTION_1_TITLE)
        question.setContent(QUESTION_1_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        def questionDetails = new ItemCombinationQuestion()
        question.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        questionRepository.save(question)

        and: '4 item combination options (left and right)'
        leftCombinationOption1 = new CombinationOption()
        leftCombinationOption1.setContent(LEFT_COMBINATION_OPTION_1_CONTENT)
        leftCombinationOption1.setType(CombinationOption.Type.LEFT)
        leftCombinationOption1.setQuestionDetails(questionDetails)
        combinationOptionRepository.save(leftCombinationOption1)

        leftCombinationOption2 = new CombinationOption()
        leftCombinationOption2.setContent(LEFT_COMBINATION_OPTION_2_CONTENT)
        leftCombinationOption2.setType(CombinationOption.Type.LEFT)
        leftCombinationOption2.setQuestionDetails(questionDetails)
        combinationOptionRepository.save(leftCombinationOption2)


        rightCombinationOption1 = new CombinationOption()
        rightCombinationOption1.setContent(RIGHT_COMBINATION_OPTION_1_CONTENT)
        rightCombinationOption1.setType(CombinationOption.Type.RIGHT)
        rightCombinationOption1.setQuestionDetails(questionDetails)
        combinationOptionRepository.save(rightCombinationOption1)

        rightCombinationOption2 = new CombinationOption()
        rightCombinationOption2.setContent(RIGHT_COMBINATION_OPTION_2_CONTENT)
        rightCombinationOption2.setType(CombinationOption.Type.RIGHT)
        rightCombinationOption2.setQuestionDetails(questionDetails)
        combinationOptionRepository.save(rightCombinationOption2)

        and: '1 combine 2 item combination questions'
        combination = new Combination(leftCombinationOption1, rightCombinationOption1)
        combination.setQuestionDetails(questionDetails)
        combinationRepository.save(combination)
    }

    def "export an item combination question to xml"() {
        when:
        def questionsXML = questionService.exportQuestionsToXml()

        then:
        questionsXML != null
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
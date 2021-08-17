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
class ImportItemCombinationQuestionFromXMLTest extends SpockTest {

    def question
    def leftCombinationOption1
    def leftCombinationOption2
    def rightCombinationOption1
    def rightCombinationOption2
    def combination1
    def combination2

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

        and: '2 combinations'
        combination1 = new Combination(leftCombinationOption1, rightCombinationOption1)
        combination1.setQuestionDetails(questionDetails)
        combinationRepository.save(combination1)

        combination2 = new Combination(leftCombinationOption2, rightCombinationOption2)
        combination2.setQuestionDetails(questionDetails)
        combinationRepository.save(combination2)
    }

    def "export and import an item combination question - xml"() {
        def questionsXml = questionService.exportQuestionsToXml()
        print questionsXml
        questionService.removeQuestion(question.getId())

        when:
        questionService.importQuestionsFromXml(questionsXml)

        then:
        questionRepository.findQuestions(externalCourse.getId()).size() == 1
        def questionResult = questionService.findQuestions(externalCourse.getId()).get(0)
        questionResult.getKey() == null
        questionResult.getTitle() == QUESTION_1_TITLE
        questionResult.getContent() == QUESTION_1_CONTENT
        questionResult.getStatus() == Question.Status.AVAILABLE.name()

        questionResult.getQuestionDetailsDto().getOptions().size() == 4

        def resLeftCombinationOption1 = questionResult.getQuestionDetailsDto().getOptions().get(0)
        resLeftCombinationOption1.getContent() == leftCombinationOption1.getContent()
        resLeftCombinationOption1.getType() == leftCombinationOption1.getType()

        def resLeftCombinationOption2 = questionResult.getQuestionDetailsDto().getOptions().get(1)
        resLeftCombinationOption2.getContent() == leftCombinationOption2.getContent()
        resLeftCombinationOption2.getType() == leftCombinationOption2.getType()

        def resRightCombinationOption1 = questionResult.getQuestionDetailsDto().getOptions().get(2)
        resRightCombinationOption1.getContent() == rightCombinationOption1.getContent()
        resRightCombinationOption1.getType() == rightCombinationOption1.getType()

        def resRightCombinationOption2 = questionResult.getQuestionDetailsDto().getOptions().get(3)
        resRightCombinationOption2.getContent() == rightCombinationOption2.getContent()
        resRightCombinationOption2.getType() == rightCombinationOption2.getType()

        questionResult.getQuestionDetailsDto().getCombinations().size() == 2

        def resCombination1 = questionResult.getQuestionDetailsDto().getCombinations().get(0)
        resCombination1.getLeftOption().getContent().equals(leftCombinationOption1.getContent())
        resCombination1.getLeftOption().getType() == leftCombinationOption1.getType()
        resCombination1.getRightOption().getContent().equals(rightCombinationOption1.getContent())
        resCombination1.getRightOption().getType() == rightCombinationOption1.getType()

        def resCombination2 = questionResult.getQuestionDetailsDto().getCombinations().get(1)
        resCombination2.getLeftOption().getContent().equals(leftCombinationOption2.getContent())
        resCombination2.getLeftOption().getType() == leftCombinationOption2.getType()
        resCombination2.getRightOption().getContent().equals(rightCombinationOption2.getContent())
        resCombination2.getRightOption().getType() == rightCombinationOption1.getType()
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
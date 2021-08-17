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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.CombinationOptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.CombinationDto
import spock.lang.Unroll

@DataJpaTest
class EditItemCombinationQuestionTest extends SpockTest {

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

    /*def "add new option to the right column"() {
        given: "a changed question"
        def questionDto = new QuestionDto(question)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())
        and: '1 new option'
        def options = new ArrayList<CombinationOptionDto>()
        def combinations = new ArrayList<CombinationDto>()
        def combinationDto = new CombinationDto()

        def optionDto = new CombinationOptionDto(leftCombinationOption1)
        options.add(optionDto)
        combinationDto.setLeftOption(optionDto)

        optionDto = new CombinationOptionDto(leftCombinationOption2)
        options.add(optionDto)

        optionDto = new CombinationOptionDto(rightCombinationOption1)
        options.add(optionDto)
        combinationDto.setRightOption(optionDto)

        optionDto = new CombinationOptionDto(rightCombinationOption2)
        options.add(optionDto)

        optionDto = new CombinationOptionDto()
        optionDto.setType(CombinationOption.Type.RIGHT)
        optionDto.setContent(RIGHT_COMBINATION_OPTION_3_CONTENT)
        options.add(optionDto)

        combinations.add(combinationDto)

        questionDto.getQuestionDetailsDto().setOptions(options)
        questionDto.getQuestionDetailsDto().setCombinations(combinations)

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then:
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() == question.getId()
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT

        and: 'are not changed'
        result.getStatus() == Question.Status.AVAILABLE
        result.getImage() == null

        and: 'an option is added'
        result.getQuestionDetails().getOptions().size() == 5
        result.getQuestionDetails().getCombinations().size() == 1

        def resLeftCombinationOption1 = result.getQuestionDetails().getOptions()
                .stream()
                .filter({ option -> option.getId() == leftCombinationOption1.getId()})
                .findAny().orElse(null)
        resLeftCombinationOption1.getContent() == LEFT_COMBINATION_OPTION_1_CONTENT
        resLeftCombinationOption1.getType() == CombinationOption.Type.LEFT

        def resLeftCombinationOption2 = result.getQuestionDetails().getOptions()
                .stream()
                .filter({ option -> option.getId() == leftCombinationOption2.getId()})
                .findAny().orElse(null)
        resLeftCombinationOption2.getContent() == LEFT_COMBINATION_OPTION_2_CONTENT
        resLeftCombinationOption2.getType() == CombinationOption.Type.LEFT

        def resRightCombinationOption1 = result.getQuestionDetails().getOptions()
                .stream()
                .filter({ option -> option.getId() == rightCombinationOption1.getId()})
                .findAny().orElse(null)
        resRightCombinationOption1.getContent() == RIGHT_COMBINATION_OPTION_1_CONTENT
        resRightCombinationOption1.getType() == CombinationOption.Type.RIGHT

        def resRightCombinationOption2 = result.getQuestionDetails().getOptions()
                .stream()
                .filter({ option -> option.getId() == rightCombinationOption2.getId()})
                .findAny().orElse(null)
        resRightCombinationOption2.getContent() == RIGHT_COMBINATION_OPTION_2_CONTENT
        resRightCombinationOption2.getType() == CombinationOption.Type.RIGHT

        def resRightCombinationOption3 = result.getQuestionDetails().getOptions().get(4)
        resRightCombinationOption3.getContent() == RIGHT_COMBINATION_OPTION_3_CONTENT
        resRightCombinationOption3.getType() == CombinationOption.Type.RIGHT

        def resCombination = result.getQuestionDetails().getCombinations()
                .stream()
                .filter({ comb -> comb.getId() == combination.getId()})
                .findAny().orElse(null)
        resCombination.getLeftOption().getContent().equals(leftCombinationOption1.getContent())
        resCombination.getLeftOption().getType() == leftCombinationOption1.getType()
        resCombination.getRightOption().getContent().equals(rightCombinationOption1.getContent())
        resCombination.getRightOption().getType() == rightCombinationOption1.getType()
    }*/

    /*@Unroll
    def "change item combination option content and column"() {
        given: "a changed question"
        def questionDto = new QuestionDto(question)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())
        and: '1 option content changed'
        def options = new ArrayList<CombinationOptionDto>()
        def combinations = new ArrayList<CombinationDto>()
        def combinationDto = new CombinationDto()

        def optionDto = new CombinationOptionDto(leftCombinationOption1)
        options.add(optionDto)
        combinationDto.setLeftOption(optionDto)

        optionDto = new CombinationOptionDto(leftCombinationOption2)
        optionDto.setContent(content)
        optionDto.setType(type)
        options.add(optionDto)

        optionDto = new CombinationOptionDto(rightCombinationOption1)
        options.add(optionDto)
        combinationDto.setRightOption(optionDto)

        optionDto = new CombinationOptionDto(rightCombinationOption2)
        options.add(optionDto)

        combinations.add(combinationDto)

        questionDto.getQuestionDetailsDto().setOptions(options)
        questionDto.getQuestionDetailsDto().setCombinations(combinations)

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then:
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() == question.getId()
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT

        and: 'are not changed'
        result.getStatus() == Question.Status.AVAILABLE
        result.getImage() == null

        and: 'an option content is changed'
        result.getQuestionDetails().getOptions().size() == 4

        def resLeftCombinationOption1 = result.getQuestionDetails().getOptions()
                .stream()
                .filter({ option -> option.getId() == leftCombinationOption1.getId()})
                .findAny().orElse(null)
        resLeftCombinationOption1.getContent() == LEFT_COMBINATION_OPTION_1_CONTENT
        resLeftCombinationOption1.getType() == CombinationOption.Type.LEFT

        def resLeftCombinationOption2 = result.getQuestionDetails().getOptions()
                .stream()
                .filter({ option -> option.getId() == leftCombinationOption2.getId()})
                .findAny().orElse(null)
        resLeftCombinationOption2.getContent() == content
        resLeftCombinationOption2.getType() == type

        def resRightCombinationOption1 = result.getQuestionDetails().getOptions()
                .stream()
                .filter({ option -> option.getId() == rightCombinationOption1.getId()})
                .findAny().orElse(null)
        resRightCombinationOption1.getContent() == RIGHT_COMBINATION_OPTION_1_CONTENT
        resRightCombinationOption1.getType() == CombinationOption.Type.RIGHT

        def resRightCombinationOption2 = result.getQuestionDetails().getOptions()
                .stream()
                .filter( option -> option.getId() == rightCombinationOption2.getId())
                .findAny().orElse(null)
        resRightCombinationOption2.getContent() == RIGHT_COMBINATION_OPTION_2_CONTENT
        resRightCombinationOption2.getType() == CombinationOption.Type.RIGHT

        def resCombination = result.getQuestionDetails().getCombinations()
                .stream()
                .filter(comb -> comb.getId() == combination.getId())
                .findAny().orElse(null)
        resCombination.getLeftOption().getContent() == resLeftCombinationOption1.getContent()
        resCombination.getLeftOption().getType() == resLeftCombinationOption1.getType()
        resCombination.getRightOption().getContent() == resRightCombinationOption1.getContent()
        resCombination.getRightOption().getType() == resRightCombinationOption1.getType()

        where:

        content                             | type
        LEFT_COMBINATION_OPTION_3_CONTENT   | CombinationOption.Type.LEFT
        RIGHT_COMBINATION_OPTION_3_CONTENT  | CombinationOption.Type.RIGHT
    }*/

    @Unroll
    def "cannot add duplicate option on left or right column"() {
        given: "a changed question"
        def questionDto = new QuestionDto(question)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())
        and: '1 option content changed'
        def options = new ArrayList<CombinationOptionDto>()
        def combinations = new ArrayList<CombinationDto>()
        def combinationDto = new CombinationDto()

        def optionDto = new CombinationOptionDto(leftCombinationOption1)
        options.add(optionDto)
        combinationDto.setLeftOption(optionDto)

        optionDto = new CombinationOptionDto(leftCombinationOption2)
        options.add(optionDto)

        optionDto = new CombinationOptionDto(rightCombinationOption1)
        options.add(optionDto)
        combinationDto.setRightOption(optionDto)

        optionDto = new CombinationOptionDto(rightCombinationOption2)
        options.add(optionDto)

        optionDto = new CombinationOptionDto()
        optionDto.setType(type)
        optionDto.setContent(content)
        options.add(optionDto)

        combinations.add(combinationDto)

        questionDto.getQuestionDetailsDto().setOptions(options)
        questionDto.getQuestionDetailsDto().setCombinations(combinations)

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.DUPLICATE_ITEM_COMBINATION_OPTION

        where:

        content                             | type
        LEFT_COMBINATION_OPTION_1_CONTENT   | CombinationOption.Type.LEFT
        RIGHT_COMBINATION_OPTION_1_CONTENT  | CombinationOption.Type.RIGHT
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
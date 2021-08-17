package pt.ulisboa.tecnico.socialsoftware.tutor.pci.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CombinationOption
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.CombinationOptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.CombinationDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import spock.lang.Unroll

@DataJpaTest
class CreateItemCombinationQuestionTest extends SpockTest {

    def setup() {
        createExternalCourseAndExecution()
    }

    def "create an item combination question with two items for each column"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())

        and: 'combinationOptionDtos'
        def leftCombinationOptionDto1 = new CombinationOptionDto()
        leftCombinationOptionDto1.setContent(LEFT_COMBINATION_OPTION_1_CONTENT)
        leftCombinationOptionDto1.setType(CombinationOption.Type.LEFT)
        def leftCombinationOptionDto2 = new CombinationOptionDto()
        leftCombinationOptionDto2.setContent(LEFT_COMBINATION_OPTION_2_CONTENT)
        leftCombinationOptionDto2.setType(CombinationOption.Type.LEFT)

        def rightCombinationOptionDto1 = new CombinationOptionDto()
        rightCombinationOptionDto1.setContent(RIGHT_COMBINATION_OPTION_1_CONTENT)
        rightCombinationOptionDto1.setType(CombinationOption.Type.RIGHT)
        def rightCombinationOptionDto2 = new CombinationOptionDto()
        rightCombinationOptionDto2.setContent(RIGHT_COMBINATION_OPTION_2_CONTENT)
        rightCombinationOptionDto2.setType(CombinationOption.Type.RIGHT)

        def options = new ArrayList<CombinationOptionDto>()
        options.add(leftCombinationOptionDto1)
        options.add(leftCombinationOptionDto2)
        options.add(rightCombinationOptionDto1)
        options.add(rightCombinationOptionDto2)

        def combinations = new ArrayList<CombinationDto>()

        def combination = new CombinationDto()
        combination.setLeftOption(leftCombinationOptionDto1)
        combination.setRightOption(rightCombinationOptionDto1)
        combinations.add(combination)

        questionDto.getQuestionDetailsDto().setOptions(options)
        questionDto.getQuestionDetailsDto().setCombinations(combinations)

        when:
        questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "the correct question is inside the repository"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.AVAILABLE
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getImage() == null
        result.getCourse().getName() == COURSE_1_NAME
        externalCourse.getQuestions().contains(result)

        def itemCombinationQuestion = result.getQuestionDetails()

        def resOptions = itemCombinationQuestion.getOptions()

        resOptions.size() == 4

        resOptions.get(0).getContent() == LEFT_COMBINATION_OPTION_1_CONTENT
        resOptions.get(0).getType() == CombinationOption.Type.LEFT
        resOptions.get(1).getContent() == LEFT_COMBINATION_OPTION_2_CONTENT
        resOptions.get(1).getType() == CombinationOption.Type.LEFT
        resOptions.get(2).getContent() == RIGHT_COMBINATION_OPTION_1_CONTENT
        resOptions.get(2).getType() == CombinationOption.Type.RIGHT
        resOptions.get(3).getContent() == RIGHT_COMBINATION_OPTION_2_CONTENT
        resOptions.get(3).getType() == CombinationOption.Type.RIGHT

        def resCombinations = itemCombinationQuestion.getCombinations()
        resCombinations.size() == 1

        resCombinations.get(0).getLeftOption().getContent() == leftCombinationOptionDto1.getContent()
        resCombinations.get(0).getLeftOption().getType() == leftCombinationOptionDto1.getType()

        resCombinations.get(0).getRightOption().getContent() == rightCombinationOptionDto1.getContent()
        resCombinations.get(0).getRightOption().getType() == rightCombinationOptionDto1.getType()
    }

    def "create an item combination question without combinations"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())

        and: 'combinationOptionDtos'
        def leftCombinationOptionDto1 = new CombinationOptionDto()
        leftCombinationOptionDto1.setContent(LEFT_COMBINATION_OPTION_1_CONTENT)
        leftCombinationOptionDto1.setType(CombinationOption.Type.LEFT)
        def leftCombinationOptionDto2 = new CombinationOptionDto()
        leftCombinationOptionDto2.setContent(LEFT_COMBINATION_OPTION_2_CONTENT)
        leftCombinationOptionDto2.setType(CombinationOption.Type.LEFT)

        def rightCombinationOptionDto1 = new CombinationOptionDto()
        rightCombinationOptionDto1.setContent(RIGHT_COMBINATION_OPTION_1_CONTENT)
        rightCombinationOptionDto1.setType(CombinationOption.Type.RIGHT)
        def rightCombinationOptionDto2 = new CombinationOptionDto()
        rightCombinationOptionDto2.setContent(RIGHT_COMBINATION_OPTION_2_CONTENT)
        rightCombinationOptionDto2.setType(CombinationOption.Type.RIGHT)

        def options = new ArrayList<CombinationOptionDto>()
        options.add(leftCombinationOptionDto1)
        options.add(leftCombinationOptionDto2)
        options.add(rightCombinationOptionDto1)
        options.add(rightCombinationOptionDto2)

        questionDto.getQuestionDetailsDto().setOptions(options)

        when:
        questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.ZERO_COMBINATIONS

    }

    @Unroll
    def "cannot create an item combination question without a column"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())

        and: "one option"
        def optionDto = new CombinationOptionDto()
        optionDto.setContent(content)
        optionDto.setType(type)
        def options = new ArrayList<CombinationOptionDto>()
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

        when:
        def result = questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.CANNOT_CREATE_ITEM_COMBINATION_QUESTION_WITH_EMPTY_COLUMN

        where:
        content                             | type
        LEFT_COMBINATION_OPTION_1_CONTENT   | CombinationOption.Type.LEFT
        RIGHT_COMBINATION_OPTION_1_CONTENT  | CombinationOption.Type.RIGHT
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
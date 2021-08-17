package pt.ulisboa.tecnico.socialsoftware.tutor.pem.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MultipleChoiceQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto

@DataJpaTest
class CreateMultipleAnswerQuestionTest extends SpockTest{

    def setup() {
        createExternalCourseAndExecution()
    }

    def "create question with 1 correct option"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        and: 'OptionDto'
        def opt1 = new OptionDto()
        opt1.setContent(OPTION_1_CONTENT)
        opt1.setCorrect(true)
        def opt2 = new OptionDto()
        opt2.setContent(OPTION_2_CONTENT)
        opt2.setCorrect(false)
        def opt3 = new OptionDto()
        opt3.setContent(OPTION_3_CONTENT)
        opt3.setCorrect(false)
        def opt4 = new OptionDto()
        opt4.setContent(OPTION_4_CONTENT)
        opt4.setCorrect(false)
        def options = new ArrayList<OptionDto>()
        options.add(opt1)
        options.add(opt2)
        options.add(opt3)
        options.add(opt4)
        questionDto.getQuestionDetailsDto().setOptions(options)

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
        result.getQuestionDetails().getOptions().size() == 4
        result.getCourse().getName() == COURSE_1_NAME
        externalCourse.getQuestions().contains(result)
        def resOption = result.getQuestionDetails().getOptions().get(0)
        resOption.getContent() == OPTION_1_CONTENT
        resOption.isCorrect()
    }

    def "create question with 2 correct options"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        and: 'OptionDto'
        def opt1 = new OptionDto()
        opt1.setContent(OPTION_1_CONTENT)
        opt1.setCorrect(true)
        def opt2 = new OptionDto()
        opt2.setContent(OPTION_2_CONTENT)
        opt2.setCorrect(true)
        def opt3 = new OptionDto()
        opt3.setContent(OPTION_3_CONTENT)
        opt3.setCorrect(false)
        def options = new ArrayList<OptionDto>()
        options.add(opt1)
        options.add(opt2)
        options.add(opt3)
        questionDto.getQuestionDetailsDto().setOptions(options)

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
        result.getQuestionDetails().getOptions().size() == 3
        result.getCourse().getName() == COURSE_1_NAME
        externalCourse.getQuestions().contains(result)
        def resOption = result.getQuestionDetails().getOptions().get(0)
        def resOption2 = result.getQuestionDetails().getOptions().get(1)
        resOption.getContent() == OPTION_1_CONTENT
        resOption.isCorrect()
        resOption2.getContent() == OPTION_2_CONTENT
        resOption2.isCorrect()
    }

    def "create question with 2 correct options with different importance"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        and: 'OptionDto'
        def opt1 = new OptionDto()
        opt1.setContent(OPTION_1_CONTENT)
        opt1.setCorrect(true)
        opt1.setImportance(1)
        def opt2 = new OptionDto()
        opt2.setContent(OPTION_2_CONTENT)
        opt2.setCorrect(true)
        opt2.setImportance(2)
        def opt3 = new OptionDto()
        opt3.setContent(OPTION_3_CONTENT)
        opt3.setCorrect(false)
        def options = new ArrayList<OptionDto>()
        options.add(opt1)
        options.add(opt2)
        options.add(opt3)
        questionDto.getQuestionDetailsDto().setOptions(options)

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
        result.getQuestionDetails().getOptions().size() == 3
        result.getCourse().getName() == COURSE_1_NAME
        externalCourse.getQuestions().contains(result)
        def resOption = result.getQuestionDetails().getOptions().get(0)
        def resOption2 = result.getQuestionDetails().getOptions().get(1)
        resOption.getContent() == OPTION_1_CONTENT
        resOption.isCorrect()
        resOption2.getContent() == OPTION_2_CONTENT
        resOption2.isCorrect()
        resOption.getImportance() != resOption2.getImportance()

    }

    def "create question with 3 correct options"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        and: 'OptionDto'
        def opt1 = new OptionDto()
        opt1.setContent(OPTION_1_CONTENT)
        opt1.setCorrect(true)
        def opt2 = new OptionDto()
        opt2.setContent(OPTION_2_CONTENT)
        opt2.setCorrect(true)
        def opt3 = new OptionDto()
        opt3.setContent(OPTION_3_CONTENT)
        opt3.setCorrect(true)
        def opt4 = new OptionDto()
        opt4.setContent(OPTION_4_CONTENT)
        opt4.setCorrect(false)
        def options = new ArrayList<OptionDto>()
        options.add(opt1)
        options.add(opt2)
        options.add(opt3)
        options.add(opt4)
        questionDto.getQuestionDetailsDto().setOptions(options)

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
        result.getQuestionDetails().getOptions().size() == 4
        result.getCourse().getName() == COURSE_1_NAME
        externalCourse.getQuestions().contains(result)
        def resOption = result.getQuestionDetails().getOptions().get(0)
        def resOption2 = result.getQuestionDetails().getOptions().get(1)
        def resOption3 = result.getQuestionDetails().getOptions().get(2)
        resOption.getContent() == OPTION_1_CONTENT
        resOption.isCorrect()
        resOption2.getContent() == OPTION_2_CONTENT
        resOption2.isCorrect()
        resOption3.isCorrect()
    }

    def "create question with 3 correct options with different importance"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        and: 'OptionDto'
        def opt1 = new OptionDto()
        opt1.setContent(OPTION_1_CONTENT)
        opt1.setCorrect(true)
        opt1.setImportance(2)
        def opt2 = new OptionDto()
        opt2.setContent(OPTION_2_CONTENT)
        opt2.setCorrect(true)
        opt2.setImportance(3)
        def opt3 = new OptionDto()
        opt3.setContent(OPTION_3_CONTENT)
        opt3.setCorrect(true)
        opt3.setImportance(1)
        def opt4 = new OptionDto()
        opt4.setContent(OPTION_4_CONTENT)
        opt4.setCorrect(false)
        def options = new ArrayList<OptionDto>()
        options.add(opt1)
        options.add(opt2)
        options.add(opt3)
        options.add(opt4)
        questionDto.getQuestionDetailsDto().setOptions(options)

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
        result.getQuestionDetails().getOptions().size() == 4
        result.getCourse().getName() == COURSE_1_NAME
        externalCourse.getQuestions().contains(result)
        def resOption = result.getQuestionDetails().getOptions().get(0)
        def resOption2 = result.getQuestionDetails().getOptions().get(1)
        def resOption3 = result.getQuestionDetails().getOptions().get(2)
        resOption.getContent() == OPTION_1_CONTENT
        resOption.isCorrect()
        resOption2.getContent() == OPTION_2_CONTENT
        resOption2.isCorrect()
        resOption3.isCorrect()
        resOption.getImportance() != resOption2.getImportance()
        resOption.getImportance() != resOption3.getImportance()
        resOption2.getImportance() != resOption3.getImportance()
    }

    def "create question with all options correct"() {

        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        and: 'OptionDto'
        def opt1 = new OptionDto()
        opt1.setContent(OPTION_1_CONTENT)
        opt1.setCorrect(true)
        def opt2 = new OptionDto()
        opt2.setContent(OPTION_2_CONTENT)
        opt2.setCorrect(true)
        def opt3 = new OptionDto()
        opt3.setContent(OPTION_3_CONTENT)
        opt3.setCorrect(true)
        def opt4 = new OptionDto()
        opt4.setContent(OPTION_4_CONTENT)
        opt4.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(opt1)
        options.add(opt2)
        options.add(opt3)
        options.add(opt4)
        questionDto.getQuestionDetailsDto().setOptions(options)

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
        result.getQuestionDetails().getOptions().size() == 4
        result.getCourse().getName() == COURSE_1_NAME
        externalCourse.getQuestions().contains(result)
        def resOption = result.getQuestionDetails().getOptions().get(0)
        def resOption2 = result.getQuestionDetails().getOptions().get(1)
        def resOption3 = result.getQuestionDetails().getOptions().get(2)
        def resOption4 = result.getQuestionDetails().getOptions().get(3)
        resOption.getContent() == OPTION_1_CONTENT
        resOption.isCorrect()
        resOption2.getContent() == OPTION_2_CONTENT
        resOption2.isCorrect()
        resOption3.getContent() == OPTION_3_CONTENT
        resOption3.isCorrect()
        resOption4.getContent() == OPTION_4_CONTENT
        resOption4.isCorrect()
    }

    def "create question with all options correct with different importance"() {

        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        and: 'OptionDto'
        def opt1 = new OptionDto()
        opt1.setContent(OPTION_1_CONTENT)
        opt1.setCorrect(true)
        opt1.setImportance(2)
        def opt2 = new OptionDto()
        opt2.setContent(OPTION_2_CONTENT)
        opt2.setCorrect(true)
        opt2.setImportance(3)
        def opt3 = new OptionDto()
        opt3.setContent(OPTION_3_CONTENT)
        opt3.setCorrect(true)
        opt3.setImportance(1)
        def opt4 = new OptionDto()
        opt4.setContent(OPTION_4_CONTENT)
        opt4.setCorrect(true)
        opt3.setImportance(4)
        def options = new ArrayList<OptionDto>()
        options.add(opt1)
        options.add(opt2)
        options.add(opt3)
        options.add(opt4)
        questionDto.getQuestionDetailsDto().setOptions(options)

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
        result.getQuestionDetails().getOptions().size() == 4
        result.getCourse().getName() == COURSE_1_NAME
        externalCourse.getQuestions().contains(result)
        def resOption = result.getQuestionDetails().getOptions().get(0)
        def resOption2 = result.getQuestionDetails().getOptions().get(1)
        def resOption3 = result.getQuestionDetails().getOptions().get(2)
        def resOption4 = result.getQuestionDetails().getOptions().get(3)
        resOption.getContent() == OPTION_1_CONTENT
        resOption.isCorrect()
        resOption2.getContent() == OPTION_2_CONTENT
        resOption2.isCorrect()
        resOption3.getContent() == OPTION_3_CONTENT
        resOption3.isCorrect()
        resOption4.getContent() == OPTION_4_CONTENT
        resOption4.isCorrect()
        resOption.getImportance() != resOption2.getImportance()
        resOption.getImportance() != resOption3.getImportance()
        resOption2.getImportance() != resOption3.getImportance()
        resOption.getImportance() != resOption4.getImportance()
        resOption2.getImportance() != resOption4.getImportance()
        resOption3.getImportance() != resOption4.getImportance()
    }

    def "create question without correct options"() {

        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setNumberOfCorrect(0)
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        and: 'OptionDto'
        def opt1 = new OptionDto()
        opt1.setContent(OPTION_1_CONTENT)
        opt1.setCorrect(false)
        def opt2 = new OptionDto()
        opt2.setContent(OPTION_2_CONTENT)
        opt2.setCorrect(false)
        def opt3 = new OptionDto()
        opt3.setContent(OPTION_3_CONTENT)
        opt3.setCorrect(false)
        def opt4 = new OptionDto()
        opt4.setContent(OPTION_4_CONTENT)
        opt4.setCorrect(false)
        def options = new ArrayList<OptionDto>()
        options.add(opt1)
        options.add(opt2)
        options.add(opt3)
        options.add(opt4)
        questionDto.getQuestionDetailsDto().setOptions(options)

        when:
        questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.AT_LEAST_ONE_OPTION_NEEDED;
    }

    def "create question with no options"() {

        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setNumberOfAnswers(0)
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        def options = new ArrayList<OptionDto>()
        questionDto.getQuestionDetailsDto().setOptions(options)

        when:
        questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.AT_LEAST_ONE_OPTION_NEEDED;
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}

}
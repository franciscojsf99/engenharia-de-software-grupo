package pt.ulisboa.tecnico.socialsoftware.tutor.pra.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenAnswerQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.*

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException

@DataJpaTest
class CreateOpenAnswerQuestionTest extends SpockTest {

    def setup() {
        createExternalCourseAndExecution()
    }

    def "create an open answer question"() {
        given: "a questionDto"
        
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())        
        questionDto.setQuestionDetailsDto(new OpenAnswerQuestionDto())

        questionDto.getQuestionDetailsDto().setAnswer(QUESTION_1_ANSWER)

        when:
        def rawResult = questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "the correct data is sent back"
        rawResult instanceof QuestionDto
        def result = (QuestionDto) rawResult
        result.getId() != null
        result.getStatus() == Question.Status.AVAILABLE.toString()
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getImage() == null
        result.getQuestionDetailsDto().getAnswer() == QUESTION_1_ANSWER

        then: "the correct question is inside the repository"
        questionRepository.count() == 1L
        def repoResult = questionRepository.findAll().get(0)
        repoResult.getId() != null
        repoResult.getKey() == 1
        repoResult.getStatus() == Question.Status.AVAILABLE
        repoResult.getTitle() == QUESTION_1_TITLE
        repoResult.getContent() == QUESTION_1_CONTENT
        repoResult.getImage() == null
        repoResult.getCourse().getName() == COURSE_1_NAME
        externalCourse.getQuestions().contains(repoResult)

        def repoOpenAnswer = (OpenAnswerQuestion) repoResult.getQuestionDetails()
        repoOpenAnswer.getAnswer() == QUESTION_1_ANSWER
    }

    def "create an open answer question where the question content is blank"() {
        given: "a questionDto"

        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent("   ")
        questionDto.setStatus(Question.Status.AVAILABLE.name())        
        questionDto.setQuestionDetailsDto(new OpenAnswerQuestionDto())
        
        questionDto.getQuestionDetailsDto().setAnswer(QUESTION_1_ANSWER)

        when:
        def result = questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.INVALID_CONTENT_FOR_QUESTION
    }

    def "create an open answer question where the question content is empty"() {
        given: "a questionDto"

        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(null)
        questionDto.setStatus(Question.Status.AVAILABLE.name())        
        questionDto.setQuestionDetailsDto(new OpenAnswerQuestionDto())
        
        questionDto.getQuestionDetailsDto().setAnswer(QUESTION_1_ANSWER)

        when:
        def result = questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.INVALID_CONTENT_FOR_QUESTION
    }

    def "create an open answer question where the title is blank"() {
        given: "a questionDto"

        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle("   ")
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())        
        questionDto.setQuestionDetailsDto(new OpenAnswerQuestionDto())
        
        questionDto.getQuestionDetailsDto().setAnswer(QUESTION_1_ANSWER)

        when:
        def result = questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.INVALID_TITLE_FOR_QUESTION
    }

    def "create an open answer question where the title is empty"() {
        given: "a questionDto"

        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(null)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())        
        questionDto.setQuestionDetailsDto(new OpenAnswerQuestionDto())
        
        questionDto.getQuestionDetailsDto().setAnswer(QUESTION_1_ANSWER)

        when:
        def result = questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.INVALID_TITLE_FOR_QUESTION
    }

    def "create an open answer question where the answer is blank"() {
        given: "a questionDto"

        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())        

        def openAnswerQuestionDto = new OpenAnswerQuestionDto()
        openAnswerQuestionDto.setAnswer("   ")
        questionDto.setQuestionDetailsDto(openAnswerQuestionDto)

        when:
        def result = questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.INVALID_CONTENT_FOR_ANSWER
    }

    def "create an open answer question where the answer is empty"() {
        given: "a questionDto"

        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())   
            
        def openAnswerQuestionDto = new OpenAnswerQuestionDto()
        openAnswerQuestionDto.setAnswer(null)
        questionDto.setQuestionDetailsDto(openAnswerQuestionDto)

        when:
        def result = questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.INVALID_CONTENT_FOR_ANSWER
    }
    
    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
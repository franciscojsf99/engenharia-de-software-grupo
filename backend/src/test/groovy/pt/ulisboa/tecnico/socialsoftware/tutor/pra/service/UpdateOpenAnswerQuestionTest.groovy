package pt.ulisboa.tecnico.socialsoftware.tutor.pra.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenAnswerQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OpenAnswerQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException

@DataJpaTest
class UpdateOpenAnswerQuestionTest extends SpockTest {

    def question
    def user

    def setup(){
        createExternalCourseAndExecution()

        user = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        user.addCourse(externalCourseExecution)
        userRepository.save(user)

        and: 'an image'
        def image = new Image()
        image.setUrl(IMAGE_1_URL)
        image.setWidth(20)
        imageRepository.save(image)

        given: "create a question"
        question = new Question()
        question.setCourse(externalCourse)
        question.setKey(1)
        question.setTitle(QUESTION_1_TITLE)
        question.setContent(QUESTION_1_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        question.setImage(image)
        def questionDetails = new OpenAnswerQuestion()
        questionDetails.setAnswer(QUESTION_1_ANSWER)
        question.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        questionRepository.save(question)
    }

    def "update an open answer question"(){
        given: "a changed question"
        def questionDto = new QuestionDto(question)
        questionDto.setTitle(QUESTION_2_TITLE)
        questionDto.setContent(QUESTION_2_CONTENT)
        questionDto.setQuestionDetailsDto(new OpenAnswerQuestionDto())
        and: 'changed answer'
        questionDto.getQuestionDetailsDto().setAnswer(QUESTION_2_ANSWER)

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "the question is changed"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() == question.getId()
        result.getTitle() == QUESTION_2_TITLE
        result.getContent() == QUESTION_2_CONTENT
        and: 'are not changed'
        result.getStatus() == Question.Status.AVAILABLE
        result.getImage() != null
        and: "answer is changed"
        result.getQuestionDetails().getAnswer() == QUESTION_2_ANSWER
    }

    def "update an open answer question with empty title"(){
        given: "a changed question"
        def questionDto = new QuestionDto(question)
        questionDto.setTitle(null)
        questionDto.setQuestionDetailsDto(new OpenAnswerQuestionDto())

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "excpetion is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.INVALID_TITLE_FOR_QUESTION
    }

    def "update an open answer question with blank title"(){
         given: "a changed question"
        def questionDto = new QuestionDto(question)
        questionDto.setTitle("   ")
        questionDto.setQuestionDetailsDto(new OpenAnswerQuestionDto())

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "excpetion is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.INVALID_TITLE_FOR_QUESTION
    }
    
    def "update an open answer question with empty content"(){
        given: "a changed question"
        def questionDto = new QuestionDto(question)
        questionDto.setContent(null)
        questionDto.setQuestionDetailsDto(new OpenAnswerQuestionDto())

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "excpetion is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.INVALID_CONTENT_FOR_QUESTION
    }

    def "update an open answer question with blank content"(){
        given: "a changed question"
        def questionDto = new QuestionDto(question)
        questionDto.setContent("   ")
        questionDto.setQuestionDetailsDto(new OpenAnswerQuestionDto())

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "excpetion is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.INVALID_CONTENT_FOR_QUESTION
    }

    def "update an open answer question with empty answer"(){
        given: "a changed answer"
        def questionDto = new QuestionDto(question)
        questionDto.setQuestionDetailsDto(new OpenAnswerQuestionDto())
        questionDto.getQuestionDetailsDto().setAnswer(null)

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "excpetion is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.INVALID_CONTENT_FOR_ANSWER
    }

    def "update an open answer question with blank answer"(){
        given: "a changed answer"
        def questionDto = new QuestionDto(question)
        questionDto.setQuestionDetailsDto(new OpenAnswerQuestionDto())
        questionDto.getQuestionDetailsDto().setAnswer("   ")

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "excpetion is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.INVALID_CONTENT_FOR_ANSWER
    }
    
    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
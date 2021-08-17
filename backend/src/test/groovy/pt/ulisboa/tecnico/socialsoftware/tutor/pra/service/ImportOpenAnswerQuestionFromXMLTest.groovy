package pt.ulisboa.tecnico.socialsoftware.tutor.pci.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OpenAnswerQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question

@DataJpaTest
class  ImportOpenAnswerQuestionFromXMLTest extends SpockTest {
    def questionId

    def setup(){
        createExternalCourseAndExecution()

        given: "create a question"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        def imageDto = new ImageDto()
        imageDto.setUrl(IMAGE_1_URL)
        imageDto.setWidth(20)
        questionDto.setImage(imageDto)

        def questionDetailsDto = new OpenAnswerQuestionDto()
        questionDto.setQuestionDetailsDto(new OpenAnswerQuestionDto())
        questionDto.getQuestionDetailsDto().setAnswer(QUESTION_1_ANSWER)

        questionId = questionService.createQuestion(externalCourse.getId(), questionDto).getId()
    }

    def "import open answer question to xml"(){
        given: "a xml with a question"
        def questionsXml = questionService.exportQuestionsToXml()
        
        print questionsXml

        and: "a clean database"
        questionService.removeQuestion(questionId)
        
        when:
        questionService.importQuestionsFromXml(questionsXml)

        then:
        questionRepository.findQuestions(externalCourse.getId()).size() == 1
        def questionResult = questionService.findQuestions(externalCourse.getId()).get(0)
        questionResult.getKey() == null
        questionResult.getTitle() == QUESTION_1_TITLE
        questionResult.getContent() == QUESTION_1_CONTENT
        questionResult.getStatus() == Question.Status.AVAILABLE.name()

        def answerResult = questionResult.getQuestionDetailsDto().getAnswer()
        answerResult == QUESTION_1_ANSWER;
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
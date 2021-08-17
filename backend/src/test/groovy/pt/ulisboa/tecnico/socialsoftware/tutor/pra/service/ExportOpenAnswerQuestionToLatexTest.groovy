package pt.ulisboa.tecnico.socialsoftware.tutor.pci.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.*
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenAnswerQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question

@DataJpaTest
class ExportOpenAnswerQuestionToLatexTest extends SpockTest {
    def questionId

    def setup() {
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

    def "export an open answer question to latex"() {
        when:
        def questionsLatex = questionService.exportQuestionsToLatex()

        then:
        questionsLatex != null
    }
    
    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
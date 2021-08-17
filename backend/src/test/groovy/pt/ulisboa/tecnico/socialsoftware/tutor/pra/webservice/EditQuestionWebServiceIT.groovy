package pt.ulisboa.tecnico.socialsoftware.tutor.pra.webservice

import com.fasterxml.jackson.databind.ObjectMapper
import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenAnswerQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OpenAnswerQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EditQuestionWebServiceIT extends SpockTest{

    @LocalServerPort
    private int port

    def question
    def course
    def courseExecution
    def teacher

    def setup() {
        given: "a client, a course and a teacher"
        restClient = new RESTClient("http://localhost:" + port)

        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)

        teacher = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        teacher.addCourse(courseExecution)
        courseExecution.addUser(teacher)
        userRepository.save(teacher)

        and: "created question"
        question = new Question()
        question.setCourse(course)
        question.setKey(1)
        question.setTitle(QUESTION_1_TITLE)
        question.setContent(QUESTION_1_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        def questionDetails = new OpenAnswerQuestion()
        questionDetails.setAnswer(QUESTION_1_ANSWER)
        question.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        questionRepository.save(question)

    }


    def "edit item combination question"() {
        given: "logged in teacher"
        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        and: "a changed question"
        def questionDto = new QuestionDto(question)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setQuestionDetailsDto(new OpenAnswerQuestionDto())

        and: 'an answer'
        questionDto.getQuestionDetailsDto().setAnswer(QUESTION_1_ANSWER)

        when:
        def ow = new ObjectMapper().writer().withDefaultPrettyPrinter()
        def response = restClient.put(
                path: '/questions/' + question.getId(),
                body: ow.writeValueAsString(questionDto),
                requestContentType: 'application/json'
        )

        then:
        questionRepository.count() == 1L

        response.data != null
        response.data.id != null
        response.data.status == Question.Status.AVAILABLE.name()
        response.data.title == QUESTION_1_TITLE
        response.data.content == QUESTION_1_CONTENT
        response.data.image == null

        response.data.questionDetailsDto.type == "open_answer"
        response.data.questionDetailsDto.answer == QUESTION_1_ANSWER
    }

    def "edit open answer question without login"() {
        given: "a question dto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new OpenAnswerQuestionDto())
        questionDto.getQuestionDetailsDto().setAnswer(QUESTION_1_ANSWER)

        when:
        def ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        def response = restClient.post(
                path: '/courses/' + course.getId() + '/questions',
                body: ow.writeValueAsString(questionDto),
                requestContentType: 'application/json'
        )
        then: "the request returns 403"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN
    }


    def cleanup() {
        questionService.removeQuestion(question.getId())
        userRepository.deleteById(teacher.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())
        courseRepository.deleteById(course.getId())
    }
}

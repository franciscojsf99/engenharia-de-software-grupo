package pt.ulisboa.tecnico.socialsoftware.tutor.pra.webservice

import org.apache.http.HttpStatus
import groovyx.net.http.HttpResponseException
import com.fasterxml.jackson.databind.ObjectMapper
import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OpenAnswerQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateOpenAnswerQuestionIT extends SpockTest {
    @LocalServerPort
    private int port

    def course
    def courseExecution
    def teacher
    def student

    def setup() {
        given: "a course, a student and a teacher"
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

        student = new User(USER_2_NAME, USER_2_EMAIL, USER_2_EMAIL,
                User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        student.authUser.setPassword(passwordEncoder.encode(USER_2_PASSWORD))
        student.addCourse(courseExecution)
        courseExecution.addUser(student)
        userRepository.save(student)
    }

    def "create an open answer question as a teacher"(){
        given: "login as a teacher"
        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)
        
        and: "create a question"
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
        
        then:
        response.data != null
        response.data.id != null
        response.data.status == Question.Status.AVAILABLE.name()
        response.data.title == QUESTION_1_TITLE
        response.data.content == QUESTION_1_CONTENT
        response.data.image == null

        response.data.questionDetailsDto.type == "open_answer"

        response.data.questionDetailsDto.answer == QUESTION_1_ANSWER
        
        cleanup:
        questionService.removeQuestion(response.data.id)
    }

    def "create an open answer question as a student"(){
        given: "login as a student"
        createdUserLogin(USER_2_EMAIL, USER_2_PASSWORD)
        
        and: "create a question"
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
        userRepository.deleteById(teacher.getId())
        userRepository.deleteById(student.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())
        courseRepository.deleteById(course.getId())
    }

}
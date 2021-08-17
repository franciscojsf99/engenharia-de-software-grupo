package pt.ulisboa.tecnico.socialsoftware.tutor.pem.webservice

import com.fasterxml.jackson.databind.ObjectMapper
import groovyx.net.http.RESTClient
import groovyx.net.http.HttpResponseException
import org.apache.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MultipleChoiceQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateQuestionWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def course
    def courseExecution
    def teacher
    def student

    def setup() {
        restClient = new RESTClient("http://localhost:" + port)

        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)

        teacher = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL, User.Role.TEACHER, false, AuthUser.Type.TECNICO)
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
    def "create question"() {
        given: "a question dto and a logged in teacher"
        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        and: 'OptionDtos'
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

        response.data.questionDetailsDto.type == "multiple_choice"

        def resOptions = response.data.questionDetailsDto.options

        resOptions.size() == 3L
        //response.data.questionDetailsDto.getNumCorrectAnswers() == 2
        resOptions.get(0).correct == true
        resOptions.get(1).correct == true
        resOptions.get(2).correct == false
        resOptions.get(0).importance == 1
        resOptions.get(1).importance == 2

        cleanup:
        questionService.removeQuestion(response.data.id)
    }

    def "create a question without login"(){
        given: "a question dto "
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        and: 'OptionDtos'
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

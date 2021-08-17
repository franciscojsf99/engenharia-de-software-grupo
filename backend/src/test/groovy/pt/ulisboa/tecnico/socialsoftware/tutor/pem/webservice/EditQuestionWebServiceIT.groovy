package pt.ulisboa.tecnico.socialsoftware.tutor.pem.webservice

import com.fasterxml.jackson.databind.ObjectMapper
import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage

import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MultipleChoiceQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EditQuestionWebServiceIT extends SpockTest{
    @LocalServerPort
    private int port

    def course
    def courseExecution
    def teacher
    def student
    def question
    def optionOK
    def optionKO
    def option3

    def setup() {
        given: "1 client, 1 course and 2 users (a student and a teacher)"
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

        and: "created question"
        question = new Question()
        question.setCourse(course)
        question.setKey(1)
        question.setTitle(QUESTION_1_TITLE)
        question.setContent(QUESTION_1_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        def questionDetails = new MultipleChoiceQuestion()
        question.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        questionRepository.save(question)

        and: "three options"
        optionOK = new Option()
        optionOK.setContent(OPTION_2_CONTENT)
        optionOK.setCorrect(true)
        optionOK.setSequence(0)
        optionOK.setImportance(2)
        optionOK.setQuestionDetails(questionDetails)
        optionRepository.save(optionOK)

        optionKO = new Option()
        optionKO.setContent(OPTION_1_CONTENT)
        optionKO.setCorrect(false)
        optionKO.setSequence(1)
        optionKO.setQuestionDetails(questionDetails)
        optionRepository.save(optionKO)

        option3 = new Option()
        option3.setContent(OPTION_3_CONTENT)
        option3.setCorrect(true)
        option3.setSequence(2)
        option3.setImportance(3)
        option3.setQuestionDetails(questionDetails)
        optionRepository.save(option3)
    }
    def "edit item combination question"(){
        given: "logged in teacher"
        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        and: "a changed question"
        def questionDto = new QuestionDto(question)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        and:"different options(dtos)"
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
        def ow = new ObjectMapper().writer().withDefaultPrettyPrinter()
        def response = restClient.put(
                path: '/questions/' + question.getId(),
                body: ow.writeValueAsString(questionDto),
                requestContentType: 'application/json'
        )
        then:
        questionRepository.count() == 1L
        and: "response status OK"
        response.status == 200
        response.data != null
        response.data.id != null
        response.data.status == Question.Status.AVAILABLE.name()
        response.data.title == QUESTION_1_TITLE
        response.data.content == QUESTION_1_CONTENT
        response.data.image == null
        response.data.questionDetailsDto.type == "multiple_choice"

        def resOptions = response.data.questionDetailsDto.options

        resOptions.size() == 3L
        resOptions.get(0).content == OPTION_1_CONTENT
        resOptions.get(1).content == OPTION_2_CONTENT
        resOptions.get(2).content == OPTION_3_CONTENT
        resOptions.get(0).correct
        resOptions.get(1).correct
        !resOptions.get(2).correct
        resOptions.get(0).importance == 1

    }

    def "edit item combination question as a student"(){
        given: "logged in student"
        createdUserLogin(USER_2_EMAIL, USER_2_PASSWORD)

        and: "a changed question"
        def questionDto = new QuestionDto(question)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        and:"different options(dtos)"
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
        def ow = new ObjectMapper().writer().withDefaultPrettyPrinter()
        def response = restClient.put(
                path: '/questions/' + question.getId(),
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
        userRepository.deleteById(student.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())
        courseRepository.deleteById(course.getId())
    }

}

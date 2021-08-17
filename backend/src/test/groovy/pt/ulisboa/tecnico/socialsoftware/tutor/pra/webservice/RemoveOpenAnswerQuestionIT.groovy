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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenAnswerQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RemoveOpenAnswerQuestionIT extends SpockTest {
    @LocalServerPort
    private int port

    def course
    def courseExecution
    def teacher
    def student
    def question

    def setup() {
        given: "1 course, 1 student and 1 teacher"
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

        and: "a question"
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

    def "remove an open answer question as a teacher"(){
        given: "login as a teacher"
        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)
        
        when:
        def response = restClient.delete(
                path: '/questions/' + question.getId(),
        )
        
        then:
        questionRepository.count() == 0L
    }

    def "remove an open answer question as a student"(){
        given: "logged in student"
        createdUserLogin(USER_2_EMAIL, USER_2_PASSWORD)

        when:
        def response = restClient.delete(
                path: '/questions/' + question.getId(),
        )

        then: "the request returns 403"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN

        cleanup:
        questionService.removeQuestion(question.getId())
    }

    def cleanup() {
        userRepository.deleteById(teacher.getId())
        userRepository.deleteById(student.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())
        courseRepository.deleteById(course.getId())
    }

}
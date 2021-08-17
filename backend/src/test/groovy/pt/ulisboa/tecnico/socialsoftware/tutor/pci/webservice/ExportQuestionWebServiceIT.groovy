package pt.ulisboa.tecnico.socialsoftware.tutor.pci.webservice

import com.fasterxml.jackson.databind.ObjectMapper
import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Combination
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CombinationOption
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.CombinationDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.CombinationOptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExportQuestionWebServiceIT extends SpockTest {

    @LocalServerPort
    private int port

    def question
    def leftCombinationOption1
    def leftCombinationOption2
    def rightCombinationOption1
    def rightCombinationOption2
    def combination
    def course
    def courseExecution
    def teacher
    def student

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
        def questionDetails = new ItemCombinationQuestion()
        question.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        questionRepository.save(question)

        and: '2 item combination options (left and right)'
        leftCombinationOption1 = new CombinationOption()
        leftCombinationOption1.setContent(LEFT_COMBINATION_OPTION_1_CONTENT)
        leftCombinationOption1.setType(CombinationOption.Type.LEFT)
        leftCombinationOption1.setQuestionDetails(questionDetails)

        rightCombinationOption1 = new CombinationOption()
        rightCombinationOption1.setContent(RIGHT_COMBINATION_OPTION_1_CONTENT)
        rightCombinationOption1.setType(CombinationOption.Type.RIGHT)
        rightCombinationOption1.setQuestionDetails(questionDetails)


        and: '1 combine 2 item combination questions'
        combination = new Combination(leftCombinationOption1, rightCombinationOption1)
        combination.setQuestionDetails(questionDetails)
        combinationRepository.save(combination)


        and: '2 item combination options (left and right)'
        leftCombinationOption2 = new CombinationOption()
        leftCombinationOption2.setContent(LEFT_COMBINATION_OPTION_2_CONTENT)
        leftCombinationOption2.setType(CombinationOption.Type.LEFT)
        leftCombinationOption2.setQuestionDetails(questionDetails)
        combinationOptionRepository.save(leftCombinationOption2)

        rightCombinationOption2 = new CombinationOption()
        rightCombinationOption2.setContent(RIGHT_COMBINATION_OPTION_2_CONTENT)
        rightCombinationOption2.setType(CombinationOption.Type.RIGHT)
        rightCombinationOption2.setQuestionDetails(questionDetails)
        combinationOptionRepository.save(rightCombinationOption2)
    }

    def "export all existent item combination questions from course"() {
        given: "logged in teacher"
        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        and: 'prepare request response'
        restClient.handler.failure = { resp, reader ->
            [response:resp, reader:reader]
        }
        restClient.handler.success = { resp, reader ->
            [response:resp, reader:reader]
        }

        when:
        def map = restClient.get(
                path: '/courses/' + course.getId() + '/questions/export',
                requestContentType: "application/json"
        )

        then: "the response status is OK"
        assert map['response'].status == 200
        assert map['reader'] != null
    }

    def "try to export questions as a student"() {
        given: "logged in student"
        createdUserLogin(USER_2_EMAIL, USER_2_PASSWORD)

        and: 'prepare request response'
        restClient.handler.failure = { resp, reader ->
            [response:resp, reader:reader]
        }
        restClient.handler.success = { resp, reader ->
            [response:resp, reader:reader]
        }

        when:
        def map = restClient.get(
                path: '/courses/' + course.getId() + '/questions/export',
                requestContentType: "application/json"
        )

        then: "the response status is FORBIDDEN"
        assert map['response'].status == HttpStatus.SC_FORBIDDEN
    }

    def cleanup() {
        questionService.removeQuestion(question.getId())
        userRepository.deleteById(teacher.getId())
        userRepository.deleteById(student.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())
        courseRepository.deleteById(course.getId())
    }

}
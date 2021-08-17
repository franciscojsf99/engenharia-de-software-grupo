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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CombinationOption
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.CombinationDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.CombinationOptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateQuestionWebServiceIT extends SpockTest {

    @LocalServerPort
    private int port

    def course
    def courseExecution
    def teacher

    def setup() {
        given: "1 client, 1 course and 1 user (teacher role)"
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
    }

    def "create question"() {
        given: "logged in teacher"
        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        and: "a question dto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())

        and: 'combinationOptionDtos'
        def leftCombinationOptionDto1 = new CombinationOptionDto()
        leftCombinationOptionDto1.setContent(LEFT_COMBINATION_OPTION_1_CONTENT)
        leftCombinationOptionDto1.setType(CombinationOption.Type.LEFT)
        def leftCombinationOptionDto2 = new CombinationOptionDto()
        leftCombinationOptionDto2.setContent(LEFT_COMBINATION_OPTION_2_CONTENT)
        leftCombinationOptionDto2.setType(CombinationOption.Type.LEFT)

        def rightCombinationOptionDto1 = new CombinationOptionDto()
        rightCombinationOptionDto1.setContent(RIGHT_COMBINATION_OPTION_1_CONTENT)
        rightCombinationOptionDto1.setType(CombinationOption.Type.RIGHT)
        def rightCombinationOptionDto2 = new CombinationOptionDto()
        rightCombinationOptionDto2.setContent(RIGHT_COMBINATION_OPTION_2_CONTENT)
        rightCombinationOptionDto2.setType(CombinationOption.Type.RIGHT)

        def options = new ArrayList<CombinationOptionDto>()
        options.add(leftCombinationOptionDto1)
        options.add(leftCombinationOptionDto2)
        options.add(rightCombinationOptionDto1)
        options.add(rightCombinationOptionDto2)

        def combinations = new ArrayList<CombinationDto>()

        def combination = new CombinationDto()
        combination.setLeftOption(leftCombinationOptionDto1)
        combination.setRightOption(rightCombinationOptionDto1)
        combinations.add(combination)

        questionDto.getQuestionDetailsDto().setOptions(options)
        questionDto.getQuestionDetailsDto().setCombinations(combinations)

        when:
        def ow = new ObjectMapper().writer().withDefaultPrettyPrinter()
        def response = restClient.post(
                path: '/courses/' + course.getId() + '/questions',
                body: ow.writeValueAsString(questionDto),
                requestContentType: 'application/json'
        )
        then:
        questionRepository.count() == 1L
        combinationOptionRepository.count() == 4L
        combinationRepository.count() == 1L

        and: "response status OK"
        response.status == 200
        response.data != null
        response.data.id != null
        response.data.status == Question.Status.AVAILABLE.name()
        response.data.title == QUESTION_1_TITLE
        response.data.content == QUESTION_1_CONTENT
        response.data.image == null

        response.data.questionDetailsDto.type == "item_combination"

        def resOptions = response.data.questionDetailsDto.options

        resOptions.size() == 4L

        resOptions.get(0).content == LEFT_COMBINATION_OPTION_1_CONTENT
        resOptions.get(0).type == CombinationOption.Type.LEFT.name()
        resOptions.get(1).content == LEFT_COMBINATION_OPTION_2_CONTENT
        resOptions.get(1).type == CombinationOption.Type.LEFT.name()
        resOptions.get(2).content == RIGHT_COMBINATION_OPTION_1_CONTENT
        resOptions.get(2).type == CombinationOption.Type.RIGHT.name()
        resOptions.get(3).content == RIGHT_COMBINATION_OPTION_2_CONTENT
        resOptions.get(3).type == CombinationOption.Type.RIGHT.name()

        def resCombinations = response.data.questionDetailsDto.combinations
        resCombinations.size() == 1L

        resCombinations.get(0).leftOption.content == leftCombinationOptionDto1.getContent()
        resCombinations.get(0).leftOption.type == leftCombinationOptionDto1.getType().name()

        resCombinations.get(0).rightOption.content == rightCombinationOptionDto1.getContent()
        resCombinations.get(0).rightOption.type == rightCombinationOptionDto1.getType().name()

        cleanup:
        questionService.removeQuestion(response.data.id)
    }

    def "create question without login"() {
        given: "a question dto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())

        and: 'combinationOptionDtos'
        def leftCombinationOptionDto1 = new CombinationOptionDto()
        leftCombinationOptionDto1.setContent(LEFT_COMBINATION_OPTION_1_CONTENT)
        leftCombinationOptionDto1.setType(CombinationOption.Type.LEFT)
        def leftCombinationOptionDto2 = new CombinationOptionDto()
        leftCombinationOptionDto2.setContent(LEFT_COMBINATION_OPTION_2_CONTENT)
        leftCombinationOptionDto2.setType(CombinationOption.Type.LEFT)

        def rightCombinationOptionDto1 = new CombinationOptionDto()
        rightCombinationOptionDto1.setContent(RIGHT_COMBINATION_OPTION_1_CONTENT)
        rightCombinationOptionDto1.setType(CombinationOption.Type.RIGHT)
        def rightCombinationOptionDto2 = new CombinationOptionDto()
        rightCombinationOptionDto2.setContent(RIGHT_COMBINATION_OPTION_2_CONTENT)
        rightCombinationOptionDto2.setType(CombinationOption.Type.RIGHT)

        def options = new ArrayList<CombinationOptionDto>()
        options.add(leftCombinationOptionDto1)
        options.add(leftCombinationOptionDto2)
        options.add(rightCombinationOptionDto1)
        options.add(rightCombinationOptionDto2)

        def combinations = new ArrayList<CombinationDto>()

        def combination = new CombinationDto()
        combination.setLeftOption(leftCombinationOptionDto1)
        combination.setRightOption(rightCombinationOptionDto1)
        combinations.add(combination)

        questionDto.getQuestionDetailsDto().setOptions(options)
        questionDto.getQuestionDetailsDto().setCombinations(combinations)

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
        courseExecutionRepository.deleteById(courseExecution.getId())
        courseRepository.deleteById(course.getId())
    }

}
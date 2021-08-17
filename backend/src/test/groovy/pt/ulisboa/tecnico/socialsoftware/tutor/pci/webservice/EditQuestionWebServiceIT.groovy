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
class EditQuestionWebServiceIT extends SpockTest{

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


    def "edit item combination question"() {
        given: "logged in teacher"
        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        and: "a changed question"
        def questionDto = new QuestionDto(question)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())

        and: '1 new option set and 1 combination'
        def options = new ArrayList<CombinationOptionDto>()
        def combinations = new ArrayList<CombinationDto>()
        def combinationDto = new CombinationDto()

        def optionDto = new CombinationOptionDto(leftCombinationOption1)
        options.add(optionDto)
        combinationDto.setLeftOption(optionDto)

        optionDto = new CombinationOptionDto(leftCombinationOption2)
        options.add(optionDto)

        optionDto = new CombinationOptionDto(rightCombinationOption1)
        options.add(optionDto)
        combinationDto.setRightOption(optionDto)

        optionDto = new CombinationOptionDto(rightCombinationOption2)
        options.add(optionDto)

        optionDto = new CombinationOptionDto()
        optionDto.setType(CombinationOption.Type.RIGHT)
        optionDto.setContent(RIGHT_COMBINATION_OPTION_3_CONTENT)
        options.add(optionDto)

        combinations.add(combinationDto)

        questionDto.getQuestionDetailsDto().setOptions(options)
        questionDto.getQuestionDetailsDto().setCombinations(combinations)

        when:
        def ow = new ObjectMapper().writer().withDefaultPrettyPrinter()
        def response = restClient.put(
                path: '/questions/' + question.getId(),
                body: ow.writeValueAsString(questionDto),
                requestContentType: 'application/json'
        )

        then:
        questionRepository.count() == 1L
        combinationOptionRepository.count() == 5L
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

        resOptions.size() == 5L

        resOptions.get(0).content == LEFT_COMBINATION_OPTION_1_CONTENT
        resOptions.get(0).type == CombinationOption.Type.LEFT.name()
        resOptions.get(1).content == LEFT_COMBINATION_OPTION_2_CONTENT
        resOptions.get(1).type == CombinationOption.Type.LEFT.name()
        resOptions.get(2).content == RIGHT_COMBINATION_OPTION_1_CONTENT
        resOptions.get(2).type == CombinationOption.Type.RIGHT.name()
        resOptions.get(3).content == RIGHT_COMBINATION_OPTION_2_CONTENT
        resOptions.get(3).type == CombinationOption.Type.RIGHT.name()
        resOptions.get(4).content == RIGHT_COMBINATION_OPTION_3_CONTENT
        resOptions.get(4).type == CombinationOption.Type.RIGHT.name()

        def resCombinations = response.data.questionDetailsDto.combinations
        resCombinations.size() == 1L

        resCombinations.get(0).leftOption.content == leftCombinationOption1.getContent()
        resCombinations.get(0).leftOption.type == leftCombinationOption1.getType().name()

        resCombinations.get(0).rightOption.content == rightCombinationOption1.getContent()
        resCombinations.get(0).rightOption.type == rightCombinationOption1.getType().name()
    }

    def "edit item combination question as a student"() {
        given: "logged in student"
        createdUserLogin(USER_2_EMAIL, USER_2_PASSWORD)

        and: 'a changed question'
        def questionDto = new QuestionDto(question)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())

        and: '1 new option set and 1 combination'
        def options = new ArrayList<CombinationOptionDto>()
        def combinations = new ArrayList<CombinationDto>()
        def combinationDto = new CombinationDto()

        def optionDto = new CombinationOptionDto(leftCombinationOption1)
        options.add(optionDto)
        combinationDto.setLeftOption(optionDto)

        optionDto = new CombinationOptionDto(leftCombinationOption2)
        options.add(optionDto)

        optionDto = new CombinationOptionDto(rightCombinationOption1)
        options.add(optionDto)
        combinationDto.setRightOption(optionDto)

        optionDto = new CombinationOptionDto(rightCombinationOption2)
        options.add(optionDto)

        optionDto = new CombinationOptionDto()
        optionDto.setType(CombinationOption.Type.RIGHT)
        optionDto.setContent(RIGHT_COMBINATION_OPTION_3_CONTENT)
        options.add(optionDto)

        combinations.add(combinationDto)

        questionDto.getQuestionDetailsDto().setOptions(options)
        questionDto.getQuestionDetailsDto().setCombinations(combinations)

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

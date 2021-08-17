package pt.ulisboa.tecnico.socialsoftware.tutor.pci.webservice

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.json.JsonOutput
import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.ItemCombinationAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.ItemCombinationStatementAnswerDetailsCombinationDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.ItemCombinationStatementAnswerDetailsDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementQuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Combination
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CombinationOption
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TeacherExportsQuizWebServiceIT extends SpockTest {

    @LocalServerPort
    private int port

    def course
    def courseExecution
    def student1
    def student2
    def question
    def questionDetails
    def quizQuestion
    def leftCombinationOption
    def rightCombinationOption
    def combination
    def quizAnswer
    def date
    def quiz

    def setup() {
        /*given: "1 client, 1 course and 3 user (teacher role and 2 students)"
        restClient = new RESTClient("http://localhost:" + port)

        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)

        student1 = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL,
                User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        student1.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        student1.addCourse(courseExecution)
        courseExecution.addUser(student1)
        userRepository.save(student1)

        student2 = new User(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL,
                User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        student2.authUser.setPassword(passwordEncoder.encode(USER_2_PASSWORD))
        student2.addCourse(courseExecution)
        courseExecution.addUser(student2)
        userRepository.save(student2)

        and: "created question"
        question = new Question()
        question.setCourse(course)
        question.setKey(1)
        question.setTitle(QUESTION_1_TITLE)
        question.setContent(QUESTION_1_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        questionDetails = new ItemCombinationQuestion()
        question.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        questionRepository.save(question)

        and: '2 item combination options (left and right)'
        leftCombinationOption = new CombinationOption()
        leftCombinationOption.setContent(LEFT_COMBINATION_OPTION_1_CONTENT)
        leftCombinationOption.setType(CombinationOption.Type.LEFT)
        leftCombinationOption.setQuestionDetails(questionDetails)

        rightCombinationOption = new CombinationOption()
        rightCombinationOption.setContent(RIGHT_COMBINATION_OPTION_1_CONTENT)
        rightCombinationOption.setType(CombinationOption.Type.RIGHT)
        rightCombinationOption.setQuestionDetails(questionDetails)


        and: '1 combine 2 item combination questions'
        combination = new Combination(leftCombinationOption, rightCombinationOption)
        combination.setQuestionDetails(questionDetails)
        combinationRepository.save(combination)

        date = DateHandler.now()

        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle("Quiz Title")
        quiz.setType(Quiz.QuizType.PROPOSED.toString())
        quiz.setCourseExecution(courseExecution)
        quiz.setAvailableDate(DateHandler.now())
        quizRepository.save(quiz)

        quizQuestion = new QuizQuestion(quiz, question, 0)
        quizQuestionRepository.save(quizQuestion)

        quizAnswer = new QuizAnswer(student, quiz)
        quizAnswerRepository.save(quizAnswer)*/
    }

    def "student answer item combination question inside quiz and see result"() {
        /*given: 'a logged in student'
        createdUserLogin(USER_1_USERNAME, USER_1_PASSWORD)

        def statementQuizDto = new StatementQuizDto()
        statementQuizDto.id = quiz.getId()
        statementQuizDto.quizAnswerId = quizAnswer.getId()
        def statementAnswerDto = new StatementAnswerDto()
        def itemCombinationStatementAnswerDetailsDto = new ItemCombinationStatementAnswerDetailsDto()
        def statementCombination = new ItemCombinationStatementAnswerDetailsCombinationDto(leftCombinationOption.getId(),
                rightCombinationOption.getId())
        def statementCombinations = new ArrayList<ItemCombinationStatementAnswerDetailsCombinationDto>()
        statementCombinations.add(statementCombination)

        itemCombinationStatementAnswerDetailsDto.setCombinations(statementCombinations)

        statementAnswerDto.setAnswerDetails(itemCombinationStatementAnswerDetailsDto)
        statementAnswerDto.setSequence(0)
        statementAnswerDto.setTimeTaken(100)
        statementAnswerDto.setQuestionAnswerId(quizAnswer.getQuestionAnswers().get(0).getId())

        statementQuizDto.getAnswers().add(statementAnswerDto)


        when:
        def map = new ObjectMapper().writer().withDefaultPrettyPrinter()
        def response = restClient.get (
                path: '/quizzes/' + quiz.getId() + '/conclude',
                body: map.writeValueAsString(statementQuizDto),
                requestContentType: 'application/json'
        )

        then: 'the value is createQuestion and persistent'
        questionAnswerRepository.findAll().size() == 1
        def quizAnswerResult = quizAnswerRepository.findAll().get(0)
        quizAnswerResult.isCompleted()
        def questionAnswerResult = questionAnswerRepository.findAll().get(0)
        questionAnswerResult.getQuizAnswer().getId() == quizAnswerResult.getId()
        quizQuestionRepository.findAll().size() == 1
        def quizQuestionResult = quizQuestionRepository.findAll().get(0)
        questionAnswerResult.getQuizQuestion().getId() == quizQuestionResult.getId()
        ((ItemCombinationAnswer) questionAnswerResult.getAnswerDetails()).isCorrect()
         */
        /*and: 'the return value is OK'
        correctAnswers.size() == 1
        def correctAnswerDto = correctAnswers.get(0)
        correctAnswerDto.getSequence() == 0
        leftCombinationOption.getId() == correctAnswerDto.getCorrectAnswerDetails()
                .getCorrectCombinations().get(0).getLeftCombinationId()
        rightCombinationOption.getId() == correctAnswerDto.getCorrectAnswerDetails()
                .getCorrectCombinations().get(0).getRightCombinationId()*/
    }

    def cleanup() {
        /*combinationRepository.deleteById(combination.getId())
        quizAnswerRepository.deleteById(quizAnswer.getId())
        quizService.removeQuiz(quiz.getId())
        questionService.removeQuestion(question.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())
        courseRepository.deleteById(course.getId())
        userRepository.deleteById(student.getId())*/
    }
}
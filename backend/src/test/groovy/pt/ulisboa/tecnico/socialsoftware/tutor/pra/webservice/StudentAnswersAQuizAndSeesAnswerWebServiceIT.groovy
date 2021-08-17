package pt.ulisboa.tecnico.socialsoftware.tutor.pra.webservice

import org.apache.http.HttpStatus
import groovyx.net.http.HttpResponseException
import groovy.json.JsonOutput
import groovyx.net.http.RESTClient
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OpenAnswerQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenAnswerQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.dto.QuestionSubmissionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.OpenAnswerAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.OpenAnswerStatementAnswerDetailsDto


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentAnswersAQuizAndSeesAnswerWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def quiz
    def quizAnswer
    def course
    def courseExecution
    def student
    def quizQuestion
    def statementQuizDto

    def setup() {
        restClient = new RESTClient("http://localhost:" + port)

        given: "a course"
        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)

        and: "a student"
        student = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL,
                User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        student.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        student.addCourse(courseExecution)
        courseExecution.addUser(student)
        userRepository.save(student)

        and: "a quiz"
        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle(QUIZ_TITLE)
        quiz.setType(Quiz.QuizType.PROPOSED.toString())
        quiz.setAvailableDate(DateHandler.now())
        quiz.setCourseExecution(courseExecution)
        quizRepository.save(quiz)

        and: "an open answer question"
        def question = new Question()
        question.setKey(1)
        question.setTitle(QUESTION_1_TITLE)
        question.setContent(QUESTION_1_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        question.setCourse(course)
        def questionDetails = new OpenAnswerQuestion()
        questionDetails.setAnswer(QUESTION_1_ANSWER)
        question.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        questionRepository.save(question)

        and: "a quiz question"
        quizQuestion = new QuizQuestion(quiz, question, 0)
        quizQuestionRepository.save(quizQuestion)

        and: "a quiz answer"
        quizAnswer = new QuizAnswer(student, quiz)
        quizAnswerRepository.save(quizAnswer)
    }

    def "Start a quiz, submit an answer and check result"() {
        given: 'a logged in student'
        createdUserLogin(USER_1_USERNAME, USER_1_PASSWORD)

        statementQuizDto = new StatementQuizDto()
        statementQuizDto.id = quiz.getId()
        statementQuizDto.quizAnswerId = quizAnswer.getId()
        def statementAnswerDto = new StatementAnswerDto()
        def openAnswerStatementAnswerDetailsDto = new OpenAnswerStatementAnswerDetailsDto()

        openAnswerStatementAnswerDetailsDto.setAnswer(QUESTION_2_ANSWER)

        statementAnswerDto.setAnswerDetails(openAnswerStatementAnswerDetailsDto)
        statementAnswerDto.setSequence(0)
        statementAnswerDto.setTimeTaken(100)
        statementAnswerDto.setQuestionAnswerId(quizAnswer.getQuestionAnswers().get(0).getId())

        statementQuizDto.getAnswers().add(statementAnswerDto)
        
        when:
        def ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        def response = restClient.post(
            path: '/quizzes/' + quiz.getId() + '/conclude',
            body: ow.writeValueAsString(statementQuizDto),
            requestContentType: 'application/json'
        )
        
        def response1 = restClient.get(
            path: '/executions/' + courseExecution.getId() + '/quizzes/solved',
            requestContentType: 'application/json'
        )
        
        then: 'check response status'
        response1.data != null
        response1.status == 200
        and:
        def data = response1.data.get(0)
        data.statementQuiz.answers.answerDetails.answer.get(0) == QUESTION_2_ANSWER
        data.statementQuiz.answers.answerDetails.type.get(0) == "open_answer"
        data.correctAnswers.correctAnswerDetails.correctAnswer.get(0) == QUESTION_1_ANSWER
    }


    def cleanup() {
        userRepository.deleteById(student.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())
        courseRepository.deleteById(course.getId())
    }
}
package pt.ulisboa.tecnico.socialsoftware.tutor.pra.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.OpenAnswerAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.OpenAnswerAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.OpenAnswerStatementAnswerDetailsDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementQuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenAnswerQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler

@DataJpaTest
class TeacherSeesQuizResultsTest extends SpockTest {

    def student1
    def student2
    def quizQuestion
    def question
    def quizAnswerStudent1
    def quizAnswerStudent2
    def date
    def quiz

    def setup() {
        createExternalCourseAndExecution()

        student1 = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        student1.addCourse(externalCourseExecution)
        userRepository.save(student1)

        student2 = new User(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        student2.addCourse(externalCourseExecution)
        userRepository.save(student2)

        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle("Quiz Title")
        quiz.setType(Quiz.QuizType.PROPOSED.toString())
        quiz.setCourseExecution(externalCourseExecution)
        quiz.setAvailableDate(DateHandler.now())
        quizRepository.save(quiz)

        def question = new Question()
        question.setKey(1)
        question.setTitle("Question Title")
        question.setCourse(externalCourse)
        def questionDetails = new OpenAnswerQuestion()
        questionDetails.setAnswer(QUESTION_1_ANSWER)
        question.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        questionRepository.save(question)

        quizQuestion = new QuizQuestion(quiz, question, 0)
        quizQuestionRepository.save(quizQuestion)


        date = DateHandler.now()

        quizAnswerStudent1 = new QuizAnswer(student1, quiz)
        quizAnswerRepository.save(quizAnswerStudent1)

        quizAnswerStudent2 = new QuizAnswer(student2, quiz)
        quizAnswerRepository.save(quizAnswerStudent2)
    }

    def 'get quiz answers 2 students'() {
        given: 'a quiz with future conclusionDate'
        quiz.setConclusionDate(DateHandler.now().plusDays(2))
        and: 'an answer for student 1'
        def statementQuizDtoStudent1 = new StatementQuizDto()
        statementQuizDtoStudent1.id = quiz.getId()
        statementQuizDtoStudent1.quizAnswerId = quizAnswerStudent1.getId()
        def statementAnswerDtoStudent1 = new StatementAnswerDto()
        def openAnswerAnswerDtoStudent1 = new OpenAnswerStatementAnswerDetailsDto()
        openAnswerAnswerDtoStudent1.setAnswer(QUESTION_1_ANSWER)
        statementAnswerDtoStudent1.setAnswerDetails(openAnswerAnswerDtoStudent1)
        statementAnswerDtoStudent1.setSequence(0)
        statementAnswerDtoStudent1.setTimeTaken(100)
        statementAnswerDtoStudent1.setQuestionAnswerId(quizAnswerStudent1.getQuestionAnswers().get(0).getId())
        statementQuizDtoStudent1.getAnswers().add(statementAnswerDtoStudent1)
        and: 'an answer for student 2'
        def statementQuizDtoStudent2 = new StatementQuizDto()
        statementQuizDtoStudent2.id = quiz.getId()
        statementQuizDtoStudent2.quizAnswerId = quizAnswerStudent2.getId()
        def statementAnswerDtoStudent2 = new StatementAnswerDto()
        def openAnswerAnswerDtoStudent2 = new OpenAnswerStatementAnswerDetailsDto()
        openAnswerAnswerDtoStudent2.setAnswer(QUESTION_1_ANSWER)
        statementAnswerDtoStudent2.setAnswerDetails(openAnswerAnswerDtoStudent2)
        statementAnswerDtoStudent2.setSequence(0)
        statementAnswerDtoStudent2.setTimeTaken(100)
        statementAnswerDtoStudent2.setQuestionAnswerId(quizAnswerStudent2.getQuestionAnswers().get(0).getId())
        statementQuizDtoStudent2.getAnswers().add(statementAnswerDtoStudent2)
        when:
        answerService.concludeQuiz(statementQuizDtoStudent1)
        answerService.concludeQuiz(statementQuizDtoStudent2)
        def answers = quizService.getQuizAnswers(quiz.getId())
        then: 'teacher can see quiz results'
        def answersList = answers.getQuizAnswers()
        answersList.size() == 2
        def quizAnswer1 = answersList.stream().filter(ans -> ans.id == 1).findAny().orElse(null)
        quizAnswer1.name == USER_1_NAME
        quizAnswer1.username == USER_1_USERNAME
        quizAnswer1.questionAnswers.size() == 1
        def answer1 = quizAnswer1.questionAnswers.get(0)
     
        def answer3 = ((OpenAnswerAnswerDto) answer1.getAnswerDetails())
        answer3.getAnswer() == QUESTION_1_ANSWER

        def quizAnswer2 = answersList.stream().filter(ans -> ans.id == 2).findAny().orElse(null)
        quizAnswer2.name == USER_2_NAME
        quizAnswer2.username == USER_2_USERNAME
        quizAnswer2.questionAnswers.size() == 1
        def answer2 = quizAnswer2.questionAnswers.get(0)

        def answer4 = ((OpenAnswerAnswerDto) answer2.getAnswerDetails())
        answer3.getAnswer() == QUESTION_1_ANSWER

    }
    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
} 
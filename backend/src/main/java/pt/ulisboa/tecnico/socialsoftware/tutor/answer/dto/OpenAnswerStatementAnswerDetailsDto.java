package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.AnswerDetails;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.OpenAnswerAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenAnswerQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.OpenAnswerAnswerItem;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswerItem;

import javax.persistence.Transient;

public class OpenAnswerStatementAnswerDetailsDto extends StatementAnswerDetailsDto {
    private String answer;

    public OpenAnswerStatementAnswerDetailsDto() {
    }

    public OpenAnswerStatementAnswerDetailsDto(OpenAnswerAnswer questionAnswer) {
        if (questionAnswer.getAnswer() != null) {
            this.answer = questionAnswer.getAnswer();
        }
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Transient
    private OpenAnswerAnswer createdOpenAnswerAnswer;

    @Override
    public AnswerDetails getAnswerDetails(QuestionAnswer questionAnswer) {
        createdOpenAnswerAnswer = new OpenAnswerAnswer(questionAnswer);
        questionAnswer.getQuestion().getQuestionDetails().update(this);
        return createdOpenAnswerAnswer;
    }

    @Override
    public boolean emptyAnswer() {
        return answer == null;
    }

    @Override
    public QuestionAnswerItem getQuestionAnswerItem(String username, int quizId, StatementAnswerDto statementAnswerDto) {
        return new OpenAnswerAnswerItem(username, quizId, statementAnswerDto, this);
    }

    @Override
    public void update(OpenAnswerQuestion question) {
        createdOpenAnswerAnswer.setAnswer(question, this);
    }

    @Override
    public String toString() {
        return "OpenAnswerStatementAnswerDetailsDto{" +
                "answer=" + answer +
                '}';
    }
}

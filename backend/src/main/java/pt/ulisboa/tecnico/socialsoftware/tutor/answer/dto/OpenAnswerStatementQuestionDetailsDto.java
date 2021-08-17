package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenAnswerQuestion;

public class OpenAnswerStatementQuestionDetailsDto extends StatementQuestionDetailsDto {
    private String answer;

    public OpenAnswerStatementQuestionDetailsDto(OpenAnswerQuestion question) {
        this.answer = question.getAnswer();
    }

    public String getAnswer() {
        return this.answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "OpenAnswerStatementQuestionDetailsDto{" +
                "answer=" + this.answer +
                '}';
    }
}
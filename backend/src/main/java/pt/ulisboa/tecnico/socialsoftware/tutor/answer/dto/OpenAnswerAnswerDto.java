package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.OpenAnswerAnswer;

public class OpenAnswerAnswerDto extends AnswerDetailsDto {
    private String answer;

    public OpenAnswerAnswerDto() {
    }

    public OpenAnswerAnswerDto(OpenAnswerAnswer answer) {
        if (answer.getAnswer() != null)
            this.answer = answer.getAnswer();
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}

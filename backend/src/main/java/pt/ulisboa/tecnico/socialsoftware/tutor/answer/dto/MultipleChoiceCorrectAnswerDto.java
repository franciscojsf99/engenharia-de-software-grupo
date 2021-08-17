package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;

import java.util.HashSet;
import java.util.Set;

public class MultipleChoiceCorrectAnswerDto extends CorrectAnswerDetailsDto {
    private Set<Integer> correctOptionId = new HashSet<>();

    public MultipleChoiceCorrectAnswerDto(MultipleChoiceQuestion question) {
        this.correctOptionId = question.getCorrectOptionId();
    }

    public  Set<Integer> getCorrectOptionId() {
        return correctOptionId;
    }

    public void setCorrectOptionId(Set<Integer> correctOptionId) {
        this.correctOptionId = correctOptionId;
    }

    @Override
    public String toString() {
        return "MultipleChoiceCorrectAnswerDto{" +
                "correctOptionId=" + correctOptionId +
                '}';
    }
}
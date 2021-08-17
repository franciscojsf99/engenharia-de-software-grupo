package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.ItemCombinationAnswerCombination;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Combination;

import java.io.Serializable;

public class ItemCombinationAnswerCombinationDto implements Serializable {
    private Integer leftCombinationOptionId;
    private Integer rightCombinationOptionId;
    private boolean correct;

    public ItemCombinationAnswerCombinationDto(Combination correctCombination) {
        this.leftCombinationOptionId = correctCombination.getLeftOption().getId();
        this.rightCombinationOptionId = correctCombination.getRightOption().getId();
        this.correct = true;
    }

    public ItemCombinationAnswerCombinationDto(ItemCombinationAnswerCombination answerCombination) {
        this.leftCombinationOptionId = answerCombination.getLeftOption().getId();
        this.rightCombinationOptionId = answerCombination.getRightOption().getId();
        correct = answerCombination.isCorrect();
    }

    public Integer getLeftCombinationId() {
        return this.leftCombinationOptionId;
    }

    public Integer getRightCombinationId() {
        return this.rightCombinationOptionId;
    }

    public void setLeftCombinationId(Integer leftCombinationOptionId) {
        this.leftCombinationOptionId = leftCombinationOptionId;
    }

    public void setRightCombinationId(Integer rightCombinationOptionId) {
        this.rightCombinationOptionId = rightCombinationOptionId;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
}

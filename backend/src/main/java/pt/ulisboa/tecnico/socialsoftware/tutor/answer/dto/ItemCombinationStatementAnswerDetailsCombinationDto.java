package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.ItemCombinationAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.ItemCombinationAnswerCombination;

import java.io.Serializable;

public class ItemCombinationStatementAnswerDetailsCombinationDto implements Serializable {
    private Integer leftOptionId;
    private Integer rightOptionId;

    public ItemCombinationStatementAnswerDetailsCombinationDto() {
    }

    public ItemCombinationStatementAnswerDetailsCombinationDto(Integer leftOptionId, Integer rightOptionId) {
        this.leftOptionId = leftOptionId;
        this.rightOptionId = rightOptionId;
    }

    public ItemCombinationStatementAnswerDetailsCombinationDto(ItemCombinationAnswerCombination combination) {
        this.leftOptionId = combination.getLeftOption().getId();
        this.rightOptionId = combination.getRightOption().getId();
    }

    public Integer getLeftOptionId() {
        return this.leftOptionId;
    }

    public void setLeftOptionId(Integer leftOptionId) {
        this.leftOptionId = leftOptionId;
    }

    public Integer getRightOptionId() {
        return this.rightOptionId;
    }

    public void setRightOptionId(Integer rightOptionId) {
        this.rightOptionId = rightOptionId;
    }
}
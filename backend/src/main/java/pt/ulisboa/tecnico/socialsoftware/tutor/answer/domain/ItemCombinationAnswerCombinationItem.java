package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.ItemCombinationStatementAnswerDetailsCombinationDto;

import javax.persistence.Embeddable;

@Embeddable
public class ItemCombinationAnswerCombinationItem {
    private Integer leftOptionId;
    private Integer rightOptionId;

    public ItemCombinationAnswerCombinationItem() {}

    public ItemCombinationAnswerCombinationItem(ItemCombinationStatementAnswerDetailsCombinationDto itemCombinationStatementAnswerDetailsCombinationDto) {
        this.leftOptionId = itemCombinationStatementAnswerDetailsCombinationDto.getLeftOptionId();
        this.rightOptionId = itemCombinationStatementAnswerDetailsCombinationDto.getRightOptionId();
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

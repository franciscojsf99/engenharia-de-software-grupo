package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CombinationOption;

import java.io.Serializable;

public class StatementCombinationOptionDto implements Serializable {

    private Integer combinationOptionId;
    private String content;
    private CombinationOption.Type type;


    public StatementCombinationOptionDto(CombinationOption option) {
        this.combinationOptionId = option.getId();
        this.content = option.getContent();
        this.type = option.getType();
    }

    public Integer getCombinationOptionId() {
        return combinationOptionId;
    }

    public void setCombinationOptionId(Integer combinationOptionId) {
        this.combinationOptionId = combinationOptionId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public CombinationOption.Type getType() {
        return this.type;
    }

    public void setContent(CombinationOption.Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "StatementCombinationOptionDto{" +
                "combinationOptionId=" + combinationOptionId +
                ", content='" + content + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}

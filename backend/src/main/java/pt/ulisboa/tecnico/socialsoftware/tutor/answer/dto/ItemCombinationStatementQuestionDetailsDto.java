package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CombinationOption;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationQuestion;

import java.util.List;
import java.util.stream.Collectors;

public class ItemCombinationStatementQuestionDetailsDto extends StatementQuestionDetailsDto {
    private List<StatementCombinationOptionDto> leftOptions;
    private List<StatementCombinationOptionDto> rightOptions;

    public ItemCombinationStatementQuestionDetailsDto(ItemCombinationQuestion question) {
        this.leftOptions = question.getOptions().stream()
                .filter((combinationOption -> combinationOption.getType().equals(CombinationOption.Type.LEFT)))
                .map(StatementCombinationOptionDto::new)
                .collect(Collectors.toList());
        this.rightOptions = question.getOptions().stream()
                .filter((combinationOption -> combinationOption.getType().equals(CombinationOption.Type.RIGHT)))
                .map(StatementCombinationOptionDto::new)
                .collect(Collectors.toList());
    }

    public List<StatementCombinationOptionDto> getLeftOptions() {
        return this.leftOptions;
    }

    public void setLeftOptions(List<StatementCombinationOptionDto> leftOptions) {
        this.leftOptions = leftOptions;
    }

    public List<StatementCombinationOptionDto> getRightOptions() {
        return this.rightOptions;
    }

    public void setRightOptions(List<StatementCombinationOptionDto> rightOptions) {
        this.rightOptions = rightOptions;
    }

    @Override
    public String toString() {
        return "ItemCombinationStatementQuestionDetailsDto{" +
                "leftOptions=" + this.leftOptions +
                "rightOptions=" + this.rightOptions +
                '}';
    }
}

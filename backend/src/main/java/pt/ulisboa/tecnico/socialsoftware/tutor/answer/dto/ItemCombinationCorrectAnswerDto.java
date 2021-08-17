package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationQuestion;

import java.util.List;
import java.util.stream.Collectors;

public class ItemCombinationCorrectAnswerDto extends CorrectAnswerDetailsDto {
    private List<ItemCombinationAnswerCombinationDto> correctCombinations;

    public ItemCombinationCorrectAnswerDto(ItemCombinationQuestion question) {
        this.correctCombinations = question.getCombinations()
                .stream()
                .map(ItemCombinationAnswerCombinationDto::new)
                .collect(Collectors.toList());
    }

    public List<ItemCombinationAnswerCombinationDto> getCorrectCombinations() {
        return this.correctCombinations;
    }

    public void setCorrectCombinations(List<ItemCombinationAnswerCombinationDto> correctCombinations) {
        this.correctCombinations = correctCombinations;
    }
}
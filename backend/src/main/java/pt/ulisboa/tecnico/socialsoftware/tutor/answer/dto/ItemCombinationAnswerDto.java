package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.ItemCombinationAnswer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemCombinationAnswerDto extends AnswerDetailsDto {
    private List<ItemCombinationAnswerCombinationDto> combinations = new ArrayList<>();
    private boolean correct;

    public ItemCombinationAnswerDto() {}

    public ItemCombinationAnswerDto(ItemCombinationAnswer answer) {
        if (answer.getCombinations() != null) {
            this.combinations = answer.getCombinations().stream().map(ItemCombinationAnswerCombinationDto::new).collect(Collectors.toList());
        }
        this.correct = answer.isCorrect();
    }

    public List<ItemCombinationAnswerCombinationDto> getCombinations() {
        return this.combinations;
    }

    public void setCombinations(List<ItemCombinationAnswerCombinationDto> combinations) {
        this.combinations = combinations;
    }

    public boolean isCorrect() {
        return this.correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
}

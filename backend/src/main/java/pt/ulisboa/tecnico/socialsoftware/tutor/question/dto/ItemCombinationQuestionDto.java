package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Combination;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.QuestionDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemCombinationQuestionDto extends QuestionDetailsDto {

    private List<CombinationOptionDto> options = new ArrayList<>();

    private List<CombinationDto> combinations = new ArrayList<>();

    public ItemCombinationQuestionDto() {

    }

    public ItemCombinationQuestionDto(ItemCombinationQuestion question) {
        this.options = question.getOptions().stream().map(CombinationOptionDto::new).collect(Collectors.toList());
        this.combinations = question.getCombinations().stream().map(CombinationDto::new).collect(Collectors.toList());
    }

    public List<CombinationOptionDto> getOptions() {
        return options;
    }

    public List<CombinationDto> getCombinations() {
        return combinations;
    }

    public void setOptions(List<CombinationOptionDto> options) {
        this.options = options;
    }

    public void setCombinations(List<CombinationDto> combinations) {
        this.combinations = combinations;
    }

    @Override
    public void update(ItemCombinationQuestion question) {
        question.update(this);
    }

    @Override
    public QuestionDetails getQuestionDetails(Question question) {
        return new ItemCombinationQuestion(question, this);
    }
}

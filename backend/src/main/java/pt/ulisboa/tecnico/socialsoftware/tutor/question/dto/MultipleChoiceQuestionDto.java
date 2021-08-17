package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.QuestionDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class MultipleChoiceQuestionDto extends QuestionDetailsDto {
    private List<OptionDto> options = new ArrayList<>();

    public MultipleChoiceQuestionDto() {

    }

    public MultipleChoiceQuestionDto(MultipleChoiceQuestion question) {
        this.options = question.getOptions().stream().map(OptionDto::new).collect(Collectors.toList());
    }

    public List<OptionDto> getOptions() {
        return options;
    }
    public Integer getNumCorrectAnswers(){
        Integer res = 0;
        return (int) this.getOptions().stream().filter(OptionDto::isCorrect).count();

    }

    public void setOptions(List<OptionDto> options) {
        this.options = options;
    }

    @Override
    public QuestionDetails getQuestionDetails(Question question) {
        return new MultipleChoiceQuestion(question, this);
    }

    @Override
    public void update(MultipleChoiceQuestion question) {
        question.update(this);
    }

    @Override
    public String toString() {
        return "MultipleChoiceQuestionDto{" +
                "options=" + options +
                '}';
    }

}

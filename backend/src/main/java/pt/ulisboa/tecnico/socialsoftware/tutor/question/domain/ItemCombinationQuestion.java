package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.Updator;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.CombinationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.CombinationOptionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDetailsDto;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@DiscriminatorValue(Question.QuestionTypes.ITEM_COMBINATION_QUESTION)
public class ItemCombinationQuestion extends QuestionDetails {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "questionDetails", fetch = FetchType.LAZY, orphanRemoval = true)
    private final List<CombinationOption> combinationOptions = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "questionDetails", fetch = FetchType.LAZY, orphanRemoval = true)
    private final List<Combination> combinations = new ArrayList<>();

    public ItemCombinationQuestion() {
        super();
    }

    public ItemCombinationQuestion(Question question, ItemCombinationQuestionDto questionDto) {
        super(question);
        setOptions(questionDto.getOptions());
        setCombinations(questionDto.getCombinations());
    }

    public List<CombinationOption> getOptions() {
        return combinationOptions;
    }

    public List<Combination> getCombinations() {
        return combinations;
    }

    public void addOption(CombinationOption option) {
        combinationOptions.add(option);
    }

    public void addCombination(Combination combination) {
        combinations.add(combination);
    }

    public void setOptions(List<CombinationOptionDto> options) {
        List<CombinationOptionDto> leftOptions = options
                .stream()
                .filter(op -> op.getType() == CombinationOption.Type.LEFT)
                .collect(Collectors.toList());
        List<CombinationOptionDto> rightOptions = options
                .stream()
                .filter(op -> op.getType() == CombinationOption.Type.RIGHT)
                .collect(Collectors.toList());
        if (leftOptions.size() < 1 || rightOptions.size() < 1) {
            throw new TutorException(CANNOT_CREATE_ITEM_COMBINATION_QUESTION_WITH_EMPTY_COLUMN);
        }
        for (CombinationOptionDto optionDto : options) {
            if(options
                    .stream()
                    .filter(op -> op.getContent().equals(optionDto.getContent())).count() > 1) {
                throw new TutorException(DUPLICATE_ITEM_COMBINATION_OPTION);
            }
        }
        for(CombinationOption option: this.combinationOptions) {
            option.remove();
        }
        this.combinationOptions.clear();

        for(CombinationOptionDto optionDto: options) {
            new CombinationOption(optionDto).setQuestionDetails(this);
        }
    }

    public void setCombinations(List<CombinationDto> combinations) {
        if(combinations.size() < 1) {
            throw new TutorException(ZERO_COMBINATIONS);
        }

        for (CombinationDto combinationDto : combinations) {
            CombinationOptionDto leftOptionDto = combinationDto.getLeftOption();
            CombinationOptionDto rightOptionDto = combinationDto.getRightOption();

            if (leftOptionDto.getType() != CombinationOption.Type.LEFT
                    || rightOptionDto.getType() != CombinationOption.Type.RIGHT) {
                throw new TutorException(INVALID_COMBINATION);
            }
        }
        for(Combination combination: this.combinations) {
            combination.remove();
        }
        this.combinations.clear();

        for (CombinationDto combinationDto : combinations) {
            CombinationOptionDto leftOptionDto = combinationDto.getLeftOption();
            CombinationOptionDto rightOptionDto = combinationDto.getRightOption();

            CombinationOption leftOption = getOptions()
                    .stream()
                    .filter(op -> op.getContent().equals(leftOptionDto.getContent()) && op.getType() == leftOptionDto.getType())
                    .findAny()
                    .orElse(null);
            CombinationOption rightOption = getOptions()
                    .stream()
                    .filter(op -> op.getContent().equals(rightOptionDto.getContent()) && op.getType() == rightOptionDto.getType())
                    .findAny()
                    .orElse(null);
            new Combination(leftOption, rightOption).setQuestionDetails(this);
        }
    }

    public CombinationOption getCombinationOptionById(Integer id) {
        return this.combinationOptions
                .stream()
                .filter(combOpt -> combOpt.getId().equals(id))
                .findAny()
                .orElseThrow(() -> new TutorException(QUESTION_COMBINATION_OPTION_MISMATCH, id));
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitQuestionDetails(this);
    }

    public void visitOptions(Visitor visitor) {
        for (CombinationOption option : this.getOptions()) {
            option.accept(visitor);
        }
    }

    public void visitCombinations(Visitor visitor) {
        for (Combination combination : this.getCombinations()) {
            combination.accept(visitor);
        }
    }

    @Override
    public CorrectAnswerDetailsDto getCorrectAnswerDetailsDto() {
        return new ItemCombinationCorrectAnswerDto(this);
    }

    @Override
    public StatementQuestionDetailsDto getStatementQuestionDetailsDto() {
        return new ItemCombinationStatementQuestionDetailsDto(this);
    }

    @Override
    public StatementAnswerDetailsDto getEmptyStatementAnswerDetailsDto() {
        return new ItemCombinationStatementAnswerDetailsDto();
    }

    @Override
    public AnswerDetailsDto getEmptyAnswerDetailsDto() {
        return new ItemCombinationAnswerDto();
    }

    @Override
    public QuestionDetailsDto getQuestionDetailsDto() {
        return new ItemCombinationQuestionDto(this);
    }

    public void update(ItemCombinationQuestionDto questionDetails) {
        setOptions(questionDetails.getOptions());
        setCombinations(questionDetails.getCombinations());
    }

    @Override
    public void update(Updator updator) {
        updator.update(this);
    }

    @Override
    public void delete() {
        super.delete();
        for (CombinationOption combinationOption : this.combinationOptions) {
            combinationOption.remove();
        }
        this.combinationOptions.clear();
        for (Combination combination : this.combinations) {
            combination.remove();
        }
        this.combinations.clear();
    }

    @Override
    public String getCorrectAnswerRepresentation() {
        StringBuilder s = new StringBuilder();
        for(Combination combination: getCombinations()) {
            s.append(String.format("\t\"%s\" -> \"%s\"\n", combination.getLeftOption().getContent(), combination.getRightOption().getContent()));
        }
        return s.toString();
    }

    @Override
    public String getAnswerRepresentation(List<Integer> selectedIds) {
        StringBuilder s = new StringBuilder();
        for(int i = 0; i < selectedIds.size(); i = i + 2) {
            Integer leftCombinationOptionId = selectedIds.get(i);
            Integer rightCombinationOptionId = selectedIds.get(i + 1);
            String leftContent = this.combinationOptions
                    .stream()
                    .filter(combinationOption -> combinationOption.getId().equals(leftCombinationOptionId))
                    .findAny()
                    .get()
                    .getContent();
            String rightContent = this.combinationOptions
                    .stream()
                    .filter(combinationOption -> combinationOption.getId().equals(rightCombinationOptionId))
                    .findAny()
                    .get()
                    .getContent();
            s.append(String.format("\t\"%s\" -> \"%s\"\n", leftContent, rightContent));
        }
        return s.toString();
    }

}

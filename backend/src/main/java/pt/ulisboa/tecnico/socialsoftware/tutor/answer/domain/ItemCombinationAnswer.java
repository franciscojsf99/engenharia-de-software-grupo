package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@DiscriminatorValue(Question.QuestionTypes.ITEM_COMBINATION_QUESTION)
public class ItemCombinationAnswer extends AnswerDetails {
    @OneToMany(mappedBy = "itemCombinationAnswer", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ItemCombinationAnswerCombination> combinations = new ArrayList<>();

    @Column(columnDefinition = "boolean default false")
    private Boolean correct;

    public ItemCombinationAnswer() {
        super();
    }

    public ItemCombinationAnswer(QuestionAnswer questionAnswer) {
        super(questionAnswer);
        this.correct = false;
    }

    public void setCombinations(ItemCombinationQuestion question,
                                ItemCombinationStatementAnswerDetailsDto itemCombinationStatementAnswerDetailsDto) {
        this.combinations.clear();
        if (!itemCombinationStatementAnswerDetailsDto.emptyAnswer()) {
            for (var combination : itemCombinationStatementAnswerDetailsDto.getCombinations()) {

                CombinationOption leftCombinationOption = question
                        .getCombinationOptionById(combination.getLeftOptionId());
                CombinationOption rightCombinationOption = question
                        .getCombinationOptionById(combination.getRightOptionId());

                var answerCombination = new ItemCombinationAnswerCombination(this, leftCombinationOption, rightCombinationOption);
                this.combinations.add(answerCombination);
            }
            int combinationsSize = 0;
            for(Combination combination: question.getCombinations()) {
                for(ItemCombinationAnswerCombination answerCombination: this.combinations) {
                    if(answerCombination.getLeftOption().getId().equals(combination.getLeftOption().getId())
                        && answerCombination.getRightOption().getId().equals(combination.getRightOption().getId())) {
                        answerCombination.setCorrect(true);
                        combinationsSize++;
                        break;
                    }
                }
            }
            if(question.getCombinations().size() == combinationsSize && this.combinations.size() == combinationsSize) {
                this.correct = true;
            }
        }
        else {
            this.correct = false;
        }
    }

    public List<ItemCombinationAnswerCombination> getCombinations() {
        return this.combinations;
    }

    @Override
    public AnswerDetailsDto getAnswerDetailsDto() {
        return new ItemCombinationAnswerDto(this);
    }

    @Override
    public String getAnswerRepresentation() {
        return this.combinations.stream()
                .map((comb) -> comb.getLeftOption().getContent() + " -> " + comb.getRightOption().getContent())
                .collect(Collectors.joining("\n"));
    }

    @Override
    public StatementAnswerDetailsDto getStatementAnswerDetailsDto() {
        return new ItemCombinationStatementAnswerDetailsDto(this);
    }

    @Override
    public boolean isAnswered() {
        return !this.combinations.isEmpty();
    }

    @Override
    public boolean isCorrect() {
        if(this.correct == null) {
            return false;
        }
        return this.correct;
    }

    @Override
    public void remove() {
        this.combinations.forEach(ItemCombinationAnswerCombination::remove);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitAnswerDetails(this);
    }
}

package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationQuestion;

import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemCombinationStatementAnswerDetailsDto extends StatementAnswerDetailsDto {
    private List<ItemCombinationStatementAnswerDetailsCombinationDto> combinations = new ArrayList<>();

    public ItemCombinationStatementAnswerDetailsDto() {}

    public ItemCombinationStatementAnswerDetailsDto(ItemCombinationAnswer questionAnswer) {
        if (questionAnswer.getCombinations() != null) {
            this.combinations = questionAnswer.getCombinations()
                    .stream()
                    .map(ItemCombinationStatementAnswerDetailsCombinationDto::new)
                    .collect(Collectors.toList());
        }
    }

    public List<ItemCombinationStatementAnswerDetailsCombinationDto> getCombinations() {
        return this.combinations;
    }

    public void setCombinations(List<ItemCombinationStatementAnswerDetailsCombinationDto> combinations) {
        this.combinations = combinations;
    }

    @Transient
    private ItemCombinationAnswer itemCombinationAnswer;

    @Override
    public AnswerDetails getAnswerDetails(QuestionAnswer questionAnswer) {
        itemCombinationAnswer = new ItemCombinationAnswer(questionAnswer);
        questionAnswer.getQuestion().getQuestionDetails().update(this);
        return itemCombinationAnswer;
    }

    @Override
    public void update(ItemCombinationQuestion question) {
        this.itemCombinationAnswer.setCombinations(question, this);
    }

    @Override
    public boolean emptyAnswer() {
        return this.combinations == null || this.combinations.isEmpty();
    }

    @Override
    public QuestionAnswerItem getQuestionAnswerItem(String username, int quizId, StatementAnswerDto statementAnswerDto) {
        return new ItemCombinationAnswerItem(username, quizId, statementAnswerDto, this);
    }

    @Override
    public String toString() {
        return "CodeOrderStatementAnswerDetailsDto{" +
                "orderedSlots=" + combinations +
                '}';
    }
}

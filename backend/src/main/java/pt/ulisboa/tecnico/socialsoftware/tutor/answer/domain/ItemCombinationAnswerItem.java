package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.ItemCombinationStatementAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.QuestionDetails;

import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@DiscriminatorValue(Question.QuestionTypes.ITEM_COMBINATION_QUESTION)
public class ItemCombinationAnswerItem extends QuestionAnswerItem {

    @ElementCollection
    private List<ItemCombinationAnswerCombinationItem> combinations;

    public ItemCombinationAnswerItem() {
    }

    public ItemCombinationAnswerItem(String username, int quizId, StatementAnswerDto answer, ItemCombinationStatementAnswerDetailsDto detailsDto) {
        super(username, quizId, answer);
        this.combinations = detailsDto.getCombinations()
                .stream()
                .map(ItemCombinationAnswerCombinationItem::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getAnswerRepresentation(QuestionDetails questionDetails) {
        List<Integer> optionIds = new ArrayList<>();
        for(ItemCombinationAnswerCombinationItem combination : this.combinations) {
            optionIds.add(combination.getLeftOptionId());
            optionIds.add(combination.getRightOptionId());
        }
        return questionDetails.getAnswerRepresentation(optionIds);
    }
}

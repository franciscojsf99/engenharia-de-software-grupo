package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.CombinationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.CombinationOptionDto;

import javax.persistence.*;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(name = "combinations")
public class Combination implements DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "left_combination_option_id")
    private CombinationOption leftOption;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "right_combination_option_id")
    private CombinationOption rightOption;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_details_id")
    private ItemCombinationQuestion questionDetails;

    public Combination() {

    }

    public Combination(CombinationOption leftOption, CombinationOption rightOption) {
        setLeftOption(leftOption);
        setRightOption(rightOption);
    }

    public Integer getId() {
        return id;
    }

    public CombinationOption getLeftOption() {
        return leftOption;
    }

    public void setLeftOption(CombinationOption leftOption) {
        if(leftOption == null) {
            throw new TutorException(INVALID_NULL_COMBINATION_OPTION);
        }
        this.leftOption = leftOption;
    }

    public CombinationOption getRightOption() {
        return rightOption;
    }

    public void setRightOption(CombinationOption rightOption) {
        if(rightOption == null) {
            throw new TutorException(INVALID_NULL_COMBINATION_OPTION);
        }
        this.rightOption = rightOption;
    }

    public ItemCombinationQuestion getQuestionDetails() {
        return questionDetails;
    }

    public void setQuestionDetails(ItemCombinationQuestion question) {
        this.questionDetails = question;
        question.addCombination(this);
    }

    public void visitCombinationOptions(Visitor visitor) {
        leftOption.accept(visitor);
        rightOption.accept(visitor);
    }

    public void remove() {
        this.leftOption = null;
        this.rightOption = null;
        this.questionDetails = null;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitCombination(this);
    }
}

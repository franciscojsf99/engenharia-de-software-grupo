package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CombinationOption;

import javax.persistence.*;

@Entity
public class ItemCombinationAnswerCombination {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private ItemCombinationAnswer itemCombinationAnswer;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "left_combination_option_id")
    private CombinationOption leftOption;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "right_combination_option_id")
    private CombinationOption rightOption;

    private boolean correct;

    public ItemCombinationAnswerCombination() {
    }

    public ItemCombinationAnswerCombination(ItemCombinationAnswer itemCombinationAnswer,
                                                CombinationOption leftOption,
                                                CombinationOption rightOption) {
        setItemCombinationAnswer(itemCombinationAnswer);
        setLeftOption(leftOption);
        setRightOption(rightOption);
        this.correct = false;
    }

    public void setItemCombinationAnswer(ItemCombinationAnswer itemCombinationAnswer) {
        this.itemCombinationAnswer = itemCombinationAnswer;
    }

    public void setLeftOption(CombinationOption leftOption) {
        this.leftOption = leftOption;
    }

    public void setRightOption(CombinationOption rightOption) {
        this.rightOption = rightOption;
    }

    public ItemCombinationAnswer getItemCombinationAnswer() {
        return itemCombinationAnswer;
    }

    public CombinationOption getLeftOption() {
        return leftOption;
    }

    public CombinationOption getRightOption() {
        return rightOption;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void remove() {
        this.itemCombinationAnswer.getCombinations().remove(this);
        this.itemCombinationAnswer = null;
    }

    public boolean isCorrect() {
        return this.correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
}

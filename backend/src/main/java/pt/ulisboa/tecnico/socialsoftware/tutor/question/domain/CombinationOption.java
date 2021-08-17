package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.CombinationOptionDto;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(name = "combination_options")
public class CombinationOption implements DomainEntity {

    public enum Type { RIGHT, LEFT }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    private Type type;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_details_id")
    private ItemCombinationQuestion questionDetails;

    public CombinationOption()  { }

    public CombinationOption(CombinationOptionDto option) {
        setContent(option.getContent());
        setType(option.getType());
    }

    public Integer getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        if (content == null || content.isBlank()) {
            throw new TutorException(INVALID_CONTENT_FOR_COMBINATION_OPTION);
        }
        this.content = content;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public ItemCombinationQuestion getQuestionDetails() {
        return questionDetails;
    }

    public void setQuestionDetails(ItemCombinationQuestion question) {
        this.questionDetails = question;
        question.addOption(this);
    }

    public void remove() {
        this.questionDetails = null;
    }


    @Override
    public void accept(Visitor visitor) {
        visitor.visitCombinationOption(this);
    }
}

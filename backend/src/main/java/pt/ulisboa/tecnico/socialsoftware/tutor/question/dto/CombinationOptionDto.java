package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CombinationOption;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;

public class CombinationOptionDto {

    private Integer id;
    private String content;
    private CombinationOption.Type type;

    public CombinationOptionDto() {

    }

    public CombinationOptionDto(CombinationOption option) {
        this.id = option.getId();
        this.content = option.getContent();
        this.type = option.getType();
    }

    public Integer getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public CombinationOption.Type getType() {
        return type;
    }

    public void setType(CombinationOption.Type type) {
        this.type = type;
    }

    public void setContent(String content) {
        this.content = content;
    }

}

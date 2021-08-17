package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Combination;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CombinationOption;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CombinationDto {

    private Integer id;

    private CombinationOptionDto leftOption;

    private CombinationOptionDto rightOption;

    public CombinationDto() { }

    public CombinationDto(Combination combination) {
        this.id = combination.getId();
        this.leftOption = new CombinationOptionDto(combination.getLeftOption());
        this.rightOption = new CombinationOptionDto(combination.getRightOption());
    }

    public Integer getId() {
        return id;
    }

    public CombinationOptionDto getLeftOption() {
        return leftOption;
    }

    public void setLeftOption(CombinationOptionDto leftOption) {
        this.leftOption = leftOption;
    }

    public CombinationOptionDto getRightOption() {
        return rightOption;
    }

    public void setRightOption(CombinationOptionDto rightOption) {
        this.rightOption = rightOption;
    }
}

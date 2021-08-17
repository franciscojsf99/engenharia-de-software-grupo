package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.Updator;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OpenAnswerQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDetailsDto;

import java.util.List;
import javax.persistence.*;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@DiscriminatorValue(Question.QuestionTypes.OPEN_ANSWER_QUESTION)
public class OpenAnswerQuestion extends QuestionDetails {

    @Column(columnDefinition = "TEXT")
    private String answer;

    public OpenAnswerQuestion() {
        super();
    }

    public OpenAnswerQuestion(Question question, OpenAnswerQuestionDto questionDto) {
        super(question);
        setAnswer(questionDto.getAnswer());
    }

    public String getAnswer() {
        return this.answer;
    }

    public void setAnswer(String answer) {
        if(answer == null || answer.isBlank()){
            throw new TutorException(INVALID_CONTENT_FOR_ANSWER);
        }
        this.answer = answer;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitQuestionDetails(this);
    }

    public void update(OpenAnswerQuestionDto questionDto) {
        setAnswer(questionDto.getAnswer());
    }

    @Override
    public void update(Updator updator) {
        updator.update(this);
    }

    @Override
    public String getAnswerRepresentation(List<Integer> selectedIds) {
        return this.answer;
    }

    @Override
    public String getCorrectAnswerRepresentation() {
        return this.answer; 
    }

    @Override
    public CorrectAnswerDetailsDto getCorrectAnswerDetailsDto() {
        return new OpenAnswerCorrectAnswerDto(this);
    }

    @Override
    public QuestionDetailsDto getQuestionDetailsDto() {
        return new OpenAnswerQuestionDto(this);
    }

    @Override
    public AnswerDetailsDto getEmptyAnswerDetailsDto() {
        return new OpenAnswerAnswerDto();
    }

    @Override
    public StatementAnswerDetailsDto getEmptyStatementAnswerDetailsDto() {
        return new OpenAnswerStatementAnswerDetailsDto();
    }
    
    @Override
    public StatementQuestionDetailsDto getStatementQuestionDetailsDto() {
        return new OpenAnswerStatementQuestionDetailsDto(this);
    }

    @Override
    public void delete() {
        super.delete();
        this.answer = null;
    }
    
    @Override
    public String toString() {
        return  "OpenAnswerQuestion{" +
                "answer=" + this.answer +
                '}';
    }
}

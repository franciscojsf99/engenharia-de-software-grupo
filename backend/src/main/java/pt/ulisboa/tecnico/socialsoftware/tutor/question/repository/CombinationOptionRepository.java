package pt.ulisboa.tecnico.socialsoftware.tutor.question.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CombinationOption;

@Repository
@Transactional
public interface CombinationOptionRepository extends JpaRepository<CombinationOption, Integer> {
}

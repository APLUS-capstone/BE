package sg.hsdd.aplus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sg.hsdd.aplus.entity.Question;

public interface QuestionRepository extends JpaRepository<Question, Integer> {

}

package sg.hsdd.aplus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sg.hsdd.aplus.entity.Question;
import sg.hsdd.aplus.service.vo.QuestionVO;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
    List<Question> findAllByRoomUid(int roomUid);
}

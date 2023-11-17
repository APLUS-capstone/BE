package sg.hsdd.aplus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sg.hsdd.aplus.entity.Chatroom;

import java.util.List;
import java.util.Optional;

public interface ChatroomRepository extends JpaRepository<Chatroom, Integer> {
    Optional<Chatroom> findByRoomUid(int roomUid);
    List<Chatroom> findAllByUserUid(int userUid);
}

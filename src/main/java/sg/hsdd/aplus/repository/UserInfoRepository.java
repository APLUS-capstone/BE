package sg.hsdd.aplus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sg.hsdd.aplus.entity.UserInfo;

import java.util.Optional;

public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {
    Optional<UserInfo> findByUserUid(int userUid);
}

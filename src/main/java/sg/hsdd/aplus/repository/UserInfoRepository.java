package sg.hsdd.aplus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sg.hsdd.aplus.entity.UserInfo;

public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {

}

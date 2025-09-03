package io.qltb.qltb.repos;

import io.qltb.qltb.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {

    User findFirstByDeparmentId(Long id);

}

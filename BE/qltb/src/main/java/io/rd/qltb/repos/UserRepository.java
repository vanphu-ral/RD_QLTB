package io.rd.qltb.repos;

import io.rd.qltb.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {

    User findFirstByDeparmentId(Long id);

}

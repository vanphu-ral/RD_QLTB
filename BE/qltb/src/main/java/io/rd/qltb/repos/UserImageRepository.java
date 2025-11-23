package io.rd.qltb.repos;

import io.rd.qltb.domain.UserImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface UserImageRepository extends JpaRepository<UserImage, Long> {
    Optional<UserImage> findByUsername(String username);

    List<UserImage> findByUsernameIn(List<String> usernames);
}

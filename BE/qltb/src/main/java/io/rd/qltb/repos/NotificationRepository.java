package io.rd.qltb.repos;

import io.rd.qltb.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;


public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Notification findFirstByRecipientId(Long id);

}

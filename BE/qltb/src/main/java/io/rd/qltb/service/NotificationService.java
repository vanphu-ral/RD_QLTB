package io.rd.qltb.service;

import io.rd.qltb.domain.Notification;
import io.rd.qltb.domain.User;
import io.rd.qltb.events.BeforeDeleteUser;
import io.rd.qltb.model.NotificationDTO;
import io.rd.qltb.repos.NotificationRepository;
import io.rd.qltb.repos.UserRepository;
import io.rd.qltb.util.NotFoundException;
import io.rd.qltb.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(final NotificationRepository notificationRepository,
            final UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public List<NotificationDTO> findAll() {
        final List<Notification> notifications = notificationRepository.findAll(Sort.by("id"));
        return notifications.stream()
                .map(notification -> mapToDTO(notification, new NotificationDTO()))
                .toList();
    }

    public NotificationDTO get(final Long id) {
        return notificationRepository.findById(id)
                .map(notification -> mapToDTO(notification, new NotificationDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final NotificationDTO notificationDTO) {
        final Notification notification = new Notification();
        mapToEntity(notificationDTO, notification);
        return notificationRepository.save(notification).getId();
    }

    public void update(final Long id, final NotificationDTO notificationDTO) {
        final Notification notification = notificationRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(notificationDTO, notification);
        notificationRepository.save(notification);
    }

    public void delete(final Long id) {
        final Notification notification = notificationRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        notificationRepository.delete(notification);
    }

    private NotificationDTO mapToDTO(final Notification notification,
                                     final NotificationDTO notificationDTO) {
        notificationDTO.setId(notification.getId());
        notificationDTO.setType(notification.getType());
        notificationDTO.setTitle(notification.getTitle());
        notificationDTO.setMessage(notification.getMessage());
        notificationDTO.setEntityType(notification.getEntityType());
        notificationDTO.setEntityId(notification.getEntityId());
        notificationDTO.setIsRead(notification.getIsRead());
        notificationDTO.setCreatedAt(notification.getCreatedAt());

        // Sao chép User (recipient) có kiểm soát
        if (notification.getRecipient() != null) {
            User userCopy = new User();
            userCopy.setId(notification.getRecipient().getId());
            userCopy.setCode(notification.getRecipient().getCode());
            userCopy.setName(notification.getRecipient().getName());
            userCopy.setImg(notification.getRecipient().getImg());
            userCopy.setSignature(notification.getRecipient().getSignature());
            userCopy.setIsActiveNotification(notification.getRecipient().getIsActiveNotification());
            userCopy.setCreatedAt(notification.getRecipient().getCreatedAt());
            userCopy.setUpdatedAt(notification.getRecipient().getUpdatedAt());
            userCopy.setCreatedBy(notification.getRecipient().getCreatedBy());
            userCopy.setUpdatedBy(notification.getRecipient().getUpdatedBy());
            userCopy.setStatus(notification.getRecipient().getStatus());

            // Xóa các quan hệ con để tránh vòng lặp
            userCopy.setDeparment(null);
            userCopy.setRecipientNotifications(null);

            notificationDTO.setRecipient(userCopy);
        } else {
            notificationDTO.setRecipient(null);
        }

        return notificationDTO;
    }


    private Notification mapToEntity(final NotificationDTO notificationDTO,
            final Notification notification) {
        notification.setType(notificationDTO.getType());
        notification.setTitle(notificationDTO.getTitle());
        notification.setMessage(notificationDTO.getMessage());
        notification.setEntityType(notificationDTO.getEntityType());
        notification.setEntityId(notificationDTO.getEntityId());
        notification.setIsRead(notificationDTO.getIsRead());
        notification.setCreatedAt(notificationDTO.getCreatedAt());
        final User recipient = notificationDTO.getRecipient() == null ? null : userRepository.findById(notificationDTO.getRecipient().getId())
                .orElseThrow(() -> new NotFoundException("recipient not found"));
        notification.setRecipient(recipient);
        return notification;
    }

    @EventListener(BeforeDeleteUser.class)
    public void on(final BeforeDeleteUser event) {
        final ReferencedException referencedException = new ReferencedException();
        final Notification recipientNotification = notificationRepository.findFirstByRecipientId(event.getId());
        if (recipientNotification != null) {
            referencedException.setKey("user.notification.recipient.referenced");
            referencedException.addParam(recipientNotification.getId());
            throw referencedException;
        }
    }

}

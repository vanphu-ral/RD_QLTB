package io.rd.qltb.service;

import io.rd.qltb.domain.Department;
import io.rd.qltb.domain.User;
import io.rd.qltb.events.BeforeDeleteDepartment;
import io.rd.qltb.events.BeforeDeleteUser;
import io.rd.qltb.model.UserDTO;
import io.rd.qltb.repos.DepartmentRepository;
import io.rd.qltb.repos.UserRepository;
import io.rd.qltb.util.NotFoundException;
import io.rd.qltb.util.ReferencedException;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final ApplicationEventPublisher publisher;

    public UserService(final UserRepository userRepository,
            final DepartmentRepository departmentRepository,
            final ApplicationEventPublisher publisher) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.publisher = publisher;
    }

    public List<UserDTO> findAll() {
        final List<User> users = userRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
        return users.stream()
                .map(user -> mapToDTO(user, new UserDTO()))
                .toList();
    }

    public UserDTO get(final Long id) {
        return userRepository.findById(id)
                .map(user -> mapToDTO(user, new UserDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final UserDTO userDTO) {
        final User user = new User();
        mapToEntity(userDTO, user);
        return userRepository.save(user).getId();
    }

    public void update(final Long id, final UserDTO userDTO) {
        final User user = userRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(userDTO, user);
        userRepository.save(user);
    }

    public void delete(final Long id) {
        final User user = userRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteUser(id));
        userRepository.delete(user);
    }

    private UserDTO mapToDTO(final User user, final UserDTO dto) {
        dto.setId(user.getId());
        dto.setCode(user.getCode());
        dto.setName(user.getName());
        dto.setImg(user.getImg());
        dto.setSignature(user.getSignature());
        dto.setIsActiveNotification(user.getIsActiveNotification());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setCreatedBy(user.getCreatedBy());
        dto.setUpdatedBy(user.getUpdatedBy());
        dto.setStatus(user.getStatus());

        // Sao chép Department có kiểm soát
        if (user.getDeparment() != null) {
            Department deptCopy = new Department();
            deptCopy.setId(user.getDeparment().getId());
            deptCopy.setCode(user.getDeparment().getCode());
            deptCopy.setName(user.getDeparment().getName());
            deptCopy.setDescription(user.getDeparment().getDescription());
            deptCopy.setCreatedAt(user.getDeparment().getCreatedAt());
            deptCopy.setUpdatedAt(user.getDeparment().getUpdatedAt());
            deptCopy.setCreatedBy(user.getDeparment().getCreatedBy());
            deptCopy.setUpdatedBy(user.getDeparment().getUpdatedBy());
            deptCopy.setStatus(user.getDeparment().getStatus());

            // Xóa các quan hệ con để tránh vòng lặp
            deptCopy.setDeparmentUsers(null);

            dto.setDeparment(deptCopy);
        } else {
            dto.setDeparment(null);
        }

        return dto;
    }


    private User mapToEntity(final UserDTO userDTO, final User user) {
        user.setCode(userDTO.getCode());
        user.setName(userDTO.getName());
        user.setImg(userDTO.getImg());
        user.setSignature(userDTO.getSignature());
        user.setIsActiveNotification(userDTO.getIsActiveNotification());
        user.setCreatedAt(userDTO.getCreatedAt());
        user.setUpdatedAt(userDTO.getUpdatedAt());
        user.setCreatedBy(userDTO.getCreatedBy());
        user.setUpdatedBy(userDTO.getUpdatedBy());
        user.setStatus(userDTO.getStatus());
        final Department deparment = userDTO.getDeparment() == null ? null : departmentRepository.findById(userDTO.getDeparment().getId())
                .orElseThrow(() -> new NotFoundException("deparment not found"));
        user.setDeparment(deparment);
        return user;
    }

    @EventListener(BeforeDeleteDepartment.class)
    public void on(final BeforeDeleteDepartment event) {
        final ReferencedException referencedException = new ReferencedException();
        final User deparmentUser = userRepository.findFirstByDeparmentId(event.getId());
        if (deparmentUser != null) {
            referencedException.setKey("department.user.deparment.referenced");
            referencedException.addParam(deparmentUser.getId());
            throw referencedException;
        }
    }

}

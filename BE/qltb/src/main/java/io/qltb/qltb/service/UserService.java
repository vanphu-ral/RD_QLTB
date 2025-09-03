package io.qltb.qltb.service;

import io.qltb.qltb.domain.Department;
import io.qltb.qltb.domain.User;
import io.qltb.qltb.events.BeforeDeleteDepartment;
import io.qltb.qltb.model.UserDTO;
import io.qltb.qltb.repos.DepartmentRepository;
import io.qltb.qltb.repos.UserRepository;
import io.qltb.qltb.util.NotFoundException;
import io.qltb.qltb.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    public UserService(final UserRepository userRepository,
            final DepartmentRepository departmentRepository) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
    }

    public List<UserDTO> findAll() {
        final List<User> users = userRepository.findAll(Sort.by("id"));
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
        userRepository.delete(user);
    }

    private UserDTO mapToDTO(final User user, final UserDTO userDTO) {
        userDTO.setId(user.getId());
        userDTO.setCode(user.getCode());
        userDTO.setName(user.getName());
        userDTO.setImg(user.getImg());
        userDTO.setSignature(user.getSignature());
        userDTO.setCreatedAt(user.getCreatedAt());
        userDTO.setUpdatedAt(user.getUpdatedAt());
        userDTO.setCreatedBy(user.getCreatedBy());
        userDTO.setUpdatedBy(user.getUpdatedBy());
        userDTO.setStatus(user.getStatus());
        userDTO.setDeparment(user.getDeparment() == null ? null : user.getDeparment());
        return userDTO;
    }

    private User mapToEntity(final UserDTO userDTO, final User user) {
        user.setCode(userDTO.getCode());
        user.setName(userDTO.getName());
        user.setImg(userDTO.getImg());
        user.setSignature(userDTO.getSignature());
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

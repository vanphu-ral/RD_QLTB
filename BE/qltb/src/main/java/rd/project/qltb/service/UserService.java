package rd.project.qltb.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import rd.project.qltb.domain.Department;
import rd.project.qltb.domain.User;
import rd.project.qltb.model.UserDTO;
import rd.project.qltb.repos.DepartmentRepository;
import rd.project.qltb.repos.UserRepository;
import rd.project.qltb.util.NotFoundException;


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
        userRepository.deleteById(id);
    }

    private UserDTO mapToDTO(final User user, final UserDTO userDTO) {
        userDTO.setId(user.getId());
        userDTO.setCode(user.getCode());
        userDTO.setName(user.getName());
        userDTO.setImageSign(user.getImageSign());
        userDTO.setAvatar(user.getAvatar());
        userDTO.setCreatedAt(user.getCreatedAt());
        userDTO.setUpdatedAt(user.getUpdatedAt());
        userDTO.setCreatedBy(user.getCreatedBy());
        userDTO.setDepartment(user.getDepartment() == null ? null : user.getDepartment().getId());
        return userDTO;
    }

    private User mapToEntity(final UserDTO userDTO, final User user) {
        user.setCode(userDTO.getCode());
        user.setName(userDTO.getName());
        user.setImageSign(userDTO.getImageSign());
        user.setAvatar(userDTO.getAvatar());
        user.setCreatedAt(userDTO.getCreatedAt());
        user.setUpdatedAt(userDTO.getUpdatedAt());
        user.setCreatedBy(userDTO.getCreatedBy());
        final Department department = userDTO.getDepartment() == null ? null : departmentRepository.findById(userDTO.getDepartment())
                .orElseThrow(() -> new NotFoundException("department not found"));
        user.setDepartment(department);
        return user;
    }

}

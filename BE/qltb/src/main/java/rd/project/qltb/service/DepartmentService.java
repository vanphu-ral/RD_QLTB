package rd.project.qltb.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import rd.project.qltb.domain.Department;
import rd.project.qltb.domain.User;
import rd.project.qltb.model.DepartmentDTO;
import rd.project.qltb.repos.DepartmentRepository;
import rd.project.qltb.repos.UserRepository;
import rd.project.qltb.util.NotFoundException;
import rd.project.qltb.util.ReferencedWarning;


@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    public DepartmentService(final DepartmentRepository departmentRepository,
            final UserRepository userRepository) {
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
    }

    public List<DepartmentDTO> findAll() {
        final List<Department> departments = departmentRepository.findAll(Sort.by("id"));
        return departments.stream()
                .map(department -> mapToDTO(department, new DepartmentDTO()))
                .toList();
    }

    public DepartmentDTO get(final Long id) {
        return departmentRepository.findById(id)
                .map(department -> mapToDTO(department, new DepartmentDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final DepartmentDTO departmentDTO) {
        final Department department = new Department();
        mapToEntity(departmentDTO, department);
        return departmentRepository.save(department).getId();
    }

    public void update(final Long id, final DepartmentDTO departmentDTO) {
        final Department department = departmentRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(departmentDTO, department);
        departmentRepository.save(department);
    }

    public void delete(final Long id) {
        departmentRepository.deleteById(id);
    }

    private DepartmentDTO mapToDTO(final Department department, final DepartmentDTO departmentDTO) {
        departmentDTO.setId(department.getId());
        departmentDTO.setCode(department.getCode());
        departmentDTO.setName(department.getName());
        departmentDTO.setDescription(department.getDescription());
        departmentDTO.setCreatedAt(department.getCreatedAt());
        departmentDTO.setUpdatedAt(department.getUpdatedAt());
        departmentDTO.setCreatedBy(department.getCreatedBy());
        return departmentDTO;
    }

    private Department mapToEntity(final DepartmentDTO departmentDTO, final Department department) {
        department.setCode(departmentDTO.getCode());
        department.setName(departmentDTO.getName());
        department.setDescription(departmentDTO.getDescription());
        department.setCreatedAt(departmentDTO.getCreatedAt());
        department.setUpdatedAt(departmentDTO.getUpdatedAt());
        department.setCreatedBy(departmentDTO.getCreatedBy());
        return department;
    }

    public ReferencedWarning getReferencedWarning(final Long id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Department department = departmentRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final User departmentUser = userRepository.findFirstByDepartment(department);
        if (departmentUser != null) {
            referencedWarning.setKey("department.user.department.referenced");
            referencedWarning.addParam(departmentUser.getId());
            return referencedWarning;
        }
        return null;
    }

}

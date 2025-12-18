package io.rd.qltb.service;

import io.rd.qltb.domain.Department;
import io.rd.qltb.events.BeforeDeleteDepartment;
import io.rd.qltb.model.DepartmentDTO;
import io.rd.qltb.repos.DepartmentRepository;
import io.rd.qltb.util.NotFoundException;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import static io.rd.qltb.config.ConstantStatusGlobal.DELETED;


@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final ApplicationEventPublisher publisher;

    public DepartmentService(final DepartmentRepository departmentRepository,
            final ApplicationEventPublisher publisher) {
        this.departmentRepository = departmentRepository;
        this.publisher = publisher;
    }

    public List<DepartmentDTO> findAll() {
        final List<Department> departments = departmentRepository.findAllByStatusNotOrderByIdDesc(DELETED);
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
        final Department department = departmentRepository.findById(id)
                .orElseThrow(NotFoundException::new);
//        publisher.publishEvent(new BeforeDeleteDepartment(id));
        department.setStatus(DELETED);
        departmentRepository.save(department);
    }

    private DepartmentDTO mapToDTO(final Department department, final DepartmentDTO departmentDTO) {
        departmentDTO.setId(department.getId());
        departmentDTO.setCode(department.getCode());
        departmentDTO.setName(department.getName());
        departmentDTO.setDescription(department.getDescription());
        departmentDTO.setManager(department.getManager());
        departmentDTO.setCreatedAt(department.getCreatedAt());
        departmentDTO.setUpdatedAt(department.getUpdatedAt());
        departmentDTO.setCreatedBy(department.getCreatedBy());
        departmentDTO.setUpdatedBy(department.getUpdatedBy());
        departmentDTO.setStatus(department.getStatus());
        return departmentDTO;
    }

    private Department mapToEntity(final DepartmentDTO departmentDTO, final Department department) {
        department.setCode(departmentDTO.getCode());
        department.setName(departmentDTO.getName());
        department.setDescription(departmentDTO.getDescription());
        department.setManager(departmentDTO.getManager());
        department.setCreatedAt(departmentDTO.getCreatedAt());
        department.setUpdatedAt(departmentDTO.getUpdatedAt());
        department.setCreatedBy(departmentDTO.getCreatedBy());
        department.setUpdatedBy(departmentDTO.getUpdatedBy());
        department.setStatus(departmentDTO.getStatus());
        return department;
    }

}

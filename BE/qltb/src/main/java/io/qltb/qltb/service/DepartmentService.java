package io.qltb.qltb.service;

import io.qltb.qltb.domain.Department;
import io.qltb.qltb.events.BeforeDeleteDepartment;
import io.qltb.qltb.model.DepartmentDTO;
import io.qltb.qltb.repos.DepartmentRepository;
import io.qltb.qltb.util.NotFoundException;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


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
        final Department department = departmentRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteDepartment(id));
        departmentRepository.delete(department);
    }

    private DepartmentDTO mapToDTO(final Department department, final DepartmentDTO departmentDTO) {
        departmentDTO.setId(department.getId());
        departmentDTO.setCode(department.getCode());
        departmentDTO.setName(department.getName());
        departmentDTO.setDescription(department.getDescription());
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
        department.setCreatedAt(departmentDTO.getCreatedAt());
        department.setUpdatedAt(departmentDTO.getUpdatedAt());
        department.setCreatedBy(departmentDTO.getCreatedBy());
        department.setUpdatedBy(departmentDTO.getUpdatedBy());
        department.setStatus(departmentDTO.getStatus());
        return department;
    }

}

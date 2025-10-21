package io.rd.qltb.service;

import io.rd.qltb.domain.GroupApprovalName;
import io.rd.qltb.model.GroupApprovalNameDTO;
import io.rd.qltb.repos.GroupApprovalNameRepository;
import io.rd.qltb.util.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class GroupApprovalNameService {

    private final GroupApprovalNameRepository groupApprovalNameRepository;

    public GroupApprovalNameService(final GroupApprovalNameRepository groupApprovalNameRepository) {
        this.groupApprovalNameRepository = groupApprovalNameRepository;
    }

    public List<GroupApprovalNameDTO> findAll() {
        final List<GroupApprovalName> groupApprovalNames = groupApprovalNameRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
        return groupApprovalNames.stream()
                .map(groupApprovalName -> mapToDTO(groupApprovalName, new GroupApprovalNameDTO()))
                .toList();
    }

    public GroupApprovalNameDTO get(final Long id) {
        return groupApprovalNameRepository.findById(id)
                .map(groupApprovalName -> mapToDTO(groupApprovalName, new GroupApprovalNameDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final GroupApprovalNameDTO groupApprovalNameDTO) {
        final GroupApprovalName groupApprovalName = new GroupApprovalName();
        mapToEntity(groupApprovalNameDTO, groupApprovalName);
        return groupApprovalNameRepository.save(groupApprovalName).getId();
    }

    public void update(final Long id, final GroupApprovalNameDTO groupApprovalNameDTO) {
        final GroupApprovalName groupApprovalName = groupApprovalNameRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(groupApprovalNameDTO, groupApprovalName);
        groupApprovalNameRepository.save(groupApprovalName);
    }

    public void delete(final Long id) {
        final GroupApprovalName groupApprovalName = groupApprovalNameRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        groupApprovalNameRepository.delete(groupApprovalName);
    }

    private GroupApprovalNameDTO mapToDTO(final GroupApprovalName groupApprovalName,
            final GroupApprovalNameDTO groupApprovalNameDTO) {
        groupApprovalNameDTO.setId(groupApprovalName.getId());
        groupApprovalNameDTO.setCode(groupApprovalName.getCode());
        groupApprovalNameDTO.setName(groupApprovalName.getName());
        groupApprovalNameDTO.setDescription(groupApprovalName.getDescription());
        groupApprovalNameDTO.setCreatedAt(groupApprovalName.getCreatedAt());
        groupApprovalNameDTO.setUpdatedAt(groupApprovalName.getUpdatedAt());
        groupApprovalNameDTO.setCreatedBy(groupApprovalName.getCreatedBy());
        groupApprovalNameDTO.setUpdatedBy(groupApprovalName.getUpdatedBy());
        groupApprovalNameDTO.setStatus(groupApprovalName.getStatus());
        return groupApprovalNameDTO;
    }

    private GroupApprovalName mapToEntity(final GroupApprovalNameDTO groupApprovalNameDTO,
            final GroupApprovalName groupApprovalName) {
        groupApprovalName.setCode(groupApprovalNameDTO.getCode());
        groupApprovalName.setName(groupApprovalNameDTO.getName());
        groupApprovalName.setDescription(groupApprovalNameDTO.getDescription());
        groupApprovalName.setCreatedAt(groupApprovalNameDTO.getCreatedAt());
        groupApprovalName.setUpdatedAt(groupApprovalNameDTO.getUpdatedAt());
        groupApprovalName.setCreatedBy(groupApprovalNameDTO.getCreatedBy());
        groupApprovalName.setUpdatedBy(groupApprovalNameDTO.getUpdatedBy());
        groupApprovalName.setStatus(groupApprovalNameDTO.getStatus());
        return groupApprovalName;
    }

}

package io.rd.qltb.service;

import io.rd.qltb.domain.ApprovalGroup;
import io.rd.qltb.domain.ApprovalGroupUser;
import io.rd.qltb.events.BeforeDeleteApprovalGroup;
import io.rd.qltb.model.ApprovalGroupUserDTO;
import io.rd.qltb.repos.ApprovalGroupRepository;
import io.rd.qltb.repos.ApprovalGroupUserRepository;
import io.rd.qltb.util.NotFoundException;
import io.rd.qltb.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class ApprovalGroupUserService {

    private final ApprovalGroupUserRepository approvalGroupUserRepository;
    private final ApprovalGroupRepository approvalGroupRepository;

    public ApprovalGroupUserService(final ApprovalGroupUserRepository approvalGroupUserRepository,
            final ApprovalGroupRepository approvalGroupRepository) {
        this.approvalGroupUserRepository = approvalGroupUserRepository;
        this.approvalGroupRepository = approvalGroupRepository;
    }

    public List<ApprovalGroupUserDTO> findAll() {
        final List<ApprovalGroupUser> approvalGroupUsers = approvalGroupUserRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
        return approvalGroupUsers.stream()
                .map(approvalGroupUser -> mapToDTO(approvalGroupUser, new ApprovalGroupUserDTO()))
                .toList();
    }

    public ApprovalGroupUserDTO get(final Long id) {
        return approvalGroupUserRepository.findById(id)
                .map(approvalGroupUser -> mapToDTO(approvalGroupUser, new ApprovalGroupUserDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final ApprovalGroupUserDTO approvalGroupUserDTO) {
        final ApprovalGroupUser approvalGroupUser = new ApprovalGroupUser();
        mapToEntity(approvalGroupUserDTO, approvalGroupUser);
        return approvalGroupUserRepository.save(approvalGroupUser).getId();
    }

    public void update(final Long id, final ApprovalGroupUserDTO approvalGroupUserDTO) {
        final ApprovalGroupUser approvalGroupUser = approvalGroupUserRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(approvalGroupUserDTO, approvalGroupUser);
        approvalGroupUserRepository.save(approvalGroupUser);
    }

    public void delete(final Long id) {
        final ApprovalGroupUser approvalGroupUser = approvalGroupUserRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        approvalGroupUserRepository.delete(approvalGroupUser);
    }

    private ApprovalGroupUserDTO mapToDTO(final ApprovalGroupUser approvalGroupUser,
                                          final ApprovalGroupUserDTO approvalGroupUserDTO) {
        approvalGroupUserDTO.setId(approvalGroupUser.getId());
        approvalGroupUserDTO.setUsername(approvalGroupUser.getUsername());
        approvalGroupUserDTO.setStatus(approvalGroupUser.getStatus());
        approvalGroupUserDTO.setTimeSign(approvalGroupUser.getTimeSign());
        approvalGroupUserDTO.setCreatedAt(approvalGroupUser.getCreatedAt());
        approvalGroupUserDTO.setUpdatedAt(approvalGroupUser.getUpdatedAt());

        // Sao chép ApprovalGroup có kiểm soát
        if (approvalGroupUser.getGroup() != null) {
            ApprovalGroup groupCopy = new ApprovalGroup();
            groupCopy.setId(approvalGroupUser.getGroup().getId());
            groupCopy.setLevel(approvalGroupUser.getGroup().getLevel());
            groupCopy.setIsRequired(approvalGroupUser.getGroup().getIsRequired());
            groupCopy.setCreatedAt(approvalGroupUser.getGroup().getCreatedAt());
            groupCopy.setUpdatedAt(approvalGroupUser.getGroup().getUpdatedAt());
            groupCopy.setCreatedBy(approvalGroupUser.getGroup().getCreatedBy());
            groupCopy.setUpdatedBy(approvalGroupUser.getGroup().getUpdatedBy());
            groupCopy.setStatus(approvalGroupUser.getGroup().getStatus());

            // Xóa các quan hệ con để tránh vòng lặp
            groupCopy.setWorkflow(null);
            groupCopy.setGroupApprovalGroupUsers(null);

            approvalGroupUserDTO.setGroup(groupCopy);
        } else {
            approvalGroupUserDTO.setGroup(null);
        }

        return approvalGroupUserDTO;
    }


    private ApprovalGroupUser mapToEntity(final ApprovalGroupUserDTO approvalGroupUserDTO,
            final ApprovalGroupUser approvalGroupUser) {
        approvalGroupUser.setUsername(approvalGroupUserDTO.getUsername());
        approvalGroupUser.setStatus(approvalGroupUserDTO.getStatus());
        approvalGroupUser.setTimeSign(approvalGroupUserDTO.getTimeSign());
        approvalGroupUser.setCreatedAt(approvalGroupUserDTO.getCreatedAt());
        approvalGroupUser.setUpdatedAt(approvalGroupUserDTO.getUpdatedAt());
        final ApprovalGroup group = approvalGroupUserDTO.getGroup() == null ? null : approvalGroupRepository.findById(approvalGroupUserDTO.getGroup().getId())
                .orElseThrow(() -> new NotFoundException("group not found"));
        approvalGroupUser.setGroup(group);
        return approvalGroupUser;
    }

    @EventListener(BeforeDeleteApprovalGroup.class)
    public void on(final BeforeDeleteApprovalGroup event) {
        final ReferencedException referencedException = new ReferencedException();
        final ApprovalGroupUser groupApprovalGroupUser = approvalGroupUserRepository.findFirstByGroupId(event.getId());
        if (groupApprovalGroupUser != null) {
            referencedException.setKey("approvalGroup.approvalGroupUser.group.referenced");
            referencedException.addParam(groupApprovalGroupUser.getId());
            throw referencedException;
        }
    }

}

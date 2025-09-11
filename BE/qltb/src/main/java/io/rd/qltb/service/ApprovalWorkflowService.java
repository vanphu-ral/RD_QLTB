package io.rd.qltb.service;

import io.rd.qltb.domain.ApprovalWorkflow;
import io.rd.qltb.events.BeforeDeleteApprovalWorkflow;
import io.rd.qltb.model.ApprovalWorkflowDTO;
import io.rd.qltb.repos.ApprovalWorkflowRepository;
import io.rd.qltb.util.NotFoundException;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class ApprovalWorkflowService {

    private final ApprovalWorkflowRepository approvalWorkflowRepository;
    private final ApplicationEventPublisher publisher;

    public ApprovalWorkflowService(final ApprovalWorkflowRepository approvalWorkflowRepository,
            final ApplicationEventPublisher publisher) {
        this.approvalWorkflowRepository = approvalWorkflowRepository;
        this.publisher = publisher;
    }

    public List<ApprovalWorkflowDTO> findAll() {
        final List<ApprovalWorkflow> approvalWorkflows = approvalWorkflowRepository.findAll(Sort.by("id"));
        return approvalWorkflows.stream()
                .map(approvalWorkflow -> mapToDTO(approvalWorkflow, new ApprovalWorkflowDTO()))
                .toList();
    }

    public ApprovalWorkflowDTO get(final Long id) {
        return approvalWorkflowRepository.findById(id)
                .map(approvalWorkflow -> mapToDTO(approvalWorkflow, new ApprovalWorkflowDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final ApprovalWorkflowDTO approvalWorkflowDTO) {
        final ApprovalWorkflow approvalWorkflow = new ApprovalWorkflow();
        mapToEntity(approvalWorkflowDTO, approvalWorkflow);
        return approvalWorkflowRepository.save(approvalWorkflow).getId();
    }

    public void update(final Long id, final ApprovalWorkflowDTO approvalWorkflowDTO) {
        final ApprovalWorkflow approvalWorkflow = approvalWorkflowRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(approvalWorkflowDTO, approvalWorkflow);
        approvalWorkflowRepository.save(approvalWorkflow);
    }

    public void delete(final Long id) {
        final ApprovalWorkflow approvalWorkflow = approvalWorkflowRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteApprovalWorkflow(id));
        approvalWorkflowRepository.delete(approvalWorkflow);
    }

    private ApprovalWorkflowDTO mapToDTO(final ApprovalWorkflow approvalWorkflow,
            final ApprovalWorkflowDTO approvalWorkflowDTO) {
        approvalWorkflowDTO.setId(approvalWorkflow.getId());
        approvalWorkflowDTO.setCode(approvalWorkflow.getCode());
        approvalWorkflowDTO.setName(approvalWorkflow.getName());
        approvalWorkflowDTO.setDescription(approvalWorkflow.getDescription());
        approvalWorkflowDTO.setCreatedAt(approvalWorkflow.getCreatedAt());
        approvalWorkflowDTO.setUpdatedAt(approvalWorkflow.getUpdatedAt());
        approvalWorkflowDTO.setCreatedBy(approvalWorkflow.getCreatedBy());
        approvalWorkflowDTO.setUpdatedBy(approvalWorkflow.getUpdatedBy());
        approvalWorkflowDTO.setStatus(approvalWorkflow.getStatus());
        return approvalWorkflowDTO;
    }

    private ApprovalWorkflow mapToEntity(final ApprovalWorkflowDTO approvalWorkflowDTO,
            final ApprovalWorkflow approvalWorkflow) {
        approvalWorkflow.setCode(approvalWorkflowDTO.getCode());
        approvalWorkflow.setName(approvalWorkflowDTO.getName());
        approvalWorkflow.setDescription(approvalWorkflowDTO.getDescription());
        approvalWorkflow.setCreatedAt(approvalWorkflowDTO.getCreatedAt());
        approvalWorkflow.setUpdatedAt(approvalWorkflowDTO.getUpdatedAt());
        approvalWorkflow.setCreatedBy(approvalWorkflowDTO.getCreatedBy());
        approvalWorkflow.setUpdatedBy(approvalWorkflowDTO.getUpdatedBy());
        approvalWorkflow.setStatus(approvalWorkflowDTO.getStatus());
        return approvalWorkflow;
    }

}

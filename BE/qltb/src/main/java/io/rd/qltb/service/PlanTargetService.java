package io.rd.qltb.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.rd.qltb.domain.ApprovalWorkflow;
import io.rd.qltb.domain.Branch;
import io.rd.qltb.domain.PlanTarget;
import io.rd.qltb.events.BeforeDeletePlanTarget;
import io.rd.qltb.model.PlanTargetDTO;
import io.rd.qltb.repos.ApprovalWorkflowRepository;
import io.rd.qltb.repos.BranchRepository;
import io.rd.qltb.repos.PlanTargetRepository;
import io.rd.qltb.repos.TeamRepository;
import io.rd.qltb.util.NotFoundException;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class PlanTargetService {

    private final PlanTargetRepository planTargetRepository;
    private final ApplicationEventPublisher publisher;
    private final BranchRepository branchRepository;
    private final ApprovalWorkflowRepository approvalWorkflowRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PlanTargetService(final PlanTargetRepository planTargetRepository,
            final ApplicationEventPublisher publisher,
                             final BranchRepository branchRepository,
                             final ApprovalWorkflowRepository approvalWorkflowRepository) {
        this.planTargetRepository = planTargetRepository;
        this.publisher = publisher;
        this.branchRepository = branchRepository;
        this.approvalWorkflowRepository = approvalWorkflowRepository;
    }

    public List<PlanTargetDTO> findAll() {
        final List<PlanTarget> planTargets = planTargetRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
        return planTargets.stream()
                .map(planTarget -> mapToDTO(planTarget, new PlanTargetDTO()))
                .toList();
    }

    public PlanTargetDTO get(final Long id) {
        return planTargetRepository.findById(id)
                .map(planTarget -> mapToDTO(planTarget, new PlanTargetDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final PlanTargetDTO planTargetDTO) {
        final PlanTarget planTarget = new PlanTarget();
        mapToEntity(planTargetDTO, planTarget);
        if (planTargetDTO.getListItems() != null && !planTargetDTO.getListItems().isEmpty()) {
            try {
                // 1. Parse chuỗi JSON sang List<Map>
                List<Map<String, Object>> items = objectMapper.readValue(
                        planTargetDTO.getListItems(),
                        new TypeReference<List<Map<String, Object>>>() {}
                );

                // 2. Duyệt index và gán code
                for (int i = 0; i < items.size(); i++) {
                    String generatedCode = planTarget.getCode() + "-" + (i + 1); // +1 nếu bạn muốn bắt đầu từ 1 thay vì 0
                    items.get(i).put("code", generatedCode);
                }

                // 3. Chuyển ngược lại thành String để lưu vào Entity
                String updatedListItems = objectMapper.writeValueAsString(items);
                planTarget.setListItems(updatedListItems);

            } catch (Exception e) {
                // Handle exception (log lỗi parse JSON)
                throw new RuntimeException("Lỗi xử lý dữ liệu listItems JSON", e);
            }
        }
        return planTargetRepository.save(planTarget).getId();
    }

    public void update(final Long id, final PlanTargetDTO planTargetDTO) {
        final PlanTarget planTarget = planTargetRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(planTargetDTO, planTarget);
        planTargetRepository.save(planTarget);
    }

    public void delete(final Long id) {
        final PlanTarget planTarget = planTargetRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeletePlanTarget(id));
        planTargetRepository.delete(planTarget);
    }

    private PlanTargetDTO mapToDTO(final PlanTarget planTarget, final PlanTargetDTO planTargetDTO) {
        planTargetDTO.setId(planTarget.getId());
        planTargetDTO.setCode(planTarget.getCode());
        planTargetDTO.setName(planTarget.getName());
        planTargetDTO.setPlanCode(planTarget.getPlanCode());
        planTargetDTO.setPlanNumber(planTarget.getPlanNumber());
        planTargetDTO.setNumberOfIssuances(planTarget.getNumberOfIssuances());
        planTargetDTO.setYear(planTarget.getYear());
        planTargetDTO.setListItems(planTarget.getListItems());
        planTargetDTO.setDescription(planTarget.getDescription());
        planTargetDTO.setCreatedAt(planTarget.getCreatedAt());
        planTargetDTO.setUpdatedAt(planTarget.getUpdatedAt());
        planTargetDTO.setCreatedBy(planTarget.getCreatedBy());
        planTargetDTO.setUpdatedBy(planTarget.getUpdatedBy());
        planTargetDTO.setStatus(planTarget.getStatus());

        if (planTarget.getBranch() != null) {
            Branch branchCopy = new Branch();
            branchCopy.setId(planTarget.getBranch().getId());
            branchCopy.setCode(planTarget.getBranch().getCode());
            branchCopy.setName(planTarget.getBranch().getName());
            branchCopy.setDescription(planTarget.getBranch().getDescription());
            branchCopy.setCreatedAt(planTarget.getBranch().getCreatedAt());
            branchCopy.setUpdatedAt(planTarget.getBranch().getUpdatedAt());
            branchCopy.setCreatedBy(planTarget.getBranch().getCreatedBy());
            branchCopy.setUpdatedBy(planTarget.getBranch().getUpdatedBy());
            branchCopy.setStatus(planTarget.getBranch().getStatus());

            // Xóa các quan hệ con để tránh vòng lặp
            branchCopy.setBranchTeams(null);
            branchCopy.setBranchDevices(null);
            branchCopy.setFactory(null);

            planTargetDTO.setBranch(branchCopy);
        } else {
            planTargetDTO.setBranch(null);
        }

        if (planTarget.getApprovalWorkflow() != null) {
            ApprovalWorkflow approvalWorkflowCopy = new ApprovalWorkflow();
            approvalWorkflowCopy.setId(planTarget.getApprovalWorkflow().getId());
            approvalWorkflowCopy.setCode(planTarget.getApprovalWorkflow().getCode());
            approvalWorkflowCopy.setName(planTarget.getApprovalWorkflow().getName());
            approvalWorkflowCopy.setStatus(planTarget.getApprovalWorkflow().getStatus());

            approvalWorkflowCopy.setWorkflowApprovalGroups(null);

            planTargetDTO.setApprovalWorkflow(approvalWorkflowCopy);
        } else {
            planTargetDTO.setApprovalWorkflow(null);
        }

        return planTargetDTO;
    }

    private PlanTarget mapToEntity(final PlanTargetDTO planTargetDTO, final PlanTarget planTarget) {
        planTarget.setCode(planTargetDTO.getCode());
        planTarget.setName(planTargetDTO.getName());
        planTarget.setPlanCode(planTargetDTO.getPlanCode());
        planTarget.setPlanNumber(planTargetDTO.getPlanNumber());
        planTarget.setNumberOfIssuances(planTargetDTO.getNumberOfIssuances());
        planTarget.setYear(planTargetDTO.getYear());
        planTarget.setListItems(planTargetDTO.getListItems());
        planTarget.setDescription(planTargetDTO.getDescription());
        planTarget.setCreatedAt(planTargetDTO.getCreatedAt());
        planTarget.setUpdatedAt(planTargetDTO.getUpdatedAt());
        planTarget.setCreatedBy(planTargetDTO.getCreatedBy());
        planTarget.setUpdatedBy(planTargetDTO.getUpdatedBy());
        planTarget.setStatus(planTargetDTO.getStatus());

        final Branch branch = planTargetDTO.getBranch() == null ? null : branchRepository.findById(planTargetDTO.getBranch().getId())
                .orElseThrow(() -> new NotFoundException("branch not found"));
        planTarget.setBranch(branch);

        final ApprovalWorkflow approvalWorkflow = planTargetDTO.getApprovalWorkflow() == null ? null :
                approvalWorkflowRepository.findById(planTargetDTO.getApprovalWorkflow().getId())
                        .orElseThrow(() -> new NotFoundException("approvalWorkflow not found"));
        planTarget.setApprovalWorkflow(approvalWorkflow);
        return planTarget;
    }

}

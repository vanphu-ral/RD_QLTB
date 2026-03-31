package io.rd.qltb.service;

import io.rd.qltb.domain.ApprovalGroup;
import io.rd.qltb.domain.ApprovalWorkflow;
import io.rd.qltb.events.BeforeDeleteApprovalWorkflow;
import io.rd.qltb.model.ApprovalWorkflowDTO;
import io.rd.qltb.repos.ApprovalGroupRepository;
import io.rd.qltb.repos.ApprovalGroupUserRepository;
import io.rd.qltb.repos.ApprovalWorkflowRepository;
import io.rd.qltb.util.NotFoundException;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import java.util.ArrayList;
import java.util.Map;
import io.rd.qltb.domain.Branch;

import static io.rd.qltb.config.ConstantStatusGlobal.*;


@Service
public class ApprovalWorkflowService {

    @PersistenceContext
    private EntityManager entityManager;
    private final ApprovalWorkflowRepository approvalWorkflowRepository;
    private final ApplicationEventPublisher publisher;
    private final ApprovalGroupRepository approvalGroupRepository;
    private final ApprovalGroupUserRepository approvalGroupUserRepository;

    public ApprovalWorkflowService(EntityManager entityManager, final ApprovalWorkflowRepository approvalWorkflowRepository,
                                   final ApplicationEventPublisher publisher, ApprovalGroupRepository approvalGroupRepository, ApprovalGroupUserRepository approvalGroupUserRepository) {
        this.entityManager = entityManager;
        this.approvalWorkflowRepository = approvalWorkflowRepository;
        this.publisher = publisher;
        this.approvalGroupRepository = approvalGroupRepository;
        this.approvalGroupUserRepository = approvalGroupUserRepository;
    }

    @Transactional
    public Page<ApprovalWorkflowDTO> findWorkflowsPaged(Map<String, Object> filters, int page) {
        int pageSize = 10;
        var cb = entityManager.getCriteriaBuilder();

        // Query chính
        var cq = cb.createQuery(ApprovalWorkflow.class);
        var root = cq.from(ApprovalWorkflow.class);

        // Query count
        var countQuery = cb.createQuery(Long.class);
        var countRoot = countQuery.from(ApprovalWorkflow.class);

        // Xây dựng Predicates
        Predicate[] dataPredicates = buildPredicates(filters, cb, root);
        Predicate[] countPredicates = buildPredicates(filters, cb, countRoot);

        // Execute Data Query
        cq.where(dataPredicates).orderBy(cb.desc(root.get("id")));
        var query = entityManager.createQuery(cq);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);

        List<ApprovalWorkflowDTO> dtos = query.getResultList().stream()
                .map(workflow -> mapToDTO(workflow, new ApprovalWorkflowDTO()))
                .toList();

        // Execute Count Query
        countQuery.select(cb.count(countRoot)).where(countPredicates);
        Long totalRecords = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(dtos, PageRequest.of(page, pageSize), totalRecords);
    }

    private Predicate[] buildPredicates(Map<String, Object> filters, CriteriaBuilder cb, Root<ApprovalWorkflow> root) {
        List<Predicate> predicates = new ArrayList<>();

        filters.forEach((key, value) -> {
            if (value != null && !value.toString().isEmpty()) {
                Path<?> path;

                // XỬ LÝ JOIN TỰ ĐỘNG
                if ("branch".equals(key)) {
                    path = root.join(key, JoinType.LEFT).get("name");
                } else if (key.contains(".")) {
                    String[] parts = key.split("\\.");
                    Join<Object, Object> join = root.join(parts[0], JoinType.LEFT);
                    path = join.get(parts[1]);
                } else {
                    path = root.get(key);
                }

                // PHÂN LOẠI KIỂU DỮ LIỆU ĐỂ TẠO PREDICATE
                predicates.add(cb.like(cb.lower(path.as(String.class)), "%" + value.toString().toLowerCase() + "%"));
            }
        });

        predicates.add(cb.notEqual(root.get("status"), DELETED));
        return predicates.toArray(new Predicate[0]);
    }

    public List<ApprovalWorkflowDTO> findAll() {
        final List<ApprovalWorkflow> approvalWorkflows = approvalWorkflowRepository.findByStatusNotOrderByIdDesc(DELETED);
        return approvalWorkflows.stream()
                .map(approvalWorkflow -> mapToDTO(approvalWorkflow, new ApprovalWorkflowDTO()))
                .toList();
    }
    public List<ApprovalWorkflowDTO> findAllByApprove() {
        final List<ApprovalWorkflow> approvalWorkflows = approvalWorkflowRepository.findByStatusOrderByIdDesc(APPROVED);
        return approvalWorkflows.stream()
                .map(approvalWorkflow -> mapToDTO(approvalWorkflow, new ApprovalWorkflowDTO()))
                .toList();
    }
    public List<ApprovalWorkflowDTO> findAllByBranchAndApprove(String branchName) {
        if (branchName != null) {
            // Ensure if branchName gets parsed as an array string like "['Ngành Điện tử']" it is cleaned
            branchName = branchName.replaceAll("^\\[[\"']?", "").replaceAll("[\"']?\\]$", "").trim();
            // If the branch string is "Ngành Điện tử tự động", we only check for "Điện tử tự động" 
            // to bypass any unicode composition mismatch on the word "Ngành"
            if (branchName.startsWith("Ngành ")) {
                branchName = branchName.substring(6).trim();
            }
        }
        final List<ApprovalWorkflow> approvalWorkflows = approvalWorkflowRepository.findByBranch_NameContainingIgnoreCaseAndStatusOrderByIdDesc(branchName, DRAFF);
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
//       List<ApprovalGroup> approvalGroups = approvalGroupRepository.findByWorkflowId(id);
//        for (ApprovalGroup approvalGroup : approvalGroups) {
//            approvalGroupUserRepository.deleteItemByGroupId(approvalGroup.getId());
//        }
//        approvalGroupRepository.deleteItemByWorkflowId(id);
//        approvalGroupRepository.flush(); // đảm bảo xóa được thực hiện ngay
        approvalWorkflow.setStatus(DELETED);
        approvalWorkflowRepository.save(approvalWorkflow);

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

        // Deep copy Branch to avoid recursion and excess data
        if (approvalWorkflow.getBranch() != null) {
            Branch branchCopy = new Branch();
            branchCopy.setId(approvalWorkflow.getBranch().getId());
            branchCopy.setCode(approvalWorkflow.getBranch().getCode());
            branchCopy.setName(approvalWorkflow.getBranch().getName());
            branchCopy.setDescription(approvalWorkflow.getBranch().getDescription());
            branchCopy.setManager(approvalWorkflow.getBranch().getManager());
            branchCopy.setCreatedAt(approvalWorkflow.getBranch().getCreatedAt());
            branchCopy.setUpdatedAt(approvalWorkflow.getBranch().getUpdatedAt());
            branchCopy.setCreatedBy(approvalWorkflow.getBranch().getCreatedBy());
            branchCopy.setUpdatedBy(approvalWorkflow.getBranch().getUpdatedBy());
            branchCopy.setStatus(approvalWorkflow.getBranch().getStatus());

            // Clear relationships
            branchCopy.setFactory(null);
            branchCopy.setBranchTeams(null);
            branchCopy.setBranchDevices(null);
            branchCopy.setSampleReports(null);

            approvalWorkflowDTO.setBranch(branchCopy);
        } else {
            approvalWorkflowDTO.setBranch(null);
        }

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
        approvalWorkflow.setBranch(approvalWorkflowDTO.getBranch());
        return approvalWorkflow;
    }

}

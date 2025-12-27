package io.rd.qltb.service;

import io.rd.qltb.domain.*;
import io.rd.qltb.model.PlanDetailDTO;
import io.rd.qltb.model.PlanSupplieDTO;
import io.rd.qltb.model.PlanSupplieDetailDTO;
import io.rd.qltb.repos.*;
import io.rd.qltb.util.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class PlanSupplieService {

    private final PlanSupplieRepository planSupplieRepository;
    private final FactoryRepository factoryRepository;
    private final BranchRepository branchRepository;
    private final TeamRepository teamRepository;
    private final ApprovalWorkflowRepository approvalWorkflowRepository;

    private final PlanSupplieDetailService planSupplieDetailService;

    public PlanSupplieService(final PlanSupplieRepository planSupplieRepository,
                              final FactoryRepository factoryRepository,
                              final BranchRepository branchRepository,
                              final TeamRepository teamRepository,
                              final ApprovalWorkflowRepository approvalWorkflowRepository,
                              final PlanSupplieDetailService planSupplieDetailService) {
        this.planSupplieRepository = planSupplieRepository;
        this.factoryRepository = factoryRepository;
        this.branchRepository = branchRepository;
        this.teamRepository = teamRepository;
        this.approvalWorkflowRepository = approvalWorkflowRepository;
        this.planSupplieDetailService = planSupplieDetailService;
    }

    public List<PlanSupplieDTO> findAll() {
        final List<PlanSupplie> planSupplies = planSupplieRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
        return planSupplies.stream()
                .map(planSupplie -> mapToDTO(planSupplie, new PlanSupplieDTO()))
                .toList();
    }

    public PlanSupplieDTO get(final Long id) {
        return planSupplieRepository.findById(id)
                .map(planSupplie -> mapToDTO(planSupplie, new PlanSupplieDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final PlanSupplieDTO planSupplieDTO) {
        final PlanSupplie planSupplie = new PlanSupplie();
        mapToEntity(planSupplieDTO, planSupplie);
        if (planSupplieDTO.getPlanSupplieDetails() != null) {
            planSupplieDTO.getPlanSupplieDetails().forEach(detailDTO -> {
                PlanSupplieDetail detail = new PlanSupplieDetail();
                planSupplieDetailService.mapToEntity(detailDTO, detail);
                detail.setCreatedAt(LocalDateTime.now());
                detail.setUpdatedAt(LocalDateTime.now());
                detail.setPlanSupplie(planSupplie);
                planSupplie.getPlanSupplieDetails().add(detail);
            });
        }
        return planSupplieRepository.save(planSupplie).getId();
    }

    public void update(final Long id, final PlanSupplieDTO planSupplieDTO) {
        final PlanSupplie planSupplie = planSupplieRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        mapToEntity(planSupplieDTO, planSupplie);
        planSupplie.setUpdatedAt(LocalDateTime.now());
        if (planSupplieDTO.getPlanSupplieDetails() != null) {
            Set<Long> dtoDetailIds = planSupplieDTO.getPlanSupplieDetails().stream()
                    .map(PlanSupplieDetailDTO::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            planSupplie.getPlanSupplieDetails().removeIf(detail -> !dtoDetailIds.contains(detail.getId()));
            planSupplieDTO.getPlanSupplieDetails().forEach(detailDTO -> {
                if (detailDTO.getId() != null) {
                    planSupplie.getPlanSupplieDetails().stream()
                            .filter(d -> d.getId().equals(detailDTO.getId()))
                            .findFirst()
                            .ifPresent(existingDetail -> {
                                planSupplieDetailService.mapToEntity(detailDTO, existingDetail);
                                existingDetail.setPlanSupplie(planSupplie);
                                existingDetail.setUpdatedAt(LocalDateTime.now());
                            });
                } else {
                    PlanSupplieDetail newDetail = new PlanSupplieDetail();
                    planSupplieDetailService.mapToEntity(detailDTO, newDetail);
                    newDetail.setCreatedAt(LocalDateTime.now());
                    newDetail.setUpdatedAt(LocalDateTime.now());
                    newDetail.setPlanSupplie(planSupplie);
                    planSupplie.getPlanSupplieDetails().add(newDetail);
                }
            });
        } else {
            planSupplie.getPlanSupplieDetails().clear();
        }

        planSupplieRepository.save(planSupplie);
    }

    public void delete(final Long id) {
        final PlanSupplie planSupplie = planSupplieRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        planSupplieRepository.delete(planSupplie);
    }

    public PlanSupplieDTO mapToDTO(final PlanSupplie planSupplie,
            final PlanSupplieDTO planSupplieDTO) {
        planSupplieDTO.setId(planSupplie.getId());
        planSupplieDTO.setCode(planSupplie.getCode());
        planSupplieDTO.setName(planSupplie.getName());
        planSupplieDTO.setType(planSupplie.getType());
        planSupplieDTO.setPlanNumber(planSupplie.getPlanNumber());
        planSupplieDTO.setDescription(planSupplie.getDescription());
        planSupplieDTO.setUserPerformer(planSupplie.getUserPerformer());
        planSupplieDTO.setFromDate(planSupplie.getFromDate());
        planSupplieDTO.setToDate(planSupplie.getToDate());
        planSupplieDTO.setCreatedAt(planSupplie.getCreatedAt());
        planSupplieDTO.setUpdatedAt(planSupplie.getUpdatedAt());
        planSupplieDTO.setCreatedBy(planSupplie.getCreatedBy());
        planSupplieDTO.setUpdatedBy(planSupplie.getUpdatedBy());
        planSupplieDTO.setStatus(planSupplie.getStatus());

        if (planSupplie.getFactory() != null) {
            Factory factoryCopy = new Factory();
            factoryCopy.setId(planSupplie.getFactory().getId());
            factoryCopy.setCode(planSupplie.getFactory().getCode());
            factoryCopy.setName(planSupplie.getFactory().getName());
            factoryCopy.setStatus(planSupplie.getFactory().getStatus());

            factoryCopy.setFactoryBranches(null);

            planSupplieDTO.setFactory(factoryCopy);
        } else {
            planSupplieDTO.setFactory(null);
        }

        if (planSupplie.getBranch() != null) {
            Branch branchCopy = new Branch();
            branchCopy.setId(planSupplie.getBranch().getId());
            branchCopy.setCode(planSupplie.getBranch().getCode());
            branchCopy.setName(planSupplie.getBranch().getName());
            branchCopy.setDescription(planSupplie.getBranch().getDescription());
            branchCopy.setCreatedAt(planSupplie.getBranch().getCreatedAt());
            branchCopy.setUpdatedAt(planSupplie.getBranch().getUpdatedAt());
            branchCopy.setCreatedBy(planSupplie.getBranch().getCreatedBy());
            branchCopy.setUpdatedBy(planSupplie.getBranch().getUpdatedBy());
            branchCopy.setStatus(planSupplie.getBranch().getStatus());

            // Xóa các quan hệ con để tránh vòng lặp
            branchCopy.setBranchTeams(null);
            branchCopy.setBranchDevices(null);
            branchCopy.setFactory(null);

            planSupplieDTO.setBranch(branchCopy);
        } else {
            planSupplieDTO.setBranch(null);
        }

        if (planSupplie.getTeam() != null) {
            Team teamCopy = new Team();
            teamCopy.setId(planSupplie.getTeam().getId());
            teamCopy.setCode(planSupplie.getTeam().getCode());
            teamCopy.setName(planSupplie.getTeam().getName());
            teamCopy.setDescription(planSupplie.getTeam().getDescription());
            teamCopy.setCreatedAt(planSupplie.getTeam().getCreatedAt());
            teamCopy.setUpdatedAt(planSupplie.getTeam().getUpdatedAt());
            teamCopy.setCreatedBy(planSupplie.getTeam().getCreatedBy());
            teamCopy.setUpdatedBy(planSupplie.getTeam().getUpdatedBy());
            teamCopy.setStatus(planSupplie.getTeam().getStatus());

            // Xóa các quan hệ con để tránh vòng lặp
            teamCopy.setTeamLines(null);
            teamCopy.setTeamDevices(null);

            planSupplieDTO.setTeam(teamCopy);
        } else {
            planSupplieDTO.setTeam(null);
        }

        if (planSupplie.getApprovalWorkflow() != null) {
            ApprovalWorkflow approvalWorkflowCopy = new ApprovalWorkflow();
            approvalWorkflowCopy.setId(planSupplie.getApprovalWorkflow().getId());
            approvalWorkflowCopy.setCode(planSupplie.getApprovalWorkflow().getCode());
            approvalWorkflowCopy.setName(planSupplie.getApprovalWorkflow().getName());
            approvalWorkflowCopy.setStatus(planSupplie.getApprovalWorkflow().getStatus());

            approvalWorkflowCopy.setWorkflowApprovalGroups(null);

            planSupplieDTO.setApprovalWorkflow(approvalWorkflowCopy);
        } else {
            planSupplieDTO.setApprovalWorkflow(null);
        }

        if (planSupplie.getPlanSupplieDetails() != null) {
            List<PlanSupplieDetailDTO> planSupplieDetailDTOS = planSupplie.getPlanSupplieDetails().stream()
                    .map(planSupplieDetail -> planSupplieDetailService.mapToDTO(planSupplieDetail, new PlanSupplieDetailDTO()))
                    .toList();
            // xoa quan he de tranh vong lap
            planSupplieDetailDTOS.forEach(detailDTO -> {
                detailDTO.setPlanSupplie(null);
            });
            planSupplieDTO.setPlanSupplieDetails(planSupplieDetailDTOS);
        }

        return planSupplieDTO;
    }

    public PlanSupplie mapToEntity(final PlanSupplieDTO planSupplieDTO,
            final PlanSupplie planSupplie) {
        planSupplie.setCode(planSupplieDTO.getCode());
        planSupplie.setName(planSupplieDTO.getName());
        planSupplie.setType(planSupplieDTO.getType());
        planSupplie.setPlanNumber(planSupplieDTO.getPlanNumber());
        planSupplie.setDescription(planSupplieDTO.getDescription());
        planSupplie.setUserPerformer(planSupplieDTO.getUserPerformer());
        planSupplie.setFromDate(planSupplieDTO.getFromDate());
        planSupplie.setToDate(planSupplieDTO.getToDate());
        planSupplie.setCreatedAt(planSupplieDTO.getCreatedAt());
        planSupplie.setUpdatedAt(planSupplieDTO.getUpdatedAt());
        planSupplie.setCreatedBy(planSupplieDTO.getCreatedBy());
        planSupplie.setUpdatedBy(planSupplieDTO.getUpdatedBy());
        planSupplie.setStatus(planSupplieDTO.getStatus());

        final Factory factory = planSupplieDTO.getFactory() == null ? null : factoryRepository.findById(planSupplieDTO.getFactory().getId())
                .orElseThrow(() -> new NotFoundException("factory not found"));
        planSupplie.setFactory(factory);

        final Branch branch = planSupplieDTO.getBranch() == null ? null : branchRepository.findById(planSupplieDTO.getBranch().getId())
                .orElseThrow(() -> new NotFoundException("branch not found"));
        planSupplie.setBranch(branch);

        final Team team = planSupplieDTO.getTeam() == null ? null : teamRepository.findById(planSupplieDTO.getTeam().getId())
                .orElseThrow(() -> new NotFoundException("team not found"));
        planSupplie.setTeam(team);

        final ApprovalWorkflow approvalWorkflow = planSupplieDTO.getApprovalWorkflow() == null ? null :
                approvalWorkflowRepository.findById(planSupplieDTO.getApprovalWorkflow().getId())
                        .orElseThrow(() -> new NotFoundException("approvalWorkflow not found"));
        planSupplie.setApprovalWorkflow(approvalWorkflow);
        return planSupplie;
    }

}

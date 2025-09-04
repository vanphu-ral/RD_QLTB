package io.qltb.qltb.service;

import io.qltb.qltb.domain.Branch;
import io.qltb.qltb.domain.Factory;
import io.qltb.qltb.events.BeforeDeleteBranch;
import io.qltb.qltb.events.BeforeDeleteFactory;
import io.qltb.qltb.model.BranchDTO;
import io.qltb.qltb.repos.BranchRepository;
import io.qltb.qltb.repos.FactoryRepository;
import io.qltb.qltb.util.NotFoundException;
import io.qltb.qltb.util.ReferencedException;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class BranchService {

    private final BranchRepository branchRepository;
    private final FactoryRepository factoryRepository;
    private final ApplicationEventPublisher publisher;

    public BranchService(final BranchRepository branchRepository,
            final FactoryRepository factoryRepository, final ApplicationEventPublisher publisher) {
        this.branchRepository = branchRepository;
        this.factoryRepository = factoryRepository;
        this.publisher = publisher;
    }
    @Transactional
    public List<BranchDTO> findAll() {
        final List<Branch> branches = branchRepository.findAll(Sort.by("id"));
        return branches.stream()
                .map(branch -> mapToDTO(branch, new BranchDTO()))
                .toList();
    }

    public BranchDTO get(final Long id) {
        return branchRepository.findById(id)
                .map(branch -> mapToDTO(branch, new BranchDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final BranchDTO branchDTO) {
        final Branch branch = new Branch();
        mapToEntity(branchDTO, branch);
        return branchRepository.save(branch).getId();
    }

    public void update(final Long id, final BranchDTO branchDTO) {
        final Branch branch = branchRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(branchDTO, branch);
        branchRepository.save(branch);
    }

    public void delete(final Long id) {
        final Branch branch = branchRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteBranch(id));
        branchRepository.delete(branch);
    }

    private BranchDTO mapToDTO(final Branch branch, final BranchDTO branchDTO) {
        branch.getFactory().setFactoryBranches(null);
        branchDTO.setId(branch.getId());
        branchDTO.setCode(branch.getCode());
        branchDTO.setName(branch.getName());
        branchDTO.setDescription(branch.getDescription());
        branchDTO.setCreatedAt(branch.getCreatedAt());
        branchDTO.setUpdatedAt(branch.getUpdatedAt());
        branchDTO.setCreatedBy(branch.getCreatedBy());
        branchDTO.setUpdatedBy(branch.getUpdatedBy());
        branchDTO.setStatus(branch.getStatus());
        branchDTO.setFactory(branch.getFactory() == null ? null : branch.getFactory());
        return branchDTO;
    }

    private Branch mapToEntity(final BranchDTO branchDTO, final Branch branch) {
        branch.setCode(branchDTO.getCode());
        branch.setName(branchDTO.getName());
        branch.setDescription(branchDTO.getDescription());
        branch.setCreatedAt(branchDTO.getCreatedAt());
        branch.setUpdatedAt(branchDTO.getUpdatedAt());
        branch.setCreatedBy(branchDTO.getCreatedBy());
        branch.setUpdatedBy(branchDTO.getUpdatedBy());
        branch.setStatus(branchDTO.getStatus());
        final Factory factory = branchDTO.getFactory() == null ? null : factoryRepository.findById(branchDTO.getFactory().getId())
                .orElseThrow(() -> new NotFoundException("factory not found"));
        branch.setFactory(factory);
        return branch;
    }

    public boolean codeExists(final String code) {
        return branchRepository.existsByCodeIgnoreCase(code);
    }

    @EventListener(BeforeDeleteFactory.class)
    public void on(final BeforeDeleteFactory event) {
        final ReferencedException referencedException = new ReferencedException();
        final Branch factoryBranch = branchRepository.findFirstByFactoryId(event.getId());
        if (factoryBranch != null) {
            referencedException.setKey("factory.branch.factory.referenced");
            referencedException.addParam(factoryBranch.getId());
            throw referencedException;
        }
    }

}

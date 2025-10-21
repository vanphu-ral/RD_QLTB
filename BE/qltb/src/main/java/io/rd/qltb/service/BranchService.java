package io.rd.qltb.service;

import io.rd.qltb.domain.Branch;
import io.rd.qltb.domain.Factory;
import io.rd.qltb.events.BeforeDeleteBranch;
import io.rd.qltb.events.BeforeDeleteFactory;
import io.rd.qltb.model.BranchDTO;
import io.rd.qltb.repos.BranchRepository;
import io.rd.qltb.repos.FactoryRepository;
import io.rd.qltb.util.NotFoundException;
import io.rd.qltb.util.ReferencedException;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


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

    public List<BranchDTO> findAll() {
        final List<Branch> branches = branchRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
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
        branchDTO.setId(branch.getId());
        branchDTO.setCode(branch.getCode());
        branchDTO.setName(branch.getName());
        branchDTO.setDescription(branch.getDescription());
        branchDTO.setManager(branch.getManager());
        branchDTO.setCreatedAt(branch.getCreatedAt());
        branchDTO.setUpdatedAt(branch.getUpdatedAt());
        branchDTO.setCreatedBy(branch.getCreatedBy());
        branchDTO.setUpdatedBy(branch.getUpdatedBy());
        branchDTO.setStatus(branch.getStatus());

        // Sao chép Factory có kiểm soát
        if (branch.getFactory() != null) {
            Factory factoryCopy = new Factory();
            factoryCopy.setId(branch.getFactory().getId());
            factoryCopy.setCode(branch.getFactory().getCode());
            factoryCopy.setName(branch.getFactory().getName());
            factoryCopy.setStatus(branch.getFactory().getStatus());
            factoryCopy.setCreatedAt(branch.getFactory().getCreatedAt());
            factoryCopy.setUpdatedAt(branch.getFactory().getUpdatedAt());
            factoryCopy.setCreatedBy(branch.getFactory().getCreatedBy());
            factoryCopy.setUpdatedBy(branch.getFactory().getUpdatedBy());

            // Xóa các quan hệ con để tránh vòng lặp
            factoryCopy.setFactoryBranches(null);

            branchDTO.setFactory(factoryCopy);
        } else {
            branchDTO.setFactory(null);
        }

        return branchDTO;
    }


    private Branch mapToEntity(final BranchDTO branchDTO, final Branch branch) {
        branch.setCode(branchDTO.getCode());
        branch.setName(branchDTO.getName());
        branch.setDescription(branchDTO.getDescription());
        branch.setManager(branchDTO.getManager());
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

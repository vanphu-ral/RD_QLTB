package rd.project.qltb.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import rd.project.qltb.domain.Branch;
import rd.project.qltb.domain.DayOff;
import rd.project.qltb.domain.Device;
import rd.project.qltb.domain.Factory;
import rd.project.qltb.domain.Form;
import rd.project.qltb.domain.PlanTarget;
import rd.project.qltb.domain.Team;
import rd.project.qltb.model.BranchDTO;
import rd.project.qltb.repos.BranchRepository;
import rd.project.qltb.repos.DayOffRepository;
import rd.project.qltb.repos.DeviceRepository;
import rd.project.qltb.repos.FactoryRepository;
import rd.project.qltb.repos.FormRepository;
import rd.project.qltb.repos.PlanTargetRepository;
import rd.project.qltb.repos.TeamRepository;
import rd.project.qltb.util.NotFoundException;
import rd.project.qltb.util.ReferencedWarning;


@Service
public class BranchService {

    private final BranchRepository branchRepository;
    private final FactoryRepository factoryRepository;
    private final TeamRepository teamRepository;
    private final DeviceRepository deviceRepository;
    private final DayOffRepository dayOffRepository;
    private final PlanTargetRepository planTargetRepository;
    private final FormRepository formRepository;

    public BranchService(final BranchRepository branchRepository,
            final FactoryRepository factoryRepository, final TeamRepository teamRepository,
            final DeviceRepository deviceRepository, final DayOffRepository dayOffRepository,
            final PlanTargetRepository planTargetRepository, final FormRepository formRepository) {
        this.branchRepository = branchRepository;
        this.factoryRepository = factoryRepository;
        this.teamRepository = teamRepository;
        this.deviceRepository = deviceRepository;
        this.dayOffRepository = dayOffRepository;
        this.planTargetRepository = planTargetRepository;
        this.formRepository = formRepository;
    }

    public List<BranchDTO> findAll() {
        final List<Branch> branches = branchRepository.findAll(Sort.by("id"));
        return branches.stream()
                .map(branch -> mapToDTO(branch, new BranchDTO()))
                .toList();
    }

    public BranchDTO get(final Integer id) {
        return branchRepository.findById(id)
                .map(branch -> mapToDTO(branch, new BranchDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final BranchDTO branchDTO) {
        final Branch branch = new Branch();
        mapToEntity(branchDTO, branch);
        return branchRepository.save(branch).getId();
    }

    public void update(final Integer id, final BranchDTO branchDTO) {
        final Branch branch = branchRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(branchDTO, branch);
        branchRepository.save(branch);
    }

    public void delete(final Integer id) {
        branchRepository.deleteById(id);
    }

    private BranchDTO mapToDTO(final Branch branch, final BranchDTO branchDTO) {
        branchDTO.setId(branch.getId());
        branchDTO.setCode(branch.getCode());
        branchDTO.setName(branch.getName());
        branchDTO.setDescription(branch.getDescription());
        branchDTO.setCreatedAt(branch.getCreatedAt());
        branchDTO.setUpdatedAt(branch.getUpdatedAt());
        branchDTO.setCreatedBy(branch.getCreatedBy());
        branchDTO.setFactory(branch.getFactory() == null ? null : branch.getFactory().getId());
        return branchDTO;
    }

    private Branch mapToEntity(final BranchDTO branchDTO, final Branch branch) {
        branch.setCode(branchDTO.getCode());
        branch.setName(branchDTO.getName());
        branch.setDescription(branchDTO.getDescription());
        branch.setCreatedAt(branchDTO.getCreatedAt());
        branch.setUpdatedAt(branchDTO.getUpdatedAt());
        branch.setCreatedBy(branchDTO.getCreatedBy());
        final Factory factory = branchDTO.getFactory() == null ? null : factoryRepository.findById(branchDTO.getFactory())
                .orElseThrow(() -> new NotFoundException("factory not found"));
        branch.setFactory(factory);
        return branch;
    }

    public ReferencedWarning getReferencedWarning(final Integer id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Branch branch = branchRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Team branchTeam = teamRepository.findFirstByBranch(branch);
        if (branchTeam != null) {
            referencedWarning.setKey("branch.team.branch.referenced");
            referencedWarning.addParam(branchTeam.getId());
            return referencedWarning;
        }
        final Device branchDevice = deviceRepository.findFirstByBranch(branch);
        if (branchDevice != null) {
            referencedWarning.setKey("branch.device.branch.referenced");
            referencedWarning.addParam(branchDevice.getId());
            return referencedWarning;
        }
        final DayOff branchDayOff = dayOffRepository.findFirstByBranch(branch);
        if (branchDayOff != null) {
            referencedWarning.setKey("branch.dayOff.branch.referenced");
            referencedWarning.addParam(branchDayOff.getId());
            return referencedWarning;
        }
        final PlanTarget branchPlanTarget = planTargetRepository.findFirstByBranch(branch);
        if (branchPlanTarget != null) {
            referencedWarning.setKey("branch.planTarget.branch.referenced");
            referencedWarning.addParam(branchPlanTarget.getId());
            return referencedWarning;
        }
        final Form branchForm = formRepository.findFirstByBranch(branch);
        if (branchForm != null) {
            referencedWarning.setKey("branch.form.branch.referenced");
            referencedWarning.addParam(branchForm.getId());
            return referencedWarning;
        }
        return null;
    }

}

package io.qltb.qltb.service;

import io.qltb.qltb.domain.Branch;
import io.qltb.qltb.domain.DayOff;
import io.qltb.qltb.domain.Team;
import io.qltb.qltb.events.BeforeDeleteBranch;
import io.qltb.qltb.events.BeforeDeleteTeam;
import io.qltb.qltb.model.DayOffDTO;
import io.qltb.qltb.repos.BranchRepository;
import io.qltb.qltb.repos.DayOffRepository;
import io.qltb.qltb.repos.TeamRepository;
import io.qltb.qltb.util.NotFoundException;
import io.qltb.qltb.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class DayOffService {

    private final DayOffRepository dayOffRepository;
    private final BranchRepository branchRepository;
    private final TeamRepository teamRepository;

    public DayOffService(final DayOffRepository dayOffRepository,
            final BranchRepository branchRepository, final TeamRepository teamRepository) {
        this.dayOffRepository = dayOffRepository;
        this.branchRepository = branchRepository;
        this.teamRepository = teamRepository;
    }

    public List<DayOffDTO> findAll() {
        final List<DayOff> dayOffs = dayOffRepository.findAll(Sort.by("id"));
        return dayOffs.stream()
                .map(dayOff -> mapToDTO(dayOff, new DayOffDTO()))
                .toList();
    }

    public DayOffDTO get(final Long id) {
        return dayOffRepository.findById(id)
                .map(dayOff -> mapToDTO(dayOff, new DayOffDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final DayOffDTO dayOffDTO) {
        final DayOff dayOff = new DayOff();
        mapToEntity(dayOffDTO, dayOff);
        return dayOffRepository.save(dayOff).getId();
    }

    public void update(final Long id, final DayOffDTO dayOffDTO) {
        final DayOff dayOff = dayOffRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(dayOffDTO, dayOff);
        dayOffRepository.save(dayOff);
    }

    public void delete(final Long id) {
        final DayOff dayOff = dayOffRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        dayOffRepository.delete(dayOff);
    }

    private DayOffDTO mapToDTO(final DayOff dayOff, final DayOffDTO dayOffDTO) {
        dayOffDTO.setId(dayOff.getId());
        dayOffDTO.setCode(dayOff.getCode());
        dayOffDTO.setName(dayOff.getName());
        dayOffDTO.setDay(dayOff.getDay());
        dayOffDTO.setFrequency(dayOff.getFrequency());
        dayOffDTO.setFromDate(dayOff.getFromDate());
        dayOffDTO.setToDate(dayOff.getToDate());
        dayOffDTO.setDescription(dayOff.getDescription());
        dayOffDTO.setCreatedAt(dayOff.getCreatedAt());
        dayOffDTO.setUpdatedAt(dayOff.getUpdatedAt());
        dayOffDTO.setCreatedBy(dayOff.getCreatedBy());
        dayOffDTO.setUpdatedBy(dayOff.getUpdatedBy());
        dayOffDTO.setStatus(dayOff.getStatus());
        dayOffDTO.setBranch(dayOff.getBranch() == null ? null : dayOff.getBranch().getId());
        dayOffDTO.setTeam(dayOff.getTeam() == null ? null : dayOff.getTeam().getId());
        return dayOffDTO;
    }

    private DayOff mapToEntity(final DayOffDTO dayOffDTO, final DayOff dayOff) {
        dayOff.setCode(dayOffDTO.getCode());
        dayOff.setName(dayOffDTO.getName());
        dayOff.setDay(dayOffDTO.getDay());
        dayOff.setFrequency(dayOffDTO.getFrequency());
        dayOff.setFromDate(dayOffDTO.getFromDate());
        dayOff.setToDate(dayOffDTO.getToDate());
        dayOff.setDescription(dayOffDTO.getDescription());
        dayOff.setCreatedAt(dayOffDTO.getCreatedAt());
        dayOff.setUpdatedAt(dayOffDTO.getUpdatedAt());
        dayOff.setCreatedBy(dayOffDTO.getCreatedBy());
        dayOff.setUpdatedBy(dayOffDTO.getUpdatedBy());
        dayOff.setStatus(dayOffDTO.getStatus());
        final Branch branch = dayOffDTO.getBranch() == null ? null : branchRepository.findById(dayOffDTO.getBranch())
                .orElseThrow(() -> new NotFoundException("branch not found"));
        dayOff.setBranch(branch);
        final Team team = dayOffDTO.getTeam() == null ? null : teamRepository.findById(dayOffDTO.getTeam())
                .orElseThrow(() -> new NotFoundException("team not found"));
        dayOff.setTeam(team);
        return dayOff;
    }

    @EventListener(BeforeDeleteBranch.class)
    public void on(final BeforeDeleteBranch event) {
        final ReferencedException referencedException = new ReferencedException();
        final DayOff branchDayOff = dayOffRepository.findFirstByBranchId(event.getId());
        if (branchDayOff != null) {
            referencedException.setKey("branch.dayOff.branch.referenced");
            referencedException.addParam(branchDayOff.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteTeam.class)
    public void on(final BeforeDeleteTeam event) {
        final ReferencedException referencedException = new ReferencedException();
        final DayOff teamDayOff = dayOffRepository.findFirstByTeamId(event.getId());
        if (teamDayOff != null) {
            referencedException.setKey("team.dayOff.team.referenced");
            referencedException.addParam(teamDayOff.getId());
            throw referencedException;
        }
    }

}

package io.rd.qltb.service;

import io.rd.qltb.domain.Branch;
import io.rd.qltb.domain.Team;
import io.rd.qltb.events.BeforeDeleteBranch;
import io.rd.qltb.events.BeforeDeleteTeam;
import io.rd.qltb.model.TeamDTO;
import io.rd.qltb.repos.BranchRepository;
import io.rd.qltb.repos.TeamRepository;
import io.rd.qltb.util.NotFoundException;
import io.rd.qltb.util.ReferencedException;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final BranchRepository branchRepository;
    private final ApplicationEventPublisher publisher;

    public TeamService(final TeamRepository teamRepository, final BranchRepository branchRepository,
            final ApplicationEventPublisher publisher) {
        this.teamRepository = teamRepository;
        this.branchRepository = branchRepository;
        this.publisher = publisher;
    }

    public List<TeamDTO> findAll() {
        final List<Team> teams = teamRepository.findAll(Sort.by("id"));
        return teams.stream()
                .map(team -> mapToDTO(team, new TeamDTO()))
                .toList();
    }

    public TeamDTO get(final Integer id) {
        return teamRepository.findById(id)
                .map(team -> mapToDTO(team, new TeamDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final TeamDTO teamDTO) {
        final Team team = new Team();
        mapToEntity(teamDTO, team);
        return teamRepository.save(team).getId();
    }

    public void update(final Integer id, final TeamDTO teamDTO) {
        final Team team = teamRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(teamDTO, team);
        teamRepository.save(team);
    }

    public void delete(final Integer id) {
        final Team team = teamRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteTeam(id));
        teamRepository.delete(team);
    }

    private TeamDTO mapToDTO(final Team team, final TeamDTO dto) {
        dto.setId(team.getId());
        dto.setCode(team.getCode());
        dto.setName(team.getName());
        dto.setDescription(team.getDescription());
        dto.setManager(team.getManager());
        dto.setCreatedAt(team.getCreatedAt());
        dto.setUpdatedAt(team.getUpdatedAt());
        dto.setCreatedBy(team.getCreatedBy());
        dto.setUpdatedBy(team.getUpdatedBy());
        dto.setStatus(team.getStatus());

        // Sao chép Branch có kiểm soát
        if (team.getBranch() != null) {
            Branch branchCopy = new Branch();
            branchCopy.setId(team.getBranch().getId());
            branchCopy.setCode(team.getBranch().getCode());
            branchCopy.setName(team.getBranch().getName());
            branchCopy.setDescription(team.getBranch().getDescription());
            branchCopy.setCreatedAt(team.getBranch().getCreatedAt());
            branchCopy.setUpdatedAt(team.getBranch().getUpdatedAt());
            branchCopy.setCreatedBy(team.getBranch().getCreatedBy());
            branchCopy.setUpdatedBy(team.getBranch().getUpdatedBy());
            branchCopy.setStatus(team.getBranch().getStatus());

            // Xóa các quan hệ con để tránh vòng lặp
            branchCopy.setBranchTeams(null);
            branchCopy.setBranchDevices(null);
            branchCopy.setFactory(null);

            dto.setBranch(branchCopy);
        } else {
            dto.setBranch(null);
        }

        return dto;
    }


    private Team mapToEntity(final TeamDTO teamDTO, final Team team) {
        team.setCode(teamDTO.getCode());
        team.setName(teamDTO.getName());
        team.setDescription(teamDTO.getDescription());
        team.setManager(teamDTO.getManager());
        team.setCreatedAt(teamDTO.getCreatedAt());
        team.setUpdatedAt(teamDTO.getUpdatedAt());
        team.setCreatedBy(teamDTO.getCreatedBy());
        team.setUpdatedBy(teamDTO.getUpdatedBy());
        team.setStatus(teamDTO.getStatus());
        final Branch branch = teamDTO.getBranch() == null ? null : branchRepository.findById(teamDTO.getBranch())
                .orElseThrow(() -> new NotFoundException("branch not found"));
        team.setBranch(branch);
        return team;
    }

    @EventListener(BeforeDeleteBranch.class)
    public void on(final BeforeDeleteBranch event) {
        final ReferencedException referencedException = new ReferencedException();
        final Team branchTeam = teamRepository.findFirstByBranchId(event.getId());
        if (branchTeam != null) {
            referencedException.setKey("branch.team.branch.referenced");
            referencedException.addParam(branchTeam.getId());
            throw referencedException;
        }
    }

}

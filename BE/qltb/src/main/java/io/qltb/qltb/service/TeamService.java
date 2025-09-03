package io.qltb.qltb.service;

import io.qltb.qltb.domain.Branch;
import io.qltb.qltb.domain.Team;
import io.qltb.qltb.events.BeforeDeleteBranch;
import io.qltb.qltb.events.BeforeDeleteTeam;
import io.qltb.qltb.model.TeamDTO;
import io.qltb.qltb.repos.BranchRepository;
import io.qltb.qltb.repos.TeamRepository;
import io.qltb.qltb.util.NotFoundException;
import io.qltb.qltb.util.ReferencedException;
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

    public TeamDTO get(final Long id) {
        return teamRepository.findById(id)
                .map(team -> mapToDTO(team, new TeamDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final TeamDTO teamDTO) {
        final Team team = new Team();
        mapToEntity(teamDTO, team);
        return teamRepository.save(team).getId();
    }

    public void update(final Long id, final TeamDTO teamDTO) {
        final Team team = teamRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(teamDTO, team);
        teamRepository.save(team);
    }

    public void delete(final Long id) {
        final Team team = teamRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteTeam(id));
        teamRepository.delete(team);
    }

    private TeamDTO mapToDTO(final Team team, final TeamDTO teamDTO) {
        teamDTO.setId(team.getId());
        teamDTO.setCode(team.getCode());
        teamDTO.setName(team.getName());
        teamDTO.setDescription(team.getDescription());
        teamDTO.setCreatedAt(team.getCreatedAt());
        teamDTO.setUpdatedAt(team.getUpdatedAt());
        teamDTO.setCreatedBy(team.getCreatedBy());
        teamDTO.setUpdatedBy(team.getUpdatedBy());
        teamDTO.setStatus(team.getStatus());
        teamDTO.setBranch(team.getBranch() == null ? null : team.getBranch().getId());
        return teamDTO;
    }

    private Team mapToEntity(final TeamDTO teamDTO, final Team team) {
        team.setCode(teamDTO.getCode());
        team.setName(teamDTO.getName());
        team.setDescription(teamDTO.getDescription());
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

    public boolean codeExists(final String code) {
        return teamRepository.existsByCodeIgnoreCase(code);
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

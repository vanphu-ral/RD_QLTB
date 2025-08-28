package rd.project.qltb.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import rd.project.qltb.domain.Branch;
import rd.project.qltb.domain.DayOff;
import rd.project.qltb.domain.Form;
import rd.project.qltb.domain.Line;
import rd.project.qltb.domain.Team;
import rd.project.qltb.model.BranchDTO;
import rd.project.qltb.model.TeamDTO;
import rd.project.qltb.repos.BranchRepository;
import rd.project.qltb.repos.DayOffRepository;
import rd.project.qltb.repos.FormRepository;
import rd.project.qltb.repos.LineRepository;
import rd.project.qltb.repos.TeamRepository;
import rd.project.qltb.util.NotFoundException;
import rd.project.qltb.util.ReferencedWarning;


@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final BranchRepository branchRepository;
    private final LineRepository lineRepository;
    private final DayOffRepository dayOffRepository;
    private final FormRepository formRepository;

    public TeamService(final TeamRepository teamRepository, final BranchRepository branchRepository,
            final LineRepository lineRepository, final DayOffRepository dayOffRepository,
            final FormRepository formRepository) {
        this.teamRepository = teamRepository;
        this.branchRepository = branchRepository;
        this.lineRepository = lineRepository;
        this.dayOffRepository = dayOffRepository;
        this.formRepository = formRepository;
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
        teamRepository.deleteById(id);
    }

    private TeamDTO mapToDTO(final Team team, final TeamDTO teamDTO) {
        teamDTO.setId(team.getId());
        teamDTO.setCode(team.getCode());
        teamDTO.setName(team.getName());
        teamDTO.setDescription(team.getDescription());
        teamDTO.setCreatedAt(team.getCreatedAt());
        teamDTO.setUpdatedAt(team.getUpdatedAt());
        teamDTO.setCreatedBy(team.getCreatedBy());
        if (team.getBranch() != null) {
            BranchDTO branchDTO = new BranchDTO();
            branchDTO.setId(team.getBranch().getId());
            branchDTO.setName(team.getBranch().getName());
            teamDTO.setBranch(branchDTO);
        } else {
            teamDTO.setBranch(null);
        }
        return teamDTO;
    }

    private Team mapToEntity(final TeamDTO teamDTO, final Team team) {
        team.setCode(teamDTO.getCode());
        team.setName(teamDTO.getName());
        team.setDescription(teamDTO.getDescription());
        team.setCreatedAt(teamDTO.getCreatedAt());
        team.setUpdatedAt(teamDTO.getUpdatedAt());
        team.setCreatedBy(teamDTO.getCreatedBy());
        final Branch branch = teamDTO.getBranch() == null ? null : branchRepository.findById(teamDTO.getBranch().getId())
                .orElseThrow(() -> new NotFoundException("branch not found"));
        team.setBranch(branch);
        return team;
    }

    public ReferencedWarning getReferencedWarning(final Integer id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Team team = teamRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Line teamLine = lineRepository.findFirstByTeam(team);
        if (teamLine != null) {
            referencedWarning.setKey("team.line.team.referenced");
            referencedWarning.addParam(teamLine.getId());
            return referencedWarning;
        }
        final DayOff teamDayOff = dayOffRepository.findFirstByTeam(team);
        if (teamDayOff != null) {
            referencedWarning.setKey("team.dayOff.team.referenced");
            referencedWarning.addParam(teamDayOff.getId());
            return referencedWarning;
        }
        final Form teamForm = formRepository.findFirstByTeam(team);
        if (teamForm != null) {
            referencedWarning.setKey("team.form.team.referenced");
            referencedWarning.addParam(teamForm.getId());
            return referencedWarning;
        }
        return null;
    }

}

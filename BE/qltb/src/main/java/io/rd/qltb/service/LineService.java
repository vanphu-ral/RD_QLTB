package io.rd.qltb.service;

import io.rd.qltb.domain.Line;
import io.rd.qltb.domain.Team;
import io.rd.qltb.events.BeforeDeleteLine;
import io.rd.qltb.events.BeforeDeleteTeam;
import io.rd.qltb.model.LineDTO;
import io.rd.qltb.repos.LineRepository;
import io.rd.qltb.repos.TeamRepository;
import io.rd.qltb.util.NotFoundException;
import io.rd.qltb.util.ReferencedException;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import static io.rd.qltb.config.GlobalConfig.DELETED;


@Service
public class LineService {

    private final LineRepository lineRepository;
    private final TeamRepository teamRepository;
    private final ApplicationEventPublisher publisher;

    public LineService(final LineRepository lineRepository, final TeamRepository teamRepository,
            final ApplicationEventPublisher publisher) {
        this.lineRepository = lineRepository;
        this.teamRepository = teamRepository;
        this.publisher = publisher;
    }

    public List<LineDTO> findAll() {
        final List<Line> lines = lineRepository.findByStatusNotOrderByIdDesc(DELETED);
        return lines.stream()
                .map(line -> mapToDTO(line, new LineDTO()))
                .toList();
    }

    public LineDTO get(final Long id) {
        return lineRepository.findById(id)
                .map(line -> mapToDTO(line, new LineDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final LineDTO lineDTO) {
        final Line line = new Line();
        mapToEntity(lineDTO, line);
        return lineRepository.save(line).getId();
    }

    public void update(final Long id, final LineDTO lineDTO) {
        final Line line = lineRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(lineDTO, line);
        lineRepository.save(line);
    }

    public void delete(final Long id) {
        final Line line = lineRepository.findById(id)
                .orElseThrow(NotFoundException::new);
//        publisher.publishEvent(new BeforeDeleteLine(id));
        line.setStatus(DELETED);
        lineRepository.save(line);
    }

    private LineDTO mapToDTO(final Line line, final LineDTO lineDTO) {
        lineDTO.setId(line.getId());
        lineDTO.setCode(line.getCode());
        lineDTO.setName(line.getName());
        lineDTO.setDescription(line.getDescription());
        lineDTO.setManager(line.getManager());
        lineDTO.setCreatedAt(line.getCreatedAt());
        lineDTO.setUpdatedAt(line.getUpdatedAt());
        lineDTO.setCreatedBy(line.getCreatedBy());
        lineDTO.setUpdatedBy(line.getUpdatedBy());
        lineDTO.setStatus(line.getStatus());

        // Sao chép Team có kiểm soát
        if (line.getTeam() != null) {
            Team teamCopy = new Team();
            teamCopy.setId(line.getTeam().getId());
            teamCopy.setCode(line.getTeam().getCode());
            teamCopy.setName(line.getTeam().getName());
            teamCopy.setDescription(line.getTeam().getDescription());
            teamCopy.setManager(line.getTeam().getManager());
            teamCopy.setCreatedAt(line.getTeam().getCreatedAt());
            teamCopy.setUpdatedAt(line.getTeam().getUpdatedAt());
            teamCopy.setCreatedBy(line.getTeam().getCreatedBy());
            teamCopy.setUpdatedBy(line.getTeam().getUpdatedBy());
            teamCopy.setStatus(line.getTeam().getStatus());

            // Xóa các quan hệ con để tránh vòng lặp
            teamCopy.setBranch(null);
            teamCopy.setTeamLines(null);
            teamCopy.setTeamDevices(null);

            lineDTO.setTeam(teamCopy);
        } else {
            lineDTO.setTeam(null);
        }

        return lineDTO;
    }


    private Line mapToEntity(final LineDTO lineDTO, final Line line) {
        line.setCode(lineDTO.getCode());
        line.setName(lineDTO.getName());
        line.setDescription(lineDTO.getDescription());
        line.setManager(lineDTO.getManager());
        line.setCreatedAt(lineDTO.getCreatedAt());
        line.setUpdatedAt(lineDTO.getUpdatedAt());
        line.setCreatedBy(lineDTO.getCreatedBy());
        line.setUpdatedBy(lineDTO.getUpdatedBy());
        line.setStatus(lineDTO.getStatus());
        final Team team = lineDTO.getTeam() == null ? null : teamRepository.findById(lineDTO.getTeam().getId())
                .orElseThrow(() -> new NotFoundException("team not found"));
        line.setTeam(team);
        return line;
    }

    @EventListener(BeforeDeleteTeam.class)
    public void on(final BeforeDeleteTeam event) {
        final ReferencedException referencedException = new ReferencedException();
        final Line teamLine = lineRepository.findFirstByTeamId(event.getId());
        if (teamLine != null) {
            referencedException.setKey("team.line.team.referenced");
            referencedException.addParam(teamLine.getId());
            throw referencedException;
        }
    }

}

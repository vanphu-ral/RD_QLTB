package rd.project.qltb.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import rd.project.qltb.domain.Device;
import rd.project.qltb.domain.Form;
import rd.project.qltb.domain.Line;
import rd.project.qltb.domain.Team;
import rd.project.qltb.model.LineDTO;
import rd.project.qltb.repos.DeviceRepository;
import rd.project.qltb.repos.FormRepository;
import rd.project.qltb.repos.LineRepository;
import rd.project.qltb.repos.TeamRepository;
import rd.project.qltb.util.NotFoundException;
import rd.project.qltb.util.ReferencedWarning;


@Service
public class LineService {

    private final LineRepository lineRepository;
    private final TeamRepository teamRepository;
    private final DeviceRepository deviceRepository;
    private final FormRepository formRepository;

    public LineService(final LineRepository lineRepository, final TeamRepository teamRepository,
            final DeviceRepository deviceRepository, final FormRepository formRepository) {
        this.lineRepository = lineRepository;
        this.teamRepository = teamRepository;
        this.deviceRepository = deviceRepository;
        this.formRepository = formRepository;
    }

    public List<LineDTO> findAll() {
        final List<Line> lines = lineRepository.findAll(Sort.by("id"));
        return lines.stream()
                .map(line -> mapToDTO(line, new LineDTO()))
                .toList();
    }

    public LineDTO get(final Integer id) {
        return lineRepository.findById(id)
                .map(line -> mapToDTO(line, new LineDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final LineDTO lineDTO) {
        final Line line = new Line();
        mapToEntity(lineDTO, line);
        return lineRepository.save(line).getId();
    }

    public void update(final Integer id, final LineDTO lineDTO) {
        final Line line = lineRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(lineDTO, line);
        lineRepository.save(line);
    }

    public void delete(final Integer id) {
        lineRepository.deleteById(id);
    }

    private LineDTO mapToDTO(final Line line, final LineDTO lineDTO) {
        lineDTO.setId(line.getId());
        lineDTO.setCode(line.getCode());
        lineDTO.setName(line.getName());
        lineDTO.setDescription(line.getDescription());
        lineDTO.setCreatedAt(line.getCreatedAt());
        lineDTO.setUpdatedAt(line.getUpdatedAt());
        lineDTO.setCreatedBy(line.getCreatedBy());
        lineDTO.setTeam(line.getTeam() == null ? null : line.getTeam().getId());
        return lineDTO;
    }

    private Line mapToEntity(final LineDTO lineDTO, final Line line) {
        line.setCode(lineDTO.getCode());
        line.setName(lineDTO.getName());
        line.setDescription(lineDTO.getDescription());
        line.setCreatedAt(lineDTO.getCreatedAt());
        line.setUpdatedAt(lineDTO.getUpdatedAt());
        line.setCreatedBy(lineDTO.getCreatedBy());
        final Team team = lineDTO.getTeam() == null ? null : teamRepository.findById(lineDTO.getTeam())
                .orElseThrow(() -> new NotFoundException("team not found"));
        line.setTeam(team);
        return line;
    }

    public ReferencedWarning getReferencedWarning(final Integer id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Line line = lineRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Device lineDevice = deviceRepository.findFirstByLine(line);
        if (lineDevice != null) {
            referencedWarning.setKey("line.device.line.referenced");
            referencedWarning.addParam(lineDevice.getId());
            return referencedWarning;
        }
        final Form lineForm = formRepository.findFirstByLine(line);
        if (lineForm != null) {
            referencedWarning.setKey("line.form.line.referenced");
            referencedWarning.addParam(lineForm.getId());
            return referencedWarning;
        }
        return null;
    }

}

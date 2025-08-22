package rd.project.qltb.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import rd.project.qltb.domain.Branch;
import rd.project.qltb.domain.Factory;
import rd.project.qltb.domain.Form;
import rd.project.qltb.domain.Line;
import rd.project.qltb.domain.Team;
import rd.project.qltb.model.FormDTO;
import rd.project.qltb.repos.BranchRepository;
import rd.project.qltb.repos.FactoryRepository;
import rd.project.qltb.repos.FormRepository;
import rd.project.qltb.repos.LineRepository;
import rd.project.qltb.repos.TeamRepository;
import rd.project.qltb.util.NotFoundException;


@Service
public class FormService {

    private final FormRepository formRepository;
    private final FactoryRepository factoryRepository;
    private final BranchRepository branchRepository;
    private final TeamRepository teamRepository;
    private final LineRepository lineRepository;

    public FormService(final FormRepository formRepository,
            final FactoryRepository factoryRepository, final BranchRepository branchRepository,
            final TeamRepository teamRepository, final LineRepository lineRepository) {
        this.formRepository = formRepository;
        this.factoryRepository = factoryRepository;
        this.branchRepository = branchRepository;
        this.teamRepository = teamRepository;
        this.lineRepository = lineRepository;
    }

    public List<FormDTO> findAll() {
        final List<Form> forms = formRepository.findAll(Sort.by("id"));
        return forms.stream()
                .map(form -> mapToDTO(form, new FormDTO()))
                .toList();
    }

    public FormDTO get(final Long id) {
        return formRepository.findById(id)
                .map(form -> mapToDTO(form, new FormDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final FormDTO formDTO) {
        final Form form = new Form();
        mapToEntity(formDTO, form);
        return formRepository.save(form).getId();
    }

    public void update(final Long id, final FormDTO formDTO) {
        final Form form = formRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(formDTO, form);
        formRepository.save(form);
    }

    public void delete(final Long id) {
        formRepository.deleteById(id);
    }

    private FormDTO mapToDTO(final Form form, final FormDTO formDTO) {
        formDTO.setId(form.getId());
        formDTO.setCode(form.getCode());
        formDTO.setName(form.getName());
        formDTO.setDescription(form.getDescription());
        formDTO.setFileName(form.getFileName());
        formDTO.setFilePath(form.getFilePath());
        formDTO.setTimeCreated(form.getTimeCreated());
        formDTO.setTimeModified(form.getTimeModified());
        formDTO.setPublishDate(form.getPublishDate());
        formDTO.setPublishNumber(form.getPublishNumber());
        formDTO.setAppovedUser(form.getAppovedUser());
        formDTO.setCreatedAt(form.getCreatedAt());
        formDTO.setUpdatedAt(form.getUpdatedAt());
        formDTO.setCreatedBy(form.getCreatedBy());
        formDTO.setFactory(form.getFactory() == null ? null : form.getFactory().getId());
        formDTO.setBranch(form.getBranch() == null ? null : form.getBranch().getId());
        formDTO.setTeam(form.getTeam() == null ? null : form.getTeam().getId());
        formDTO.setLine(form.getLine() == null ? null : form.getLine().getId());
        return formDTO;
    }

    private Form mapToEntity(final FormDTO formDTO, final Form form) {
        form.setCode(formDTO.getCode());
        form.setName(formDTO.getName());
        form.setDescription(formDTO.getDescription());
        form.setFileName(formDTO.getFileName());
        form.setFilePath(formDTO.getFilePath());
        form.setTimeCreated(formDTO.getTimeCreated());
        form.setTimeModified(formDTO.getTimeModified());
        form.setPublishDate(formDTO.getPublishDate());
        form.setPublishNumber(formDTO.getPublishNumber());
        form.setAppovedUser(formDTO.getAppovedUser());
        form.setCreatedAt(formDTO.getCreatedAt());
        form.setUpdatedAt(formDTO.getUpdatedAt());
        form.setCreatedBy(formDTO.getCreatedBy());
        final Factory factory = formDTO.getFactory() == null ? null : factoryRepository.findById(formDTO.getFactory())
                .orElseThrow(() -> new NotFoundException("factory not found"));
        form.setFactory(factory);
        final Branch branch = formDTO.getBranch() == null ? null : branchRepository.findById(formDTO.getBranch())
                .orElseThrow(() -> new NotFoundException("branch not found"));
        form.setBranch(branch);
        final Team team = formDTO.getTeam() == null ? null : teamRepository.findById(formDTO.getTeam())
                .orElseThrow(() -> new NotFoundException("team not found"));
        form.setTeam(team);
        final Line line = formDTO.getLine() == null ? null : lineRepository.findById(formDTO.getLine())
                .orElseThrow(() -> new NotFoundException("line not found"));
        form.setLine(line);
        return form;
    }

}

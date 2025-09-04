package io.qltb.qltb.service;

import io.qltb.qltb.domain.Branch;
import io.qltb.qltb.domain.Factory;
import io.qltb.qltb.domain.Form;
import io.qltb.qltb.domain.Line;
import io.qltb.qltb.domain.Team;
import io.qltb.qltb.events.BeforeDeleteBranch;
import io.qltb.qltb.events.BeforeDeleteFactory;
import io.qltb.qltb.events.BeforeDeleteLine;
import io.qltb.qltb.events.BeforeDeleteTeam;
import io.qltb.qltb.model.FormDTO;
import io.qltb.qltb.repos.BranchRepository;
import io.qltb.qltb.repos.FactoryRepository;
import io.qltb.qltb.repos.FormRepository;
import io.qltb.qltb.repos.LineRepository;
import io.qltb.qltb.repos.TeamRepository;
import io.qltb.qltb.util.NotFoundException;
import io.qltb.qltb.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


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
        final Form form = formRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        formRepository.delete(form);
    }

    private FormDTO mapToDTO(final Form form, final FormDTO formDTO) {
        form.getFactory().setFactoryBranches(null);
        form.getLine().setTeam(null);
        form.getLine().setLineDevices(null);
        form.getLine().setLineForms(null);
        form.getBranch().setFactory(null);
        form.getBranch().setBranchDayOffs(null);
        form.getBranch().setBranchTeams(null);
        form.getBranch().setBranchForms(null);
        form.getBranch().setBranchPlanTargets(null);
        form.getTeam().setBranch(null);
        form.getTeam().setTeamLines(null);
        form.getTeam().setTeamDayOffs(null);
        form.getTeam().setTeamForms(null);
        formDTO.setId(form.getId());
        formDTO.setCode(form.getCode());
        formDTO.setName(form.getName());
        formDTO.setDescription(form.getDescription());
        formDTO.setFileName(form.getFileName());
        formDTO.setFilePath(form.getFilePath());
        formDTO.setPublishDate(form.getPublishDate());
        formDTO.setPublishNum(form.getPublishNum());
        formDTO.setCreatedAt(form.getCreatedAt());
        formDTO.setUpdatedAt(form.getUpdatedAt());
        formDTO.setCreatedBy(form.getCreatedBy());
        formDTO.setUpdatedBy(form.getUpdatedBy());
        formDTO.setStatus(form.getStatus());
        formDTO.setFactory(form.getFactory() == null ? null : form.getFactory());
        formDTO.setBranch(form.getBranch() == null ? null : form.getBranch());
        formDTO.setTeam(form.getTeam() == null ? null : form.getTeam());
        formDTO.setLine(form.getLine() == null ? null : form.getLine());
        return formDTO;
    }

    private Form mapToEntity(final FormDTO formDTO, final Form form) {
        form.setCode(formDTO.getCode());
        form.setName(formDTO.getName());
        form.setDescription(formDTO.getDescription());
        form.setFileName(formDTO.getFileName());
        form.setFilePath(formDTO.getFilePath());
        form.setPublishDate(formDTO.getPublishDate());
        form.setPublishNum(formDTO.getPublishNum());
        form.setCreatedAt(formDTO.getCreatedAt());
        form.setUpdatedAt(formDTO.getUpdatedAt());
        form.setCreatedBy(formDTO.getCreatedBy());
        form.setUpdatedBy(formDTO.getUpdatedBy());
        form.setStatus(formDTO.getStatus());
        final Factory factory = formDTO.getFactory() == null ? null : factoryRepository.findById(formDTO.getFactory().getId())
                .orElseThrow(() -> new NotFoundException("factory not found"));
        form.setFactory(factory);
        final Branch branch = formDTO.getBranch() == null ? null : branchRepository.findById(formDTO.getBranch().getId())
                .orElseThrow(() -> new NotFoundException("branch not found"));
        form.setBranch(branch);
        final Team team = formDTO.getTeam() == null ? null : teamRepository.findById(formDTO.getTeam().getId())
                .orElseThrow(() -> new NotFoundException("team not found"));
        form.setTeam(team);
        final Line line = formDTO.getLine() == null ? null : lineRepository.findById(formDTO.getLine().getId())
                .orElseThrow(() -> new NotFoundException("line not found"));
        form.setLine(line);
        return form;
    }

    @EventListener(BeforeDeleteFactory.class)
    public void on(final BeforeDeleteFactory event) {
        final ReferencedException referencedException = new ReferencedException();
        final Form factoryForm = formRepository.findFirstByFactoryId(event.getId());
        if (factoryForm != null) {
            referencedException.setKey("factory.form.factory.referenced");
            referencedException.addParam(factoryForm.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteBranch.class)
    public void on(final BeforeDeleteBranch event) {
        final ReferencedException referencedException = new ReferencedException();
        final Form branchForm = formRepository.findFirstByBranchId(event.getId());
        if (branchForm != null) {
            referencedException.setKey("branch.form.branch.referenced");
            referencedException.addParam(branchForm.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteTeam.class)
    public void on(final BeforeDeleteTeam event) {
        final ReferencedException referencedException = new ReferencedException();
        final Form teamForm = formRepository.findFirstByTeamId(event.getId());
        if (teamForm != null) {
            referencedException.setKey("team.form.team.referenced");
            referencedException.addParam(teamForm.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteLine.class)
    public void on(final BeforeDeleteLine event) {
        final ReferencedException referencedException = new ReferencedException();
        final Form lineForm = formRepository.findFirstByLineId(event.getId());
        if (lineForm != null) {
            referencedException.setKey("line.form.line.referenced");
            referencedException.addParam(lineForm.getId());
            throw referencedException;
        }
    }

}

package io.rd.qltb.service;

import io.rd.qltb.domain.Form;
import io.rd.qltb.model.FormDTO;
import io.rd.qltb.repos.FormRepository;
import io.rd.qltb.util.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class FormService {

    private final FormRepository formRepository;

    public FormService(final FormRepository formRepository) {
        this.formRepository = formRepository;
    }

    public List<FormDTO> findAll() {
        final List<Form> forms = formRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
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
        formDTO.setId(form.getId());
        formDTO.setCode(form.getCode());
        formDTO.setName(form.getName());
        formDTO.setDescription(form.getDescription());
        formDTO.setFileName(form.getFileName());
        formDTO.setFilePath(form.getFilePath());
        formDTO.setFactoryId(form.getFactoryId());
        formDTO.setBranchId(form.getBranchId());
        formDTO.setTeamId(form.getTeamId());
        formDTO.setLineId(form.getLineId());
        formDTO.setPublishDate(form.getPublishDate());
        formDTO.setPublishNum(form.getPublishNum());
        formDTO.setCreatedAt(form.getCreatedAt());
        formDTO.setUpdatedAt(form.getUpdatedAt());
        formDTO.setCreatedBy(form.getCreatedBy());
        formDTO.setUpdatedBy(form.getUpdatedBy());
        formDTO.setStatus(form.getStatus());
        return formDTO;
    }

    private Form mapToEntity(final FormDTO formDTO, final Form form) {
        form.setCode(formDTO.getCode());
        form.setName(formDTO.getName());
        form.setDescription(formDTO.getDescription());
        form.setFileName(formDTO.getFileName());
        form.setFilePath(formDTO.getFilePath());
        form.setFactoryId(formDTO.getFactoryId());
        form.setBranchId(formDTO.getBranchId());
        form.setTeamId(formDTO.getTeamId());
        form.setLineId(formDTO.getLineId());
        form.setPublishDate(formDTO.getPublishDate());
        form.setPublishNum(formDTO.getPublishNum());
        form.setCreatedAt(formDTO.getCreatedAt());
        form.setUpdatedAt(formDTO.getUpdatedAt());
        form.setCreatedBy(formDTO.getCreatedBy());
        form.setUpdatedBy(formDTO.getUpdatedBy());
        form.setStatus(formDTO.getStatus());
        return form;
    }

}

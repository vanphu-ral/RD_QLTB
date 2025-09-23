package io.rd.qltb.service;

import io.rd.qltb.domain.PlanSupplie;
import io.rd.qltb.model.PlanSupplieDTO;
import io.rd.qltb.repos.PlanSupplieRepository;
import io.rd.qltb.util.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class PlanSupplieService {

    private final PlanSupplieRepository planSupplieRepository;

    public PlanSupplieService(final PlanSupplieRepository planSupplieRepository) {
        this.planSupplieRepository = planSupplieRepository;
    }

    public List<PlanSupplieDTO> findAll() {
        final List<PlanSupplie> planSupplies = planSupplieRepository.findAll(Sort.by("id"));
        return planSupplies.stream()
                .map(planSupplie -> mapToDTO(planSupplie, new PlanSupplieDTO()))
                .toList();
    }

    public PlanSupplieDTO get(final Long id) {
        return planSupplieRepository.findById(id)
                .map(planSupplie -> mapToDTO(planSupplie, new PlanSupplieDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final PlanSupplieDTO planSupplieDTO) {
        final PlanSupplie planSupplie = new PlanSupplie();
        mapToEntity(planSupplieDTO, planSupplie);
        return planSupplieRepository.save(planSupplie).getId();
    }

    public void update(final Long id, final PlanSupplieDTO planSupplieDTO) {
        final PlanSupplie planSupplie = planSupplieRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(planSupplieDTO, planSupplie);
        planSupplieRepository.save(planSupplie);
    }

    public void delete(final Long id) {
        final PlanSupplie planSupplie = planSupplieRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        planSupplieRepository.delete(planSupplie);
    }

    public PlanSupplieDTO mapToDTO(final PlanSupplie planSupplie,
            final PlanSupplieDTO planSupplieDTO) {
        planSupplieDTO.setId(planSupplie.getId());
        planSupplieDTO.setCode(planSupplie.getCode());
        planSupplieDTO.setName(planSupplie.getName());
        planSupplieDTO.setDescription(planSupplie.getDescription());
        planSupplieDTO.setQuantity(planSupplie.getQuantity());
        planSupplieDTO.setPrice(planSupplie.getPrice());
        planSupplieDTO.setActiveValue(planSupplie.getActiveValue());
        planSupplieDTO.setFileScan(planSupplie.getFileScan());
        planSupplieDTO.setCreatedAt(planSupplie.getCreatedAt());
        planSupplieDTO.setUpdatedAt(planSupplie.getUpdatedAt());
        planSupplieDTO.setCreatedBy(planSupplie.getCreatedBy());
        planSupplieDTO.setUpdatedBy(planSupplie.getUpdatedBy());
        planSupplieDTO.setStatus(planSupplie.getStatus());
        return planSupplieDTO;
    }

    public PlanSupplie mapToEntity(final PlanSupplieDTO planSupplieDTO,
            final PlanSupplie planSupplie) {
        planSupplie.setCode(planSupplieDTO.getCode());
        planSupplie.setName(planSupplieDTO.getName());
        planSupplie.setDescription(planSupplieDTO.getDescription());
        planSupplie.setQuantity(planSupplieDTO.getQuantity());
        planSupplie.setPrice(planSupplieDTO.getPrice());
        planSupplie.setActiveValue(planSupplieDTO.getActiveValue());
        planSupplie.setFileScan(planSupplieDTO.getFileScan());
        planSupplie.setCreatedAt(planSupplieDTO.getCreatedAt());
        planSupplie.setUpdatedAt(planSupplieDTO.getUpdatedAt());
        planSupplie.setCreatedBy(planSupplieDTO.getCreatedBy());
        planSupplie.setUpdatedBy(planSupplieDTO.getUpdatedBy());
        planSupplie.setStatus(planSupplieDTO.getStatus());
        return planSupplie;
    }

}

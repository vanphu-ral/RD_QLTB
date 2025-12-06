package io.rd.qltb.service;

import io.rd.qltb.config.GlobalConfig;
import io.rd.qltb.domain.Supply;
import io.rd.qltb.domain.SupplyGroup;
import io.rd.qltb.events.BeforeDeleteSupply;
import io.rd.qltb.events.BeforeDeleteSupplyGroup;
import io.rd.qltb.model.SupplyDTO;
import io.rd.qltb.repos.SupplyGroupRepository;
import io.rd.qltb.repos.SupplyRepository;
import io.rd.qltb.util.NotFoundException;
import io.rd.qltb.util.ReferencedException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class SupplyService {
    @PersistenceContext
    private EntityManager entityManager;
    private final SupplyRepository supplyRepository;
    private final SupplyGroupRepository supplyGroupRepository;
    private final ApplicationEventPublisher publisher;
    private final GlobalConfig globalConfig;

    public SupplyService(EntityManager entityManager, final SupplyRepository supplyRepository,
                         final SupplyGroupRepository supplyGroupRepository,
                         final ApplicationEventPublisher publisher, GlobalConfig globalConfig) {
        this.entityManager = entityManager;
        this.supplyRepository = supplyRepository;
        this.supplyGroupRepository = supplyGroupRepository;
        this.publisher = publisher;
        this.globalConfig = globalConfig;
    }

    public List<SupplyDTO> findAll() {
        final List<Supply> supplies = supplyRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
        return supplies.stream()
                .map(supply -> mapToDTO(supply, new SupplyDTO()))
                .toList();
    }
    @Transactional
    public Page<SupplyDTO> findSuppliesPaged(Map<String, Object> filters, int page) {
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(Supply.class);
        var root = cq.from(Supply.class);
        List<Predicate> predicates = new ArrayList<>();
        filters.forEach((key, value) -> {
            if (value != null) {
                Path<?> path = root.get(key);
                if (path.getJavaType().equals(LocalDateTime.class)) {
                    String v = value.toString();
                    LocalDateTime dateTime;
                    if (v.length() == 10) {
                        dateTime = LocalDate.parse(v).atStartOfDay();
                    } else {
                        dateTime = LocalDateTime.parse(v);
                    }
                    LocalDate date = dateTime.toLocalDate();
                    LocalDateTime startOfDay = date.atStartOfDay();
                    LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
                    predicates.add(cb.between(root.get(key), startOfDay, endOfDay));
                } else {
                    // chuyển equal sang like
                    predicates.add(cb.like(path.as(String.class), "%" + value + "%"));
                }
            }
        });

        cq.where(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        var query = entityManager.createQuery(cq);
        query.setFirstResult(page * 10);
        query.setMaxResults(10);

        List<Supply> supplies = query.getResultList();
        List<SupplyDTO> dtos = supplies.stream()
                .map(supply -> mapToDTO(supply, new SupplyDTO()))
                .toList();
        return new org.springframework.data.domain.PageImpl<>(dtos, PageRequest.of(page, 10), dtos.size());
    }

    public SupplyDTO get(final Long id) {
        return supplyRepository.findById(id)
                .map(supply -> mapToDTO(supply, new SupplyDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final SupplyDTO supplyDTO) {
        final Supply supply = new Supply();
        mapToEntity(supplyDTO, supply);
        Supply savedSupply = supplyRepository.save(supply);
        savedSupply.setCode(supplyDTO.getCode()+"-"+ globalConfig.createNumberPrefix(savedSupply.getId(),6));
        return supplyRepository.save(savedSupply).getId();
    }

    public void update(final Long id, final SupplyDTO supplyDTO) {
        final Supply supply = supplyRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(supplyDTO, supply);
        supplyRepository.save(supply);
    }

    public void delete(final Long id) {
        final Supply supply = supplyRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteSupply(id));
        supplyRepository.delete(supply);
    }

    private SupplyDTO mapToDTO(final Supply supply, final SupplyDTO dto) {
        dto.setId(supply.getId());
        dto.setCode(supply.getCode());
        dto.setName(supply.getName());
        dto.setDescription(supply.getDescription());
        dto.setSource(supply.getSource());
        dto.setCreatedAt(supply.getCreatedAt());
        dto.setUpdatedAt(supply.getUpdatedAt());
        dto.setCreatedBy(supply.getCreatedBy());
        dto.setUpdatedBy(supply.getUpdatedBy());
        dto.setStatus(supply.getStatus());

        // Sao chép SupplyGroup có kiểm soát
        if (supply.getGroup() != null) {
            SupplyGroup groupCopy = new SupplyGroup();
            groupCopy.setId(supply.getGroup().getId());
            groupCopy.setCode(supply.getGroup().getCode());
            groupCopy.setName(supply.getGroup().getName());
            groupCopy.setDescription(supply.getGroup().getDescription());
            groupCopy.setCreatedAt(supply.getGroup().getCreatedAt());
            groupCopy.setUpdatedAt(supply.getGroup().getUpdatedAt());
            groupCopy.setCreatedBy(supply.getGroup().getCreatedBy());
            groupCopy.setUpdatedBy(supply.getGroup().getUpdatedBy());
            groupCopy.setStatus(supply.getGroup().getStatus());

            // Xóa các quan hệ con để tránh vòng lặp
            groupCopy.setGroupSupplies(null);

            dto.setGroup(groupCopy);
        } else {
            dto.setGroup(null);
        }

        return dto;
    }


    private Supply mapToEntity(final SupplyDTO supplyDTO, final Supply supply) {
        supply.setCode(supplyDTO.getCode());
        supply.setName(supplyDTO.getName());
        supply.setDescription(supplyDTO.getDescription());
        supply.setSource(supplyDTO.getSource());
        supply.setCreatedAt(supplyDTO.getCreatedAt());
        supply.setUpdatedAt(supplyDTO.getUpdatedAt());
        supply.setCreatedBy(supplyDTO.getCreatedBy());
        supply.setUpdatedBy(supplyDTO.getUpdatedBy());
        supply.setStatus(supplyDTO.getStatus());
        final SupplyGroup group = supplyDTO.getGroup() == null ? null : supplyGroupRepository.findById(supplyDTO.getGroup().getId())
                .orElseThrow(() -> new NotFoundException("group not found"));
        supply.setGroup(group);
        return supply;
    }

    @EventListener(BeforeDeleteSupplyGroup.class)
    public void on(final BeforeDeleteSupplyGroup event) {
        final ReferencedException referencedException = new ReferencedException();
        final Supply groupSupply = supplyRepository.findFirstByGroupId(event.getId());
        if (groupSupply != null) {
            referencedException.setKey("supplyGroup.supply.group.referenced");
            referencedException.addParam(groupSupply.getId());
            throw referencedException;
        }
    }

}

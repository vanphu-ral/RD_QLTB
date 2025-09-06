package io.qltb.qltb.service;

import io.qltb.qltb.domain.Supply;
import io.qltb.qltb.domain.SupplyDetail;
import io.qltb.qltb.events.BeforeDeleteSupply;
import io.qltb.qltb.model.SupplyDetailDTO;
import io.qltb.qltb.repos.SupplyDetailRepository;
import io.qltb.qltb.repos.SupplyRepository;
import io.qltb.qltb.util.NotFoundException;
import io.qltb.qltb.util.ReferencedException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
public class SupplyDetailService {

    private final SupplyDetailRepository supplyDetailRepository;
    private final SupplyRepository supplyRepository;

    public SupplyDetailService(final SupplyDetailRepository supplyDetailRepository,
            final SupplyRepository supplyRepository) {
        this.supplyDetailRepository = supplyDetailRepository;
        this.supplyRepository = supplyRepository;
    }

    public List<SupplyDetailDTO> getBySupplyId(Long supplyId) {
        return supplyDetailRepository.findBySupplyId(supplyId).stream()
                .map(s -> mapToDTO(s, new SupplyDetailDTO()))
                .collect(Collectors.toList());
    }

    public List<SupplyDetailDTO> findAll() {
        final List<SupplyDetail> supplyDetails = supplyDetailRepository.findAll(Sort.by("id"));
        return supplyDetails.stream()
                .map(supplyDetail -> mapToDTO(supplyDetail, new SupplyDetailDTO()))
                .toList();
    }

    public SupplyDetailDTO get(final Long id) {
        return supplyDetailRepository.findById(id)
                .map(supplyDetail -> mapToDTO(supplyDetail, new SupplyDetailDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final SupplyDetailDTO supplyDetailDTO) {
        final SupplyDetail supplyDetail = new SupplyDetail();
        mapToEntity(supplyDetailDTO, supplyDetail);
        return supplyDetailRepository.save(supplyDetail).getId();
    }

    public List<Long> creates(final List<SupplyDetailDTO> supplyDetailDTOs) {
        List<Long> createdIds = new ArrayList<>();
        for (SupplyDetailDTO dto : supplyDetailDTOs) {
            SupplyDetail entity;
            if (dto.getId() != null) {
                entity = supplyDetailRepository.findById(dto.getId()).orElse(new SupplyDetail());
            } else {
                entity = new SupplyDetail();
            }
            mapToEntity(dto, entity);
            SupplyDetail saved = supplyDetailRepository.save(entity);
            createdIds.add(saved.getId());
        }
        return createdIds;
    }

    public void update(final Long id, final SupplyDetailDTO supplyDetailDTO) {
        final SupplyDetail supplyDetail = supplyDetailRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(supplyDetailDTO, supplyDetail);
        supplyDetailRepository.save(supplyDetail);
    }

    public void delete(final Long id) {
        final SupplyDetail supplyDetail = supplyDetailRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        supplyDetailRepository.delete(supplyDetail);
    }

    private SupplyDetailDTO mapToDTO(final SupplyDetail supplyDetail,
                                     final SupplyDetailDTO supplyDetailDTO) {
        supplyDetailDTO.setId(supplyDetail.getId());
        supplyDetailDTO.setSerial(supplyDetail.getSerial());
        supplyDetailDTO.setImportDate(supplyDetail.getImportDate());
        supplyDetailDTO.setPrice(supplyDetail.getPrice());
        supplyDetailDTO.setSupplier(supplyDetail.getSupplier());
        supplyDetailDTO.setStatus(supplyDetail.getStatus());

        if (supplyDetail.getSupply() != null) {
            Supply supplyCopy = new Supply();
            supplyCopy.setId(supplyDetail.getSupply().getId());
            supplyCopy.setCode(supplyDetail.getSupply().getCode());
            supplyCopy.setName(supplyDetail.getSupply().getName());
            supplyCopy.setStatus(supplyDetail.getSupply().getStatus());
            supplyCopy.setCreatedAt(supplyDetail.getSupply().getCreatedAt());
            supplyCopy.setUpdatedAt(supplyDetail.getSupply().getUpdatedAt());
            supplyCopy.setCreatedBy(supplyDetail.getSupply().getCreatedBy());
            supplyCopy.setUpdatedBy(supplyDetail.getSupply().getUpdatedBy());

            // Loại bỏ các quan hệ con để tránh vòng lặp hoặc dữ liệu thừa
            supplyCopy.setSupplyDeviceSupplyUsages(null);
            supplyCopy.setGroup(null);
            supplyCopy.setSupplySupplyReplacements(null);
            supplyCopy.setSupplySupplyDetails(null);

            supplyDetailDTO.setSupply(supplyCopy);
        } else {
            supplyDetailDTO.setSupply(null);
        }

        return supplyDetailDTO;
    }


    private SupplyDetail mapToEntity(final SupplyDetailDTO supplyDetailDTO,
            final SupplyDetail supplyDetail) {
        supplyDetail.setSerial(supplyDetailDTO.getSerial());
        supplyDetail.setImportDate(supplyDetailDTO.getImportDate());
        supplyDetail.setPrice(supplyDetailDTO.getPrice());
        supplyDetail.setSupplier(supplyDetailDTO.getSupplier());
        supplyDetail.setStatus(supplyDetailDTO.getStatus());
        final Supply supply = supplyDetailDTO.getSupply() == null ? null : supplyRepository.findById(supplyDetailDTO.getSupply().getId())
                .orElseThrow(() -> new NotFoundException("supply not found"));
        supplyDetail.setSupply(supply);
        return supplyDetail;
    }

    @EventListener(BeforeDeleteSupply.class)
    public void on(final BeforeDeleteSupply event) {
        final ReferencedException referencedException = new ReferencedException();
        final SupplyDetail supplySupplyDetail = supplyDetailRepository.findFirstBySupplyId(event.getId());
        if (supplySupplyDetail != null) {
            referencedException.setKey("supply.supplyDetail.supply.referenced");
            referencedException.addParam(supplySupplyDetail.getId());
            throw referencedException;
        }
    }

}

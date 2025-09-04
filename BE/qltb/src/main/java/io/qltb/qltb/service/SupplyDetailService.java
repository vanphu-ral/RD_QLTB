package io.qltb.qltb.service;

import io.qltb.qltb.domain.Supply;
import io.qltb.qltb.domain.SupplyDetail;
import io.qltb.qltb.events.BeforeDeleteSupply;
import io.qltb.qltb.model.SupplyDetailDTO;
import io.qltb.qltb.repos.SupplyDetailRepository;
import io.qltb.qltb.repos.SupplyRepository;
import io.qltb.qltb.util.NotFoundException;
import io.qltb.qltb.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
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
        supplyDetail.getSupply().setSupplyDeviceSupplyUsages(null);
        supplyDetail.getSupply().setGroup(null);
        supplyDetail.getSupply().setSupplySupplyReplacements(null);
        supplyDetail.getSupply().setSupplySupplyDetails(null);
        supplyDetailDTO.setId(supplyDetail.getId());
        supplyDetailDTO.setSerial(supplyDetail.getSerial());
        supplyDetailDTO.setImportDate(supplyDetail.getImportDate());
        supplyDetailDTO.setSupplier(supplyDetail.getSupplier());
        supplyDetailDTO.setStatus(supplyDetail.getStatus());
        supplyDetailDTO.setSupply(supplyDetail.getSupply() == null ? null : supplyDetail.getSupply());
        return supplyDetailDTO;
    }

    private SupplyDetail mapToEntity(final SupplyDetailDTO supplyDetailDTO,
            final SupplyDetail supplyDetail) {
        supplyDetail.setSerial(supplyDetailDTO.getSerial());
        supplyDetail.setImportDate(supplyDetailDTO.getImportDate());
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

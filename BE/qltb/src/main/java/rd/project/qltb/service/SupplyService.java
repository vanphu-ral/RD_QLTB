package rd.project.qltb.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import rd.project.qltb.domain.DeviceSupplyUsage;
import rd.project.qltb.domain.Supply;
import rd.project.qltb.domain.SupplyGroup;
import rd.project.qltb.domain.SupplyReplacement;
import rd.project.qltb.model.SupplyDTO;
import rd.project.qltb.repos.DeviceSupplyUsageRepository;
import rd.project.qltb.repos.SupplyGroupRepository;
import rd.project.qltb.repos.SupplyReplacementRepository;
import rd.project.qltb.repos.SupplyRepository;
import rd.project.qltb.util.NotFoundException;
import rd.project.qltb.util.ReferencedWarning;


@Service
public class SupplyService {

    private final SupplyRepository supplyRepository;
    private final SupplyGroupRepository supplyGroupRepository;
    private final DeviceSupplyUsageRepository deviceSupplyUsageRepository;
    private final SupplyReplacementRepository supplyReplacementRepository;

    public SupplyService(final SupplyRepository supplyRepository,
            final SupplyGroupRepository supplyGroupRepository,
            final DeviceSupplyUsageRepository deviceSupplyUsageRepository,
            final SupplyReplacementRepository supplyReplacementRepository) {
        this.supplyRepository = supplyRepository;
        this.supplyGroupRepository = supplyGroupRepository;
        this.deviceSupplyUsageRepository = deviceSupplyUsageRepository;
        this.supplyReplacementRepository = supplyReplacementRepository;
    }

    public List<SupplyDTO> findAll() {
        final List<Supply> supplies = supplyRepository.findAll(Sort.by("id"));
        return supplies.stream()
                .map(supply -> mapToDTO(supply, new SupplyDTO()))
                .toList();
    }

    public SupplyDTO get(final Integer id) {
        return supplyRepository.findById(id)
                .map(supply -> mapToDTO(supply, new SupplyDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final SupplyDTO supplyDTO) {
        final Supply supply = new Supply();
        mapToEntity(supplyDTO, supply);
        return supplyRepository.save(supply).getId();
    }

    public void update(final Integer id, final SupplyDTO supplyDTO) {
        final Supply supply = supplyRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(supplyDTO, supply);
        supplyRepository.save(supply);
    }

    public void delete(final Integer id) {
        supplyRepository.deleteById(id);
    }

    private SupplyDTO mapToDTO(final Supply supply, final SupplyDTO supplyDTO) {
        supplyDTO.setId(supply.getId());
        supplyDTO.setCode(supply.getCode());
        supplyDTO.setName(supply.getName());
        supplyDTO.setQuantity(supply.getQuantity());
        supplyDTO.setPrice(supply.getPrice());
        supplyDTO.setDescription(supply.getDescription());
        supplyDTO.setSource(supply.getSource());
        supplyDTO.setCreatedAt(supply.getCreatedAt());
        supplyDTO.setUpdatedAt(supply.getUpdatedAt());
        supplyDTO.setCreatedBy(supply.getCreatedBy());
        supplyDTO.setGroup(supply.getGroup() == null ? null : supply.getGroup().getId());
        return supplyDTO;
    }

    private Supply mapToEntity(final SupplyDTO supplyDTO, final Supply supply) {
        supply.setCode(supplyDTO.getCode());
        supply.setName(supplyDTO.getName());
        supply.setQuantity(supplyDTO.getQuantity());
        supply.setPrice(supplyDTO.getPrice());
        supply.setDescription(supplyDTO.getDescription());
        supply.setSource(supplyDTO.getSource());
        supply.setCreatedAt(supplyDTO.getCreatedAt());
        supply.setUpdatedAt(supplyDTO.getUpdatedAt());
        supply.setCreatedBy(supplyDTO.getCreatedBy());
        final SupplyGroup group = supplyDTO.getGroup() == null ? null : supplyGroupRepository.findById(supplyDTO.getGroup())
                .orElseThrow(() -> new NotFoundException("group not found"));
        supply.setGroup(group);
        return supply;
    }

    public ReferencedWarning getReferencedWarning(final Integer id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Supply supply = supplyRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final DeviceSupplyUsage supplyDeviceSupplyUsage = deviceSupplyUsageRepository.findFirstBySupply(supply);
        if (supplyDeviceSupplyUsage != null) {
            referencedWarning.setKey("supply.deviceSupplyUsage.supply.referenced");
            referencedWarning.addParam(supplyDeviceSupplyUsage.getId());
            return referencedWarning;
        }
        final SupplyReplacement supplySupplyReplacement = supplyReplacementRepository.findFirstBySupply(supply);
        if (supplySupplyReplacement != null) {
            referencedWarning.setKey("supply.supplyReplacement.supply.referenced");
            referencedWarning.addParam(supplySupplyReplacement.getId());
            return referencedWarning;
        }
        return null;
    }

}

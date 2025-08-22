package rd.project.qltb.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import rd.project.qltb.domain.Supply;
import rd.project.qltb.domain.SupplyGroup;
import rd.project.qltb.model.SupplyGroupDTO;
import rd.project.qltb.repos.SupplyGroupRepository;
import rd.project.qltb.repos.SupplyRepository;
import rd.project.qltb.util.NotFoundException;
import rd.project.qltb.util.ReferencedWarning;


@Service
public class SupplyGroupService {

    private final SupplyGroupRepository supplyGroupRepository;
    private final SupplyRepository supplyRepository;

    public SupplyGroupService(final SupplyGroupRepository supplyGroupRepository,
            final SupplyRepository supplyRepository) {
        this.supplyGroupRepository = supplyGroupRepository;
        this.supplyRepository = supplyRepository;
    }

    public List<SupplyGroupDTO> findAll() {
        final List<SupplyGroup> supplyGroups = supplyGroupRepository.findAll(Sort.by("id"));
        return supplyGroups.stream()
                .map(supplyGroup -> mapToDTO(supplyGroup, new SupplyGroupDTO()))
                .toList();
    }

    public SupplyGroupDTO get(final Integer id) {
        return supplyGroupRepository.findById(id)
                .map(supplyGroup -> mapToDTO(supplyGroup, new SupplyGroupDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final SupplyGroupDTO supplyGroupDTO) {
        final SupplyGroup supplyGroup = new SupplyGroup();
        mapToEntity(supplyGroupDTO, supplyGroup);
        return supplyGroupRepository.save(supplyGroup).getId();
    }

    public void update(final Integer id, final SupplyGroupDTO supplyGroupDTO) {
        final SupplyGroup supplyGroup = supplyGroupRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(supplyGroupDTO, supplyGroup);
        supplyGroupRepository.save(supplyGroup);
    }

    public void delete(final Integer id) {
        supplyGroupRepository.deleteById(id);
    }

    private SupplyGroupDTO mapToDTO(final SupplyGroup supplyGroup,
            final SupplyGroupDTO supplyGroupDTO) {
        supplyGroupDTO.setId(supplyGroup.getId());
        supplyGroupDTO.setName(supplyGroup.getName());
        supplyGroupDTO.setDescription(supplyGroup.getDescription());
        supplyGroupDTO.setCreatedAt(supplyGroup.getCreatedAt());
        supplyGroupDTO.setUpdatedAt(supplyGroup.getUpdatedAt());
        supplyGroupDTO.setCreatedBy(supplyGroup.getCreatedBy());
        return supplyGroupDTO;
    }

    private SupplyGroup mapToEntity(final SupplyGroupDTO supplyGroupDTO,
            final SupplyGroup supplyGroup) {
        supplyGroup.setName(supplyGroupDTO.getName());
        supplyGroup.setDescription(supplyGroupDTO.getDescription());
        supplyGroup.setCreatedAt(supplyGroupDTO.getCreatedAt());
        supplyGroup.setUpdatedAt(supplyGroupDTO.getUpdatedAt());
        supplyGroup.setCreatedBy(supplyGroupDTO.getCreatedBy());
        return supplyGroup;
    }

    public ReferencedWarning getReferencedWarning(final Integer id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final SupplyGroup supplyGroup = supplyGroupRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Supply groupSupply = supplyRepository.findFirstByGroup(supplyGroup);
        if (groupSupply != null) {
            referencedWarning.setKey("supplyGroup.supply.group.referenced");
            referencedWarning.addParam(groupSupply.getId());
            return referencedWarning;
        }
        return null;
    }

}

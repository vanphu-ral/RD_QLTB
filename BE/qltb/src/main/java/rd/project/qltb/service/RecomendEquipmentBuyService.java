package rd.project.qltb.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import rd.project.qltb.domain.RecomendEquipmentBuy;
import rd.project.qltb.model.RecomendEquipmentBuyDTO;
import rd.project.qltb.repos.RecomendEquipmentBuyRepository;
import rd.project.qltb.util.NotFoundException;


@Service
public class RecomendEquipmentBuyService {

    private final RecomendEquipmentBuyRepository recomendEquipmentBuyRepository;

    public RecomendEquipmentBuyService(
            final RecomendEquipmentBuyRepository recomendEquipmentBuyRepository) {
        this.recomendEquipmentBuyRepository = recomendEquipmentBuyRepository;
    }

    public List<RecomendEquipmentBuyDTO> findAll() {
        final List<RecomendEquipmentBuy> recomendEquipmentBuys = recomendEquipmentBuyRepository.findAll(Sort.by("id"));
        return recomendEquipmentBuys.stream()
                .map(recomendEquipmentBuy -> mapToDTO(recomendEquipmentBuy, new RecomendEquipmentBuyDTO()))
                .toList();
    }

    public RecomendEquipmentBuyDTO get(final Long id) {
        return recomendEquipmentBuyRepository.findById(id)
                .map(recomendEquipmentBuy -> mapToDTO(recomendEquipmentBuy, new RecomendEquipmentBuyDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final RecomendEquipmentBuyDTO recomendEquipmentBuyDTO) {
        final RecomendEquipmentBuy recomendEquipmentBuy = new RecomendEquipmentBuy();
        mapToEntity(recomendEquipmentBuyDTO, recomendEquipmentBuy);
        return recomendEquipmentBuyRepository.save(recomendEquipmentBuy).getId();
    }

    public void update(final Long id, final RecomendEquipmentBuyDTO recomendEquipmentBuyDTO) {
        final RecomendEquipmentBuy recomendEquipmentBuy = recomendEquipmentBuyRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(recomendEquipmentBuyDTO, recomendEquipmentBuy);
        recomendEquipmentBuyRepository.save(recomendEquipmentBuy);
    }

    public void delete(final Long id) {
        recomendEquipmentBuyRepository.deleteById(id);
    }

    private RecomendEquipmentBuyDTO mapToDTO(final RecomendEquipmentBuy recomendEquipmentBuy,
            final RecomendEquipmentBuyDTO recomendEquipmentBuyDTO) {
        recomendEquipmentBuyDTO.setId(recomendEquipmentBuy.getId());
        recomendEquipmentBuyDTO.setCode(recomendEquipmentBuy.getCode());
        recomendEquipmentBuyDTO.setName(recomendEquipmentBuy.getName());
        recomendEquipmentBuyDTO.setPrice(recomendEquipmentBuy.getPrice());
        recomendEquipmentBuyDTO.setQuantity(recomendEquipmentBuy.getQuantity());
        recomendEquipmentBuyDTO.setCreatedAt(recomendEquipmentBuy.getCreatedAt());
        recomendEquipmentBuyDTO.setUpdatedAt(recomendEquipmentBuy.getUpdatedAt());
        recomendEquipmentBuyDTO.setCreatedBy(recomendEquipmentBuy.getCreatedBy());
        return recomendEquipmentBuyDTO;
    }

    private RecomendEquipmentBuy mapToEntity(final RecomendEquipmentBuyDTO recomendEquipmentBuyDTO,
            final RecomendEquipmentBuy recomendEquipmentBuy) {
        recomendEquipmentBuy.setCode(recomendEquipmentBuyDTO.getCode());
        recomendEquipmentBuy.setName(recomendEquipmentBuyDTO.getName());
        recomendEquipmentBuy.setPrice(recomendEquipmentBuyDTO.getPrice());
        recomendEquipmentBuy.setQuantity(recomendEquipmentBuyDTO.getQuantity());
        recomendEquipmentBuy.setCreatedAt(recomendEquipmentBuyDTO.getCreatedAt());
        recomendEquipmentBuy.setUpdatedAt(recomendEquipmentBuyDTO.getUpdatedAt());
        recomendEquipmentBuy.setCreatedBy(recomendEquipmentBuyDTO.getCreatedBy());
        return recomendEquipmentBuy;
    }

}

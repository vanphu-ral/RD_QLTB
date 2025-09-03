package io.qltb.qltb.service;

import io.qltb.qltb.domain.AssetHistory;
import io.qltb.qltb.model.AssetHistoryDTO;
import io.qltb.qltb.repos.AssetHistoryRepository;
import io.qltb.qltb.util.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class AssetHistoryService {

    private final AssetHistoryRepository assetHistoryRepository;

    public AssetHistoryService(final AssetHistoryRepository assetHistoryRepository) {
        this.assetHistoryRepository = assetHistoryRepository;
    }

    public List<AssetHistoryDTO> findAll() {
        final List<AssetHistory> assetHistories = assetHistoryRepository.findAll(Sort.by("id"));
        return assetHistories.stream()
                .map(assetHistory -> mapToDTO(assetHistory, new AssetHistoryDTO()))
                .toList();
    }

    public AssetHistoryDTO get(final Long id) {
        return assetHistoryRepository.findById(id)
                .map(assetHistory -> mapToDTO(assetHistory, new AssetHistoryDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final AssetHistoryDTO assetHistoryDTO) {
        final AssetHistory assetHistory = new AssetHistory();
        mapToEntity(assetHistoryDTO, assetHistory);
        return assetHistoryRepository.save(assetHistory).getId();
    }

    public void update(final Long id, final AssetHistoryDTO assetHistoryDTO) {
        final AssetHistory assetHistory = assetHistoryRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(assetHistoryDTO, assetHistory);
        assetHistoryRepository.save(assetHistory);
    }

    public void delete(final Long id) {
        final AssetHistory assetHistory = assetHistoryRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        assetHistoryRepository.delete(assetHistory);
    }

    private AssetHistoryDTO mapToDTO(final AssetHistory assetHistory,
            final AssetHistoryDTO assetHistoryDTO) {
        assetHistoryDTO.setId(assetHistory.getId());
        assetHistoryDTO.setAssetType(assetHistory.getAssetType());
        assetHistoryDTO.setAssetId(assetHistory.getAssetId());
        assetHistoryDTO.setEventType(assetHistory.getEventType());
        assetHistoryDTO.setDescription(assetHistory.getDescription());
        assetHistoryDTO.setDowntimeStart(assetHistory.getDowntimeStart());
        assetHistoryDTO.setDowntimeEnd(assetHistory.getDowntimeEnd());
        assetHistoryDTO.setCreatedAt(assetHistory.getCreatedAt());
        assetHistoryDTO.setUpdatedAt(assetHistory.getUpdatedAt());
        assetHistoryDTO.setCreatedBy(assetHistory.getCreatedBy());
        assetHistoryDTO.setUpdatedBy(assetHistory.getUpdatedBy());
        return assetHistoryDTO;
    }

    private AssetHistory mapToEntity(final AssetHistoryDTO assetHistoryDTO,
            final AssetHistory assetHistory) {
        assetHistory.setAssetType(assetHistoryDTO.getAssetType());
        assetHistory.setAssetId(assetHistoryDTO.getAssetId());
        assetHistory.setEventType(assetHistoryDTO.getEventType());
        assetHistory.setDescription(assetHistoryDTO.getDescription());
        assetHistory.setDowntimeStart(assetHistoryDTO.getDowntimeStart());
        assetHistory.setDowntimeEnd(assetHistoryDTO.getDowntimeEnd());
        assetHistory.setCreatedAt(assetHistoryDTO.getCreatedAt());
        assetHistory.setUpdatedAt(assetHistoryDTO.getUpdatedAt());
        assetHistory.setCreatedBy(assetHistoryDTO.getCreatedBy());
        assetHistory.setUpdatedBy(assetHistoryDTO.getUpdatedBy());
        return assetHistory;
    }

}

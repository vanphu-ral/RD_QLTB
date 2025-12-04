package io.rd.qltb.service;

import io.rd.qltb.domain.DetailLog;
import io.rd.qltb.model.DetailLogDTO;
import io.rd.qltb.model.DetailLogResponseDTO;
import io.rd.qltb.repos.DetailLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DetailLogService {
    @Autowired
    private final DetailLogRepository detailLogRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    public DetailLogService(DetailLogRepository detailLogRepository) {
        this.detailLogRepository = detailLogRepository;
    }

    public DetailLogDTO mapToDTO(final DetailLog detailLog) {
        DetailLogDTO dto = new DetailLogDTO();
        dto.setId(detailLog.getId());
        dto.setEntityType(detailLog.getEntityType());
        dto.setEntityId(detailLog.getEntityId());
        dto.setDetail(detailLog.getDetail());
        dto.setVersion(detailLog.getVersion());
        dto.setCreatedBy(detailLog.getCreatedBy());
        dto.setStatus(detailLog.getStatus());
        return dto;
    }

    public DetailLog mapToEntity(final DetailLogDTO dto, final DetailLog entity) {
        entity.setEntityType(dto.getEntityType());
        entity.setEntityId(dto.getEntityId());
        entity.setDetail(dto.getDetail());
        entity.setVersion(dto.getVersion());
        entity.setCreatedBy(dto.getCreatedBy());
        entity.setStatus(dto.getStatus());
        entity.setLoggedAt(java.time.LocalDateTime.now());
        entity.setCreatedAt(java.time.LocalDateTime.now());
        return entity;
    }
    public DetailLogResponseDTO getAllByEntityTypeAndEntityId(String entityType, Long entityId) {
        DetailLogResponseDTO responseDTO = new DetailLogResponseDTO();
        var detailLogs = detailLogRepository.findAllByEntityTypeAndEntityId(entityType, entityId);
        var detailLogDTOs = detailLogs.stream().map(this::mapToDTO).toList();
        responseDTO.setDetailLog(detailLogDTOs);
        String sql  = "SELECT * FROM detail_log WHERE entity_type = '" + entityType + "' AND entity_id = " + entityId;
        Map<String,Object> data = jdbcTemplate.queryForMap(sql);
        if (data != null) {
            responseDTO.setData(data);
        }else {
            responseDTO.setData(Map.of());
        }
        return responseDTO;
    }
    public DetailLogDTO createDetailLog(DetailLogDTO detailLogDTO) {
        DetailLog detailLog = mapToEntity(detailLogDTO, new DetailLog());
        detailLog = detailLogRepository.save(detailLog);
        return mapToDTO(detailLog);
    }
    public void create  (DetailLogDTO detailLogDTO) {
        DetailLog detailLog = mapToEntity(detailLogDTO, new DetailLog());
        detailLogRepository.save(detailLog);
    }
}

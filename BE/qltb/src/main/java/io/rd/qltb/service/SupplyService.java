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
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import static io.rd.qltb.config.ConstantStatusGlobal.APPROVED;
import static io.rd.qltb.config.ConstantStatusGlobal.DELETED;


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

    public List<SupplyDTO> findAllByApprove() {
        final List<Supply> supplies = supplyRepository.findByStatusOrderByIdDesc(APPROVED);
        return supplies.stream()
                .map(supply -> mapToDTO(supply, new SupplyDTO()))
                .toList();
    }
    public List<SupplyDTO> findAll() {
        final List<Supply> supplies = supplyRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
        return supplies.stream()
                .map(supply -> mapToDTO(supply, new SupplyDTO()))
                .toList();
    }
    @Transactional
    public Page<SupplyDTO> findSuppliesPaged(Map<String, Object> filters, int page) {
        int pageSize = 10;
        var cb = entityManager.getCriteriaBuilder();

        // 1. Khởi tạo Query lấy dữ liệu (Data Query)
        var cq = cb.createQuery(Supply.class);
        var root = cq.from(Supply.class);

        // 2. Khởi tạo Query đếm tổng (Count Query)
        var countQuery = cb.createQuery(Long.class);
        var countRoot = countQuery.from(Supply.class);

        // 3. Xây dựng danh sách Predicates (Dùng chung logic)
        Predicate[] dataPredicates = buildSupplyPredicates(filters, cb, root);
        Predicate[] countPredicates = buildSupplyPredicates(filters, cb, countRoot);

        // 4. Thực thi truy vấn lấy dữ liệu
        cq.where(dataPredicates);
        cq.orderBy(cb.desc(root.get("id"))); // Sắp xếp mới nhất lên đầu

        var query = entityManager.createQuery(cq);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);

        List<SupplyDTO> dtos = query.getResultList().stream()
                .map(supply -> mapToDTO(supply, new SupplyDTO()))
                .toList();

        // 5. Thực thi truy vấn đếm tổng số bản ghi (Cần thiết cho phân trang)
        countQuery.select(cb.count(countRoot)).where(countPredicates);
        Long totalRecords = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(dtos, PageRequest.of(page, pageSize), totalRecords);
    }

    /**
     * Hàm hỗ trợ xử lý logic lọc linh hoạt cho Supply
     */
    private Predicate[] buildSupplyPredicates(Map<String, Object> filters, CriteriaBuilder cb, Root<Supply> root) {
        List<Predicate> predicates = new ArrayList<>();

        // Các quan hệ cần join để lấy trường "name" (Dựa trên SupplyDTO của bạn là "group")
        List<String> relationKeys = List.of("group");

        filters.forEach((key, value) -> {
            if (value != null && !value.toString().isEmpty()) {
                Path<?> path;

                // Xử lý Join nếu key là "group"
                if (relationKeys.contains(key)) {
                    path = root.join(key, JoinType.LEFT).get("name");
                }
                // Xử lý Nested key nếu truyền dạng "group.code"
                else if (key.contains(".")) {
                    String[] parts = key.split("\\.");
                    Join<Object, Object> join = root.join(parts[0], JoinType.LEFT);
                    path = join.get(parts[1]);
                }
                // Các trường trực tiếp: code, name, sapCode, description...
                else {
                    path = root.get(key);
                }

                // PHÂN LOẠI KIỂU DỮ LIỆU
                if (path.getJavaType().equals(LocalDateTime.class)) {
                    String v = value.toString();
                    LocalDateTime startOfDay = (v.length() == 10)
                            ? LocalDate.parse(v).atStartOfDay()
                            : LocalDateTime.parse(v);
                    LocalDateTime endOfDay = startOfDay.toLocalDate().atTime(LocalTime.MAX);
                    predicates.add(cb.between((Expression<LocalDateTime>) path, startOfDay, endOfDay));
                }
                else if (path.getJavaType().equals(Integer.class)) {
                    // Ví dụ lọc theo status (khớp chính xác)
                    predicates.add(cb.equal(path, Integer.valueOf(value.toString())));
                }
                else {
                    // Mặc định dùng LIKE cho String (code, name, sapCode...)
                    predicates.add(cb.like(cb.lower(path.as(String.class)), "%" + value.toString().toLowerCase() + "%"));
                }
            }
        });

        // Luôn lọc bỏ vật tư đã xóa (status != 10)
        predicates.add(cb.notEqual(root.get("status"), 10));

        return predicates.toArray(new Predicate[0]);
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
//        publisher.publishEvent(new BeforeDeleteSupply(id));
        supply.setStatus(DELETED);
        supplyRepository.save(supply);
    }

    private SupplyDTO mapToDTO(final Supply supply, final SupplyDTO dto) {
        dto.setId(supply.getId());
        dto.setCode(supply.getCode());
        dto.setName(supply.getName());
        dto.setDescription(supply.getDescription());
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

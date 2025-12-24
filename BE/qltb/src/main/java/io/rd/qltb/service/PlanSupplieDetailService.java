package io.rd.qltb.service;


import java.util.List;

import io.rd.qltb.domain.*;
import io.rd.qltb.model.PlanSupplieDetailDTO;
import io.rd.qltb.repos.*;
import io.rd.qltb.util.NotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class PlanSupplieDetailService {

    private final PlanSupplieDetailRepository planSupplieDetailRepository;
    private final PlanSupplieRepository planSupplieRepository;
    private final DeviceGroupRepository deviceGroupRepository;
    private final LineRepository lineRepository;
    private final SupplyRepository supplyRepository;

    public PlanSupplieDetailService(final PlanSupplieDetailRepository planSupplieDetailRepository,
                                    final PlanSupplieRepository planSupplieRepository,
                                    final DeviceGroupRepository deviceGroupRepository,
                                    final LineRepository lineRepository,
                                    final SupplyRepository supplyRepository) {
        this.planSupplieDetailRepository = planSupplieDetailRepository;
        this.planSupplieRepository = planSupplieRepository;
        this.deviceGroupRepository = deviceGroupRepository;
        this.lineRepository = lineRepository;
        this.supplyRepository = supplyRepository;
    }

    public List<PlanSupplieDetailDTO> findAll() {
        final List<PlanSupplieDetail> planSupplieDetails = planSupplieDetailRepository.findAll(Sort.by("id"));
        return planSupplieDetails.stream()
                .map(planSupplieDetail -> mapToDTO(planSupplieDetail, new PlanSupplieDetailDTO()))
                .toList();
    }

    public PlanSupplieDetailDTO get(final Long id) {
        return planSupplieDetailRepository.findById(id)
                .map(planSupplieDetail -> mapToDTO(planSupplieDetail, new PlanSupplieDetailDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final PlanSupplieDetailDTO planSupplieDetailDTO) {
        final PlanSupplieDetail planSupplieDetail = new PlanSupplieDetail();
        mapToEntity(planSupplieDetailDTO, planSupplieDetail);
        return planSupplieDetailRepository.save(planSupplieDetail).getId();
    }

    public void update(final Long id, final PlanSupplieDetailDTO planSupplieDetailDTO) {
        final PlanSupplieDetail planSupplieDetail = planSupplieDetailRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(planSupplieDetailDTO, planSupplieDetail);
        planSupplieDetailRepository.save(planSupplieDetail);
    }

    public void delete(final Long id) {
        final PlanSupplieDetail planSupplieDetail = planSupplieDetailRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        planSupplieDetailRepository.delete(planSupplieDetail);
    }

    public PlanSupplieDetailDTO mapToDTO(final PlanSupplieDetail planSupplieDetail,
                                          final PlanSupplieDetailDTO planSupplieDetailDTO) {
        planSupplieDetailDTO.setId(planSupplieDetail.getId());
        planSupplieDetailDTO.setQuantity(planSupplieDetail.getQuantity());
        planSupplieDetailDTO.setSymbol(planSupplieDetail.getSymbol());
        planSupplieDetailDTO.setTechRequired(planSupplieDetail.getTechRequired());
        planSupplieDetailDTO.setManufacturer(planSupplieDetail.getManufacturer());
        planSupplieDetailDTO.setDeliveryTime(planSupplieDetail.getDeliveryTime());
        planSupplieDetailDTO.setNote(planSupplieDetail.getNote());
        planSupplieDetailDTO.setCreatedAt(planSupplieDetail.getCreatedAt());
        planSupplieDetailDTO.setUpdatedAt(planSupplieDetail.getUpdatedAt());
        planSupplieDetailDTO.setCreatedBy(planSupplieDetail.getCreatedBy());
        planSupplieDetailDTO.setUpdatedBy(planSupplieDetail.getUpdatedBy());
        planSupplieDetailDTO.setStatus(planSupplieDetail.getStatus());

        if (planSupplieDetail.getPlanSupplie() != null) {
            PlanSupplie planSupplie = new PlanSupplie();
            planSupplie.setId(planSupplieDetail.getPlanSupplie().getId());
            planSupplie.setCode(planSupplieDetail.getPlanSupplie().getCode());
            planSupplie.setName(planSupplieDetail.getPlanSupplie().getName());
            planSupplie.setStatus(planSupplieDetail.getPlanSupplie().getStatus());
            // Xóa các quan hệ con
            planSupplie.setPlanSupplieDetails(null);

            planSupplieDetailDTO.setPlanSupplie(planSupplie);
        } else {
            planSupplieDetailDTO.setPlanSupplie(null);
        }

        if (planSupplieDetail.getDeviceGroup() != null) {
            DeviceGroup groupCopy = new DeviceGroup();
            groupCopy.setId(planSupplieDetail.getDeviceGroup().getId());
            groupCopy.setCode(planSupplieDetail.getDeviceGroup().getCode());
            groupCopy.setName(planSupplieDetail.getDeviceGroup().getName());
            groupCopy.setDescription(planSupplieDetail.getDeviceGroup().getDescription());
            groupCopy.setCreatedAt(planSupplieDetail.getDeviceGroup().getCreatedAt());
            groupCopy.setUpdatedAt(planSupplieDetail.getDeviceGroup().getUpdatedAt());
            groupCopy.setCreatedBy(planSupplieDetail.getDeviceGroup().getCreatedBy());
            groupCopy.setUpdatedBy(planSupplieDetail.getDeviceGroup().getUpdatedBy());
            groupCopy.setStatus(planSupplieDetail.getDeviceGroup().getStatus());

            // Xóa các quan hệ con
            groupCopy.setDeviceGroupSampleReports(null);
            groupCopy.setDeviceGroupKeyMappingDeviceSampleReports(null);
            groupCopy.setGroupDevices(null);
            groupCopy.setDeviceGroupPlanDetails(null);

            planSupplieDetailDTO.setDeviceGroup(groupCopy);
        } else {
            planSupplieDetailDTO.setDeviceGroup(null);
        }

        if (planSupplieDetail.getLine() != null) {
            Line line = new Line();
            line.setId(planSupplieDetail.getLine().getId());
            line.setCode(planSupplieDetail.getLine().getCode());
            line.setName(planSupplieDetail.getLine().getName());
            line.setStatus(planSupplieDetail.getLine().getStatus());

            // Xóa các quan hệ con
            line.setTeam(null);

            planSupplieDetailDTO.setLine(line);
        } else {
            planSupplieDetailDTO.setLine(null);
        }

        if (planSupplieDetail.getSupply() != null) {
            Supply supply = new Supply();
            supply.setId(planSupplieDetail.getSupply().getId());
            supply.setCode(planSupplieDetail.getSupply().getCode());
            supply.setName(planSupplieDetail.getSupply().getName());
            supply.setStatus(planSupplieDetail.getSupply().getStatus());

            supply.setGroup(null);
            supply.setSupplySupplyDetails(null);

            planSupplieDetailDTO.setSupply(supply);
        } else {
            planSupplieDetailDTO.setSupply(null);
        }

        return planSupplieDetailDTO;
    }

    public PlanSupplieDetail mapToEntity(final PlanSupplieDetailDTO planSupplieDetailDTO,
                                          final PlanSupplieDetail planSupplieDetail) {
        planSupplieDetail.setQuantity(planSupplieDetailDTO.getQuantity());
        planSupplieDetail.setSymbol(planSupplieDetailDTO.getSymbol());
        planSupplieDetail.setTechRequired(planSupplieDetailDTO.getTechRequired());
        planSupplieDetail.setManufacturer(planSupplieDetailDTO.getManufacturer());
        planSupplieDetail.setDeliveryTime(planSupplieDetailDTO.getDeliveryTime());
        planSupplieDetail.setNote(planSupplieDetailDTO.getNote());
        planSupplieDetail.setCreatedAt(planSupplieDetailDTO.getCreatedAt());
        planSupplieDetail.setUpdatedAt(planSupplieDetailDTO.getUpdatedAt());
        planSupplieDetail.setCreatedBy(planSupplieDetailDTO.getCreatedBy());
        planSupplieDetail.setUpdatedBy(planSupplieDetailDTO.getUpdatedBy());
        planSupplieDetail.setStatus(planSupplieDetailDTO.getStatus());

        final DeviceGroup deviceGroup = planSupplieDetailDTO.getDeviceGroup() == null ? null : deviceGroupRepository.findById(planSupplieDetailDTO.getDeviceGroup().getId())
                .orElseThrow(() -> new NotFoundException("deviceGroup not found"));
        planSupplieDetail.setDeviceGroup(deviceGroup);

        final Line line = planSupplieDetailDTO.getLine() == null ? null : lineRepository.findById(planSupplieDetailDTO.getLine().getId())
                .orElseThrow(() -> new NotFoundException("line not found"));
        planSupplieDetail.setLine(line);

        final Supply supply = planSupplieDetailDTO.getSupply() == null ? null : supplyRepository.findById(planSupplieDetailDTO.getSupply().getId())
                .orElseThrow(() -> new NotFoundException("supply not found"));
        planSupplieDetail.setSupply(supply);
        return planSupplieDetail;
    }

}

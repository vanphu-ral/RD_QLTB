package io.rd.qltb.service;

import io.rd.qltb.domain.DayOff;
import io.rd.qltb.model.DayOffDTO;
import io.rd.qltb.repos.DayOffRepository;
import io.rd.qltb.util.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class DayOffService {

    private final DayOffRepository dayOffRepository;

    public DayOffService(final DayOffRepository dayOffRepository) {
        this.dayOffRepository = dayOffRepository;
    }

    public List<DayOffDTO> findAll() {
        final List<DayOff> dayOffs = dayOffRepository.findAll(Sort.by("id"));
        return dayOffs.stream()
                .map(dayOff -> mapToDTO(dayOff, new DayOffDTO()))
                .toList();
    }

    public DayOffDTO get(final Long id) {
        return dayOffRepository.findById(id)
                .map(dayOff -> mapToDTO(dayOff, new DayOffDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final DayOffDTO dayOffDTO) {
        final DayOff dayOff = new DayOff();
        mapToEntity(dayOffDTO, dayOff);
        return dayOffRepository.save(dayOff).getId();
    }

    public void update(final Long id, final DayOffDTO dayOffDTO) {
        final DayOff dayOff = dayOffRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(dayOffDTO, dayOff);
        dayOffRepository.save(dayOff);
    }

    public void delete(final Long id) {
        final DayOff dayOff = dayOffRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        dayOffRepository.delete(dayOff);
    }

    private DayOffDTO mapToDTO(final DayOff dayOff, final DayOffDTO dayOffDTO) {
        dayOffDTO.setId(dayOff.getId());
        dayOffDTO.setCode(dayOff.getCode());
        dayOffDTO.setName(dayOff.getName());
        dayOffDTO.setListBranchId(dayOff.getListBranchId());
        dayOffDTO.setListTeamId(dayOff.getListTeamId());
        dayOffDTO.setDay(dayOff.getDay());
        dayOffDTO.setFrequency(dayOff.getFrequency());
        dayOffDTO.setFromDate(dayOff.getFromDate());
        dayOffDTO.setToDate(dayOff.getToDate());
        dayOffDTO.setDescription(dayOff.getDescription());
        dayOffDTO.setCreatedAt(dayOff.getCreatedAt());
        dayOffDTO.setUpdatedAt(dayOff.getUpdatedAt());
        dayOffDTO.setCreatedBy(dayOff.getCreatedBy());
        dayOffDTO.setUpdatedBy(dayOff.getUpdatedBy());
        dayOffDTO.setStatus(dayOff.getStatus());
        return dayOffDTO;
    }

    private DayOff mapToEntity(final DayOffDTO dayOffDTO, final DayOff dayOff) {
        dayOff.setCode(dayOffDTO.getCode());
        dayOff.setName(dayOffDTO.getName());
        dayOff.setListBranchId(dayOffDTO.getListBranchId());
        dayOff.setListTeamId(dayOffDTO.getListTeamId());
        dayOff.setDay(dayOffDTO.getDay());
        dayOff.setFrequency(dayOffDTO.getFrequency());
        dayOff.setFromDate(dayOffDTO.getFromDate());
        dayOff.setToDate(dayOffDTO.getToDate());
        dayOff.setDescription(dayOffDTO.getDescription());
        dayOff.setCreatedAt(dayOffDTO.getCreatedAt());
        dayOff.setUpdatedAt(dayOffDTO.getUpdatedAt());
        dayOff.setCreatedBy(dayOffDTO.getCreatedBy());
        dayOff.setUpdatedBy(dayOffDTO.getUpdatedBy());
        dayOff.setStatus(dayOffDTO.getStatus());
        return dayOff;
    }

}

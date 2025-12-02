package io.rd.qltb.service;

import io.rd.qltb.domain.DayOffCalendar;
import io.rd.qltb.domain.UserImage;
import io.rd.qltb.model.DayOffCalendarDTO;
import io.rd.qltb.repos.DayOffCalendarRepository;
import io.rd.qltb.util.NotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DayOffCalendarService {

    private final DayOffCalendarRepository dayOffCalendarRepository;
    private final ApplicationEventPublisher publisher;

    public DayOffCalendarService(final DayOffCalendarRepository dayOffCalendarRepository, final ApplicationEventPublisher publisher) {
        this.dayOffCalendarRepository = dayOffCalendarRepository;
        this.publisher = publisher;
    }

    public List<DayOffCalendarDTO> findAll() {
        final List<DayOffCalendar> dayOffCalendars = dayOffCalendarRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
        return dayOffCalendars.stream()
                .map(dayOffCalendar -> mapToDTO(dayOffCalendar, new DayOffCalendarDTO()))
                .toList();
    }

    public DayOffCalendarDTO get(final Long id) {
        return dayOffCalendarRepository.findById(id)
                .map(dayOffCalendar -> mapToDTO(dayOffCalendar, new DayOffCalendarDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final DayOffCalendarDTO dayOffCalendarDTO) {
        final DayOffCalendar dayOffCalendar = new DayOffCalendar();
        mapToEntity(dayOffCalendarDTO, dayOffCalendar);
        return dayOffCalendarRepository.save(dayOffCalendar).getId();
    }

    public void update(final Long id, final DayOffCalendarDTO dayOffCalendarDTO) {
        final DayOffCalendar dayOffCalendar = dayOffCalendarRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(dayOffCalendarDTO, dayOffCalendar);
        dayOffCalendarRepository.save(dayOffCalendar);
    }

    public void delete(final Long id) {
        final DayOffCalendar dayOffCalendar = dayOffCalendarRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        dayOffCalendarRepository.delete(dayOffCalendar);
    }

    private DayOffCalendarDTO mapToDTO(final DayOffCalendar dayOffCalendar, final DayOffCalendarDTO dayOffCalendarDTO) {
        dayOffCalendarDTO.setId(dayOffCalendar.getId());
        dayOffCalendarDTO.setBranchId(dayOffCalendar.getBranchId());
        dayOffCalendarDTO.setTeamId(dayOffCalendar.getTeamId());
        dayOffCalendarDTO.setDate(dayOffCalendar.getDate());
        dayOffCalendarDTO.setDayOfWeek(dayOffCalendar.getDayOfWeek());
        dayOffCalendarDTO.setType(dayOffCalendar.getType());
        dayOffCalendarDTO.setIsDayOff(dayOffCalendar.getIsDayOff());
        dayOffCalendarDTO.setDescription(dayOffCalendar.getDescription());
        dayOffCalendarDTO.setCreatedAt(dayOffCalendar.getCreatedAt());
        dayOffCalendarDTO.setUpdatedAt(dayOffCalendar.getUpdatedAt());
        dayOffCalendarDTO.setCreatedBy(dayOffCalendar.getCreatedBy());
        dayOffCalendarDTO.setUpdatedBy(dayOffCalendar.getUpdatedBy());
        dayOffCalendarDTO.setStatus(dayOffCalendar.getStatus());

        return dayOffCalendarDTO;
    }


    private DayOffCalendar mapToEntity(final DayOffCalendarDTO dayOffCalendarDTO, final DayOffCalendar dayOffCalendar) {
        dayOffCalendar.setBranchId(dayOffCalendarDTO.getBranchId());
        dayOffCalendar.setTeamId(dayOffCalendarDTO.getTeamId());
        dayOffCalendar.setDate(dayOffCalendarDTO.getDate());
        dayOffCalendar.setDayOfWeek(dayOffCalendarDTO.getDayOfWeek());
        dayOffCalendar.setType(dayOffCalendarDTO.getType());
        dayOffCalendar.setIsDayOff(dayOffCalendarDTO.getIsDayOff());
        dayOffCalendar.setDescription(dayOffCalendarDTO.getDescription());
        dayOffCalendar.setCreatedAt(dayOffCalendarDTO.getCreatedAt());
        dayOffCalendar.setUpdatedAt(dayOffCalendarDTO.getUpdatedAt());
        dayOffCalendar.setCreatedBy(dayOffCalendarDTO.getCreatedBy());
        dayOffCalendar.setUpdatedBy(dayOffCalendarDTO.getUpdatedBy());
        dayOffCalendar.setStatus(dayOffCalendarDTO.getStatus());

        return dayOffCalendar;
    }

}

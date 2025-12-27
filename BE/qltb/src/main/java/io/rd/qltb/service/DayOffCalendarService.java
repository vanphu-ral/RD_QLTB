package io.rd.qltb.service;

import io.rd.qltb.domain.Branch;
import io.rd.qltb.domain.DayOffCalendar;
import io.rd.qltb.domain.Team;
import io.rd.qltb.domain.UserImage;
import io.rd.qltb.model.DayOffCalendarDTO;
import io.rd.qltb.repos.BranchRepository;
import io.rd.qltb.repos.DayOffCalendarRepository;
import io.rd.qltb.repos.TeamRepository;
import io.rd.qltb.util.NotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static io.rd.qltb.config.ConstantStatusGlobal.DELETED;

@Service
public class DayOffCalendarService {

    private final DayOffCalendarRepository dayOffCalendarRepository;
    private final BranchRepository branchRepository;
    private final TeamRepository teamRepository;
    private final ApplicationEventPublisher publisher;

    public DayOffCalendarService(final DayOffCalendarRepository dayOffCalendarRepository, final ApplicationEventPublisher publisher,
                                 final BranchRepository branchRepository, final TeamRepository teamRepository) {
        this.dayOffCalendarRepository = dayOffCalendarRepository;
        this.branchRepository = branchRepository;
        this.teamRepository = teamRepository;
        this.publisher = publisher;
    }

    public List<DayOffCalendarDTO> findAll() {
        final List<DayOffCalendar> dayOffCalendars = dayOffCalendarRepository.findAllByStatusNotOrderByIdDesc(DELETED);
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
        dayOffCalendar.setStatus(DELETED);
        dayOffCalendarRepository.save(dayOffCalendar);
    }

    public List<DayOffCalendar> getDayOffByTeam(Long teamId) {
        return dayOffCalendarRepository.findAllByTeamIdOrderByFromDateDesc(teamId);
    }

    private DayOffCalendarDTO mapToDTO(final DayOffCalendar dayOffCalendar, final DayOffCalendarDTO dayOffCalendarDTO) {
        dayOffCalendarDTO.setId(dayOffCalendar.getId());
        dayOffCalendarDTO.setFromDate(dayOffCalendar.getFromDate());
        dayOffCalendarDTO.setToDate(dayOffCalendar.getToDate());
        dayOffCalendarDTO.setCreatedAt(dayOffCalendar.getCreatedAt());
        dayOffCalendarDTO.setUpdatedAt(dayOffCalendar.getUpdatedAt());
        dayOffCalendarDTO.setCreatedBy(dayOffCalendar.getCreatedBy());
        dayOffCalendarDTO.setUpdatedBy(dayOffCalendar.getUpdatedBy());
        dayOffCalendarDTO.setStatus(dayOffCalendar.getStatus());

        if (dayOffCalendar.getBranch() != null) {
            Branch branchCopy = new Branch();
            branchCopy.setId(dayOffCalendar.getBranch().getId());
            branchCopy.setCode(dayOffCalendar.getBranch().getCode());
            branchCopy.setName(dayOffCalendar.getBranch().getName());
            branchCopy.setDescription(dayOffCalendar.getBranch().getDescription());
            branchCopy.setCreatedAt(dayOffCalendar.getBranch().getCreatedAt());
            branchCopy.setUpdatedAt(dayOffCalendar.getBranch().getUpdatedAt());
            branchCopy.setCreatedBy(dayOffCalendar.getBranch().getCreatedBy());
            branchCopy.setUpdatedBy(dayOffCalendar.getBranch().getUpdatedBy());
            branchCopy.setStatus(dayOffCalendar.getBranch().getStatus());

            // Xóa các quan hệ con để tránh vòng lặp
            branchCopy.setBranchTeams(null);
            branchCopy.setBranchDevices(null);
            branchCopy.setFactory(null);

            dayOffCalendarDTO.setBranch(branchCopy);
        } else {
            dayOffCalendarDTO.setBranch(null);
        }

        if (dayOffCalendar.getTeam() != null) {
            Team teamCopy = new Team();
            teamCopy.setId(dayOffCalendar.getTeam().getId());
            teamCopy.setCode(dayOffCalendar.getTeam().getCode());
            teamCopy.setName(dayOffCalendar.getTeam().getName());
            teamCopy.setDescription(dayOffCalendar.getTeam().getDescription());
            teamCopy.setCreatedAt(dayOffCalendar.getTeam().getCreatedAt());
            teamCopy.setUpdatedAt(dayOffCalendar.getTeam().getUpdatedAt());
            teamCopy.setCreatedBy(dayOffCalendar.getTeam().getCreatedBy());
            teamCopy.setUpdatedBy(dayOffCalendar.getTeam().getUpdatedBy());
            teamCopy.setStatus(dayOffCalendar.getTeam().getStatus());

            // Xóa các quan hệ con để tránh vòng lặp
            teamCopy.setTeamLines(null);
            teamCopy.setTeamDevices(null);

            dayOffCalendarDTO.setTeam(teamCopy);
        } else {
            dayOffCalendarDTO.setTeam(null);
        }

        return dayOffCalendarDTO;
    }


    private DayOffCalendar mapToEntity(final DayOffCalendarDTO dayOffCalendarDTO, final DayOffCalendar dayOffCalendar) {
        dayOffCalendar.setFromDate(dayOffCalendarDTO.getFromDate());
        dayOffCalendar.setToDate(dayOffCalendarDTO.getToDate());
        dayOffCalendar.setCreatedAt(dayOffCalendarDTO.getCreatedAt());
        dayOffCalendar.setUpdatedAt(dayOffCalendarDTO.getUpdatedAt());
        dayOffCalendar.setCreatedBy(dayOffCalendarDTO.getCreatedBy());
        dayOffCalendar.setUpdatedBy(dayOffCalendarDTO.getUpdatedBy());
        dayOffCalendar.setStatus(dayOffCalendarDTO.getStatus());

        final Branch branch = dayOffCalendarDTO.getBranch() == null ? null : branchRepository.findById(dayOffCalendarDTO.getBranch().getId())
                .orElseThrow(() -> new NotFoundException("branch not found"));
        dayOffCalendar.setBranch(branch);

        final Team team = dayOffCalendarDTO.getTeam() == null ? null : teamRepository.findById(dayOffCalendarDTO.getTeam().getId())
                .orElseThrow(() -> new NotFoundException("team not found"));
        dayOffCalendar.setTeam(team);

        return dayOffCalendar;
    }

}

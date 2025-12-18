package io.rd.qltb.service;

import io.rd.qltb.domain.Factory;
import io.rd.qltb.events.BeforeDeleteFactory;
import io.rd.qltb.model.FactoryDTO;
import io.rd.qltb.repos.FactoryRepository;
import io.rd.qltb.util.NotFoundException;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import static io.rd.qltb.config.GlobalConfig.DELETED;


@Service
public class FactoryService {

    private final FactoryRepository factoryRepository;
    private final ApplicationEventPublisher publisher;

    public FactoryService(final FactoryRepository factoryRepository,
            final ApplicationEventPublisher publisher) {
        this.factoryRepository = factoryRepository;
        this.publisher = publisher;
    }

    public List<FactoryDTO> findAll() {
        final List<Factory> factories = factoryRepository.findAllByStatusNotOrderByIdDesc(DELETED);
        return factories.stream()
                .map(factory -> mapToDTO(factory, new FactoryDTO()))
                .toList();
    }

    public FactoryDTO get(final Long id) {
        return factoryRepository.findById(id)
                .map(factory -> mapToDTO(factory, new FactoryDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final FactoryDTO factoryDTO) {
        final Factory factory = new Factory();
        mapToEntity(factoryDTO, factory);
        return factoryRepository.save(factory).getId();
    }

    public void update(final Long id, final FactoryDTO factoryDTO) {
        final Factory factory = factoryRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(factoryDTO, factory);
        factoryRepository.save(factory);
    }

    public void delete(final Long id) {
        final Factory factory = factoryRepository.findById(id)
                .orElseThrow(NotFoundException::new);
//        publisher.publishEvent(new BeforeDeleteFactory(id));
        factory.setStatus(DELETED);
        factoryRepository.save(factory);
    }

    private FactoryDTO mapToDTO(final Factory factory, final FactoryDTO factoryDTO) {
        factoryDTO.setId(factory.getId());
        factoryDTO.setCode(factory.getCode());
        factoryDTO.setName(factory.getName());
        factoryDTO.setDescription(factory.getDescription());
        factoryDTO.setCreatedAt(factory.getCreatedAt());
        factoryDTO.setUpdatedAt(factory.getUpdatedAt());
        factoryDTO.setCreatedBy(factory.getCreatedBy());
        factoryDTO.setUpdatedBy(factory.getUpdatedBy());
        factoryDTO.setStatus(factory.getStatus());
        return factoryDTO;
    }

    private Factory mapToEntity(final FactoryDTO factoryDTO, final Factory factory) {
        factory.setCode(factoryDTO.getCode());
        factory.setName(factoryDTO.getName());
        factory.setDescription(factoryDTO.getDescription());
        factory.setCreatedAt(factoryDTO.getCreatedAt());
        factory.setUpdatedAt(factoryDTO.getUpdatedAt());
        factory.setCreatedBy(factoryDTO.getCreatedBy());
        factory.setUpdatedBy(factoryDTO.getUpdatedBy());
        factory.setStatus(factoryDTO.getStatus());
        return factory;
    }

}

package rd.project.qltb.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import rd.project.qltb.domain.Branch;
import rd.project.qltb.domain.Factory;
import rd.project.qltb.domain.Form;
import rd.project.qltb.model.FactoryDTO;
import rd.project.qltb.repos.BranchRepository;
import rd.project.qltb.repos.FactoryRepository;
import rd.project.qltb.repos.FormRepository;
import rd.project.qltb.util.NotFoundException;
import rd.project.qltb.util.ReferencedWarning;


@Service
public class FactoryService {

    private final FactoryRepository factoryRepository;
    private final BranchRepository branchRepository;
    private final FormRepository formRepository;

    public FactoryService(final FactoryRepository factoryRepository,
            final BranchRepository branchRepository, final FormRepository formRepository) {
        this.factoryRepository = factoryRepository;
        this.branchRepository = branchRepository;
        this.formRepository = formRepository;
    }

    public List<FactoryDTO> findAll() {
        final List<Factory> factories = factoryRepository.findAll(Sort.by("id"));
        return factories.stream()
                .map(factory -> mapToDTO(factory, new FactoryDTO()))
                .toList();
    }

    public FactoryDTO get(final Integer id) {
        return factoryRepository.findById(id)
                .map(factory -> mapToDTO(factory, new FactoryDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final FactoryDTO factoryDTO) {
        final Factory factory = new Factory();
        mapToEntity(factoryDTO, factory);
        return factoryRepository.save(factory).getId();
    }

    public void update(final Integer id, final FactoryDTO factoryDTO) {
        final Factory factory = factoryRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(factoryDTO, factory);
        factoryRepository.save(factory);
    }

    public void delete(final Integer id) {
        factoryRepository.deleteById(id);
    }

    private FactoryDTO mapToDTO(final Factory factory, final FactoryDTO factoryDTO) {
        factoryDTO.setId(factory.getId());
        factoryDTO.setCode(factory.getCode());
        factoryDTO.setName(factory.getName());
        factoryDTO.setDescription(factory.getDescription());
        factoryDTO.setCreatedAt(factory.getCreatedAt());
        factoryDTO.setUpdatedAt(factory.getUpdatedAt());
        factoryDTO.setCreatedBy(factory.getCreatedBy());
        return factoryDTO;
    }

    private Factory mapToEntity(final FactoryDTO factoryDTO, final Factory factory) {
        factory.setCode(factoryDTO.getCode());
        factory.setName(factoryDTO.getName());
        factory.setDescription(factoryDTO.getDescription());
        factory.setCreatedAt(factoryDTO.getCreatedAt());
        factory.setUpdatedAt(factoryDTO.getUpdatedAt());
        factory.setCreatedBy(factoryDTO.getCreatedBy());
        return factory;
    }

    public ReferencedWarning getReferencedWarning(final Integer id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Factory factory = factoryRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Branch factoryBranch = branchRepository.findFirstByFactory(factory);
        if (factoryBranch != null) {
            referencedWarning.setKey("factory.branch.factory.referenced");
            referencedWarning.addParam(factoryBranch.getId());
            return referencedWarning;
        }
        final Form factoryForm = formRepository.findFirstByFactory(factory);
        if (factoryForm != null) {
            referencedWarning.setKey("factory.form.factory.referenced");
            referencedWarning.addParam(factoryForm.getId());
            return referencedWarning;
        }
        return null;
    }

}

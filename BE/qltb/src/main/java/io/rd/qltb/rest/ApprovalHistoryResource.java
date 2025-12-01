package io.rd.qltb.rest;

import io.rd.qltb.model.ApprovalHistoryDTO;
import io.rd.qltb.service.ApprovalHistoryService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@RestController
@RequestMapping("/api/approval-history")
public class ApprovalHistoryResource {

    @Autowired
    private ApprovalHistoryService service;

    @PostMapping
    public ApprovalHistoryDTO create(@RequestBody ApprovalHistoryDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public ApprovalHistoryDTO update(@PathVariable Long id, @RequestBody ApprovalHistoryDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

//    @GetMapping
//    public List<ApprovalHistoryDTO> getAll() {
//        return service.getAll();
//    }

    @GetMapping("/{id}")
    public ApprovalHistoryDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }
}

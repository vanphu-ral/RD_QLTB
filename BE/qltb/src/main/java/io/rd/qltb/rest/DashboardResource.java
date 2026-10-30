package io.rd.qltb.rest;

import io.rd.qltb.model.DashboardDTO;
import io.rd.qltb.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardResource {
    @Autowired
    private DashboardService dashboardService;
    @PostMapping("/data")
    public DashboardDTO getDashboardData() {
        return dashboardService.getDashboardData();
    }
}

package com.ecommerce.library.service.impl;

import com.ecommerce.library.model.Dashboard;
import com.ecommerce.library.repository.DashboardRepository;
import com.ecommerce.library.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {
    @Autowired
    private DashboardRepository dashboardRepository;

    @Override
    public List<Dashboard> findDashboardForExport() {
        return dashboardRepository.findDashboardForExport();
    }
}

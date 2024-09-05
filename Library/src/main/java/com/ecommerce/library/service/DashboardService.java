package com.ecommerce.library.service;

import com.ecommerce.library.model.Dashboard;

import java.util.List;

public interface DashboardService {
    public List<Dashboard> findDashboardForExport();

}

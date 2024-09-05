package com.ecommerce.library.repository;

import com.ecommerce.library.model.Dashboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DashboardRepository extends JpaRepository<Dashboard, Integer> {
    Dashboard findByOrderId(Long orderId);

    @Query(value = "select d from Dashboard d ORDER BY d.datePaid asc")
    List<Dashboard> findDashboardForExport();
}

package com.rentflow.repository;

import com.rentflow.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> findByIsActive(Integer isActive);
    List<Bill> findByPropertyIdAndIsActive(Long propertyId, Integer isActive);
    List<Bill> findByTenantIdAndIsActive(Long tenantId, Integer isActive);
    List<Bill> findByStatusAndIsActive(String status, Integer isActive);
    List<Bill> findByCategoryAndIsActive(String category, Integer isActive);
    List<Bill> findByDueDateBeforeAndStatusAndIsActive(LocalDate date, String status, Integer isActive);
    List<Bill> findByDueDateBetweenAndIsActive(LocalDate start, LocalDate end, Integer isActive);
}

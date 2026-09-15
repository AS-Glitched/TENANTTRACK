package com.rentflow.repository;

import com.rentflow.model.RentPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RentPaymentRepository extends JpaRepository<RentPayment, Long> {
    List<RentPayment> findByTenantIdOrderByPeriodMonthDesc(Long tenantId);
    Optional<RentPayment> findByTenantIdAndPeriodMonth(Long tenantId, String periodMonth);
    List<RentPayment> findByStatus(String status);
    List<RentPayment> findByStatusIn(List<String> statuses);
}

package com.rentflow.repository;

import com.rentflow.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
    List<Tenant> findByPropertyId(Long propertyId);
    List<Tenant> findByStatus(String status);
    List<Tenant> findByPropertyIdAndStatus(Long propertyId, String status);
}

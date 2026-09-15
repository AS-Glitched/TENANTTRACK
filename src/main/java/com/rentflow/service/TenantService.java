package com.rentflow.service;

import com.rentflow.model.Tenant;
import com.rentflow.repository.TenantRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TenantService {

    private final TenantRepository tenantRepository;

    public TenantService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    public List<Tenant> findAll() {
        return tenantRepository.findAll();
    }

    public Optional<Tenant> findById(Long id) {
        return tenantRepository.findById(id);
    }

    public Tenant save(Tenant tenant) {
        return tenantRepository.save(tenant);
    }

    public void deleteById(Long id) {
        tenantRepository.deleteById(id);
    }

    public List<Tenant> findByPropertyId(Long propertyId) {
        return tenantRepository.findByPropertyId(propertyId);
    }

    public List<Tenant> findByStatus(String status) {
        return tenantRepository.findByStatus(status);
    }

    public List<Tenant> findByPropertyIdAndStatus(Long propertyId, String status) {
        return tenantRepository.findByPropertyIdAndStatus(propertyId, status);
    }

    public List<Tenant> findFiltered(Long propertyId, String status) {
        if (propertyId != null && status != null && !status.isEmpty()) {
            return findByPropertyIdAndStatus(propertyId, status);
        } else if (propertyId != null) {
            return findByPropertyId(propertyId);
        } else if (status != null && !status.isEmpty()) {
            return findByStatus(status);
        }
        return findAll();
    }

    public List<Tenant> findActiveAndNotice() {
        List<Tenant> active = tenantRepository.findByStatus("ACTIVE");
        active.addAll(tenantRepository.findByStatus("NOTICE_GIVEN"));
        return active;
    }
}

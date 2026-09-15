package com.rentflow.service;

import com.rentflow.model.Bill;
import com.rentflow.repository.BillRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BillService {

    private final BillRepository billRepository;

    public BillService(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    public List<Bill> findAllActive() {
        return billRepository.findByIsActive(1);
    }

    public Optional<Bill> findById(Long id) {
        return billRepository.findById(id);
    }

    public Bill save(Bill bill) {
        return billRepository.save(bill);
    }

    /**
     * Soft delete — sets is_active = 0.
     */
    public void softDelete(Long id) {
        billRepository.findById(id).ifPresent(bill -> {
            bill.setIsActive(0);
            billRepository.save(bill);
        });
    }

    /**
     * Hard delete — removes from database.
     */
    public void hardDelete(Long id) {
        billRepository.deleteById(id);
    }

    public void markAsPaid(Long id) {
        billRepository.findById(id).ifPresent(bill -> {
            bill.setStatus("PAID");
            billRepository.save(bill);
        });
    }

    public List<Bill> findByPropertyId(Long propertyId) {
        return billRepository.findByPropertyIdAndIsActive(propertyId, 1);
    }

    public List<Bill> findByTenantId(Long tenantId) {
        return billRepository.findByTenantIdAndIsActive(tenantId, 1);
    }

    public List<Bill> findByCategory(String category) {
        return billRepository.findByCategoryAndIsActive(category, 1);
    }

    public List<Bill> findByStatus(String status) {
        return billRepository.findByStatusAndIsActive(status, 1);
    }

    public List<Bill> findOverdueBills() {
        return billRepository.findByDueDateBeforeAndStatusAndIsActive(LocalDate.now(), "UNPAID", 1);
    }

    public List<Bill> findUpcomingBills(int days) {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(days);
        return billRepository.findByDueDateBetweenAndIsActive(start, end, 1);
    }
}

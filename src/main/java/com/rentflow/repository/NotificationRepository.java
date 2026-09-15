package com.rentflow.repository;

import com.rentflow.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByIsReadOrderByCreatedAtDesc(Integer isRead);
    List<Notification> findAllByOrderByCreatedAtDesc();
    long countByIsRead(Integer isRead);
    List<Notification> findTop5ByOrderByCreatedAtDesc();
    List<Notification> findByTenantIdAndType(Long tenantId, String type);
}

package com.rentflow.service;

import com.rentflow.model.Notification;
import com.rentflow.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<Notification> findAll() {
        return notificationRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Notification> findUnread() {
        return notificationRepository.findByIsReadOrderByCreatedAtDesc(0);
    }

    public long countUnread() {
        return notificationRepository.countByIsRead(0);
    }

    public List<Notification> findLatest5() {
        return notificationRepository.findTop5ByOrderByCreatedAtDesc();
    }

    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    public void markAsRead(Long id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setIsRead(1);
            notificationRepository.save(n);
        });
    }

    public void markAllAsRead() {
        List<Notification> unread = notificationRepository.findByIsReadOrderByCreatedAtDesc(0);
        for (Notification n : unread) {
            n.setIsRead(1);
            notificationRepository.save(n);
        }
    }

    public Optional<Notification> findById(Long id) {
        return notificationRepository.findById(id);
    }
}

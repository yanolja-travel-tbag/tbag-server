package com.tbag.tbag_backend.domain.travel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tbag.tbag_backend.domain.User.entity.User;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "travel_request")
public class TravelRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "name", nullable = false)
    private String name;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public TravelRequest(User user, LocalDate startDate, LocalDate endDate, String name) {
        this.user = user;
        this.startDate = startDate;
        this.endDate = endDate;
        this.name = name;
    }
}

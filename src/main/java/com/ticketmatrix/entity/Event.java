package com.ticketmatrix.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(
        name = "events",
        indexes = {
                @Index(name = "idx_event_date", columnList = "event_date"),
                @Index(name = "idx_event_title", columnList = "title")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event extends BaseEntity{

    @Column(nullable =false, length = 150)
    private String title;
}

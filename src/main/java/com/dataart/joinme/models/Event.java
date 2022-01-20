package com.dataart.joinme.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@Table(name = "events")
@Entity
@Getter
@Setter
@ToString
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;
    private String name;
    private String description;
    private Instant date;
    private String linkAva;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", referencedColumnName = "id")
    private User creatorId;
    @ManyToMany(mappedBy = "events", cascade = CascadeType.ALL)
    private List<User> users;
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<LikeEvent> likeEvents;
    @CreatedDate
    private Instant createdDate = Instant.now();
    @LastModifiedDate
    private Instant updatedDate = Instant.now();

    public Event(String name, String description, Instant date, String linkAva, User creatorId) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.linkAva = linkAva;
        this.creatorId = creatorId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Event event = (Event) o;
        return id != null && Objects.equals(id, event.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

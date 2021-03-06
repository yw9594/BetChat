package com.study.loge.betchat.model.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

// Room Entity를 표현하기 위한 Entity 클래스입니다.
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String roomKey;
    private LocalDateTime createdAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "room")
    private List<Participate> participate;

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", roomKey='" + roomKey + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}

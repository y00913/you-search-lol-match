package com.example.lol.repository;

import com.example.lol.dto.RankTypeSolo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RankTypeSoloRepository extends JpaRepository<RankTypeSolo, Long> {
    RankTypeSolo findById(String id);
}

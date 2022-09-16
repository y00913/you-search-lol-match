package com.example.lol.repository;

import com.example.lol.dto.RankType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RankTypeRepository extends JpaRepository<RankType, Long> {
    Optional<RankType> findById(String id);
}

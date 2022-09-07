package com.example.lol.repository;

import com.example.lol.dto.RankTypeFlex;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RankTypeFlexRepository extends JpaRepository<RankTypeFlex, Long> {
    RankTypeFlex findById(String id);
}

package com.example.lol.repository;

import com.example.lol.dto.Match;
import com.example.lol.dto.StartTimeMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {
    Page<Match> findByPuuid(Pageable pageable, String puuid);
    Optional<StartTimeMapping> findTop1ByPuuid(String puuid, Sort sort);
}

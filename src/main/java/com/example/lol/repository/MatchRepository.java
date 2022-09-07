package com.example.lol.repository;

import com.example.lol.dto.Match;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, Long> {
    Match findByMatchId(String matchId);
}

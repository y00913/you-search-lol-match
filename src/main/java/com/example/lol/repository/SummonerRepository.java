package com.example.lol.repository;

import com.example.lol.dto.Summoner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SummonerRepository extends JpaRepository<Summoner, Long> {
    Summoner findByPuuid(String puuid);
}

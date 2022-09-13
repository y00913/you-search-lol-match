package com.example.lol.repository;

import com.example.lol.dto.Summoner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SummonerRepository extends JpaRepository<Summoner, Long> {
    Summoner findByPuuid(String puuid);
    Optional<Summoner> findByName(String summonerName);
}

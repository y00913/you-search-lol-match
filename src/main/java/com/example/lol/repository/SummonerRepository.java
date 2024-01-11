package com.example.lol.repository;

import com.example.lol.dto.Summoner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SummonerRepository extends JpaRepository<Summoner, Long> {
    Optional<Summoner> findByNameAndTagLine(String summonerName, String tagLine);
}

package com.example.lol.repository;

import com.example.lol.dto.MyInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyInfoRepository extends JpaRepository<MyInfo, Long> {
}

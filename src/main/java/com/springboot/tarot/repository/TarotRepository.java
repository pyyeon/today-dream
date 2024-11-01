package com.springboot.tarot.repository;

import com.springboot.tarot.entity.Tarot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TarotRepository extends JpaRepository<Tarot, Long> {

}

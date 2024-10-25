package com.springboot.tarot.repository;

import com.springboot.tarot.entity.Tarot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TarotRepository extends JpaRepository<Tarot, Long> {

}

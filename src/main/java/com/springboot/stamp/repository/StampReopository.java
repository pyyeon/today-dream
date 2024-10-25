package com.springboot.stamp.repository;

import com.springboot.stamp.entity.Stamp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StampReopository extends JpaRepository<Stamp, Long> {
}

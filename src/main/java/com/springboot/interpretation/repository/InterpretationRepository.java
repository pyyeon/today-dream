package com.springboot.interpretation.repository;

import com.springboot.interpretation.entity.Interpretation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterpretationRepository  extends JpaRepository<Interpretation, Long> {
}

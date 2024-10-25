package com.springboot.sharing.repository;

import com.springboot.sharing.entity.Sharing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SharingRepository extends JpaRepository<Sharing, Long> {

}

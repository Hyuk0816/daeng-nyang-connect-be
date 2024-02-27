package com.git.backend.daeng_nyang_connect.mate.board.repository;

import com.git.backend.daeng_nyang_connect.mate.board.entity.Mate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MateRepository extends JpaRepository<Mate, Long> {

    Page<Mate> findByTextContaining(String keyword, Pageable pageable);
    
    List<Mate> findByUserEmail(String userEmail);
}

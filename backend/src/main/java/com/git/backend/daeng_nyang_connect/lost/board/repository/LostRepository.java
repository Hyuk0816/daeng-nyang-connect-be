package com.git.backend.daeng_nyang_connect.lost.board.repository;

import com.git.backend.daeng_nyang_connect.lost.board.entity.Lost;
import com.git.backend.daeng_nyang_connect.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LostRepository extends JpaRepository<Lost, Long> {
    List<Lost> findByUser(User user);
    List<Lost> findByTextContaining(String keyword);
}

package com.git.backend.daeng_nyang_connect.tips.board.repository;

import com.git.backend.daeng_nyang_connect.tips.board.entity.Tips;
import com.git.backend.daeng_nyang_connect.tips.board.entity.TipsBoardLike;
import com.git.backend.daeng_nyang_connect.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TipsBoardRepository extends JpaRepository<Tips, Long> {
    List<Tips> findByUser(User user);

    List<Tips>findByTitleContaining(String keyword);

    List<Tips> findByTipsBoardId(Long tipsBoardId);
}

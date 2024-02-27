package com.git.backend.daeng_nyang_connect.tips.board.repository;

import com.git.backend.daeng_nyang_connect.tips.board.entity.Tips;
import com.git.backend.daeng_nyang_connect.tips.board.entity.TipsImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TipsImageRepository extends JpaRepository<TipsImage, Long> {
    Optional<List<TipsImage>> findByTips(Tips tips);
}

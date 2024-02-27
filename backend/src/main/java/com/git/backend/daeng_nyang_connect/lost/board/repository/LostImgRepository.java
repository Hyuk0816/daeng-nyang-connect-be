package com.git.backend.daeng_nyang_connect.lost.board.repository;

import com.git.backend.daeng_nyang_connect.lost.board.entity.Lost;
import com.git.backend.daeng_nyang_connect.lost.board.entity.LostImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LostImgRepository extends JpaRepository<LostImage, Long> {
    Optional<List<LostImage>> findByLost(Lost lost);
}

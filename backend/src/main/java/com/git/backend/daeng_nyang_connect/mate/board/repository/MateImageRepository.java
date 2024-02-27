package com.git.backend.daeng_nyang_connect.mate.board.repository;

import com.git.backend.daeng_nyang_connect.mate.board.entity.Mate;
import com.git.backend.daeng_nyang_connect.mate.board.entity.MateImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MateImageRepository extends JpaRepository<MateImage, Long> {
    Optional<List<MateImage>> findByMate(Mate mate);
}

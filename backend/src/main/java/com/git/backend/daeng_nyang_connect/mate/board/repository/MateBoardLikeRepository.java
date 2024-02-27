package com.git.backend.daeng_nyang_connect.mate.board.repository;

import com.git.backend.daeng_nyang_connect.mate.board.entity.Mate;
import com.git.backend.daeng_nyang_connect.mate.board.entity.MateBoardLike;
import com.git.backend.daeng_nyang_connect.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MateBoardLikeRepository extends JpaRepository<MateBoardLike, Long> {
    Optional<MateBoardLike> findByMateAndUser(Mate mate, User user);

    List<MateBoardLike> findByUser(User user);

    List<MateBoardLike> findAllByUser(User user);
}

package com.git.backend.daeng_nyang_connect.mate.comments.repository;

import com.git.backend.daeng_nyang_connect.mate.comments.entity.MateComments;
import com.git.backend.daeng_nyang_connect.mate.comments.entity.MateCommentsLike;
import com.git.backend.daeng_nyang_connect.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MateCommentsLikeRepository extends JpaRepository<MateCommentsLike, Long> {
    Optional<MateCommentsLike> findByMateCommentsAndUser(MateComments mateComments, User user);
}

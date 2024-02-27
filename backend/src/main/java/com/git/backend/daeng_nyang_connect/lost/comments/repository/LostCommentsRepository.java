package com.git.backend.daeng_nyang_connect.lost.comments.repository;

import com.git.backend.daeng_nyang_connect.lost.board.entity.Lost;
import com.git.backend.daeng_nyang_connect.lost.comments.entity.LostComments;
import com.git.backend.daeng_nyang_connect.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LostCommentsRepository extends JpaRepository<LostComments, Long> {
    List<LostComments> findByUser(User user);
    List<LostComments> findByLost(Lost byId);
}

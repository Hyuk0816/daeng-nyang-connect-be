package com.git.backend.daeng_nyang_connect.tips.comments.repository;

import com.git.backend.daeng_nyang_connect.tips.board.entity.Tips;
import com.git.backend.daeng_nyang_connect.tips.comments.entity.TipsComments;
import com.git.backend.daeng_nyang_connect.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipsCommentsRepository extends JpaRepository<TipsComments, Long> {
    List<TipsComments> findByUser(User user);

    List<TipsComments> findByTips(Tips byId);

}

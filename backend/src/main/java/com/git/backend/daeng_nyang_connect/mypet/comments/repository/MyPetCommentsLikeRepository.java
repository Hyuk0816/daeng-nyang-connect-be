package com.git.backend.daeng_nyang_connect.mypet.comments.repository;

import com.git.backend.daeng_nyang_connect.mypet.comments.entity.MyPetComments;
import com.git.backend.daeng_nyang_connect.mypet.comments.entity.MyPetCommentsLike;
import com.git.backend.daeng_nyang_connect.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MyPetCommentsLikeRepository extends JpaRepository<MyPetCommentsLike, Long> {
    Optional<MyPetCommentsLike> findByMyPetCommentsAndUser(MyPetComments myPetComments, User user);
}

package com.git.backend.daeng_nyang_connect.user.repository;

import com.git.backend.daeng_nyang_connect.user.entity.MyPage;
import com.git.backend.daeng_nyang_connect.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyPageRepository extends JpaRepository<MyPage, Long> {
    MyPage findByUser(User user);

}

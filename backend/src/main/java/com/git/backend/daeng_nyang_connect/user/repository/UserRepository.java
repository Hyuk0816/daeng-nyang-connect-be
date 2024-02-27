package com.git.backend.daeng_nyang_connect.user.repository;

import com.git.backend.daeng_nyang_connect.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    List<User > findEmailByNameAndMobile(String name, String mobile);

    User findByNameAndMobileAndEmail(String name, String mobile, String email);

    User findByNickname(String nickname);

    User findByName(String name);

    User findByNameAndNickname(String name, String nickname);
}


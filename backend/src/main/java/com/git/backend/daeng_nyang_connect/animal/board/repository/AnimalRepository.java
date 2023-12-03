package com.git.backend.daeng_nyang_connect.animal.board.repository;

import com.git.backend.daeng_nyang_connect.animal.board.entity.Animal;
import com.git.backend.daeng_nyang_connect.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnimalRepository extends JpaRepository<Animal, Long> {
    Optional<Animal> findByUser(User user);
}

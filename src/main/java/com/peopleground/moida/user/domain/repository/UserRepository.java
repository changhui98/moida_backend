package com.peopleground.moida.user.domain.repository;

import com.peopleground.moida.user.domain.entity.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepository {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    User save(User user);

    Page<User> findAllUsers(Pageable pageable);
}

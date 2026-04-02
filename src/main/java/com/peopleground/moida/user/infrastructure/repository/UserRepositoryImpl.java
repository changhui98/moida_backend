package com.peopleground.moida.user.infrastructure.repository;

import com.peopleground.moida.user.domain.entity.User;
import com.peopleground.moida.user.domain.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final UserQueryRepository userQueryRepository;

    @Override
    public Optional<User> findByUsername(String username) {

        return userJpaRepository.findByUsername(username);
    }

    @Override
    public boolean existsByUsername(String username) {

        return userJpaRepository.existsByUsername(username);
    }

    @Override
    public User save(User user) {

        return userJpaRepository.save(user);
    }

    @Override
    public Page<User> findAllUsers(Pageable pageable) {

        return userQueryRepository.findAllUsers(pageable);
    }

    @Override
    public Page<User> findAllUserForAdmin(Pageable pageable) {

        return userQueryRepository.findAllUsersForAdmin(pageable);
    }
}

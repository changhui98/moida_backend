package com.peopleground.moida.group.infrastructure.repository;

import com.peopleground.moida.group.domain.entity.Group;
import com.peopleground.moida.group.domain.repository.GroupRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GroupRepositoryImpl implements GroupRepository {

    private final GroupJpaRepository groupJpaRepository;
    private final GroupQueryRepository groupQueryRepository;

    @Override
    public Group save(Group group) {
        return groupJpaRepository.save(group);
    }

    @Override
    public Optional<Group> findById(Long id) {
        return groupQueryRepository.findById(id);
    }

    @Override
    public Page<Group> findAll(Pageable pageable) {
        return groupQueryRepository.findAll(pageable);
    }

    @Override
    public Page<Group> findByMemberUsername(String username, Pageable pageable) {
        return groupQueryRepository.findByMemberUsername(username, pageable);
    }
}

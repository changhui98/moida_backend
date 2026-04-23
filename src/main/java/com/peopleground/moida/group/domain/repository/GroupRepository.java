package com.peopleground.moida.group.domain.repository;

import com.peopleground.moida.group.domain.entity.Group;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GroupRepository {

    Group save(Group group);

    Optional<Group> findById(Long id);

    Page<Group> findAll(Pageable pageable);

    Page<Group> findByMemberUsername(String username, Pageable pageable);
}

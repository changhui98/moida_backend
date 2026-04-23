package com.peopleground.moida.group.infrastructure.repository;

import com.peopleground.moida.group.domain.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupJpaRepository extends JpaRepository<Group, Long> {
}

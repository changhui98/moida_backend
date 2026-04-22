package com.peopleground.moida.comment.infrastructure.repository;

import com.peopleground.moida.comment.domain.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentJpaRepository extends JpaRepository<Comment, Long> {

}

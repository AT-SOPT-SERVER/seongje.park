package org.sopt.repository;

import org.sopt.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<User,Long> {
}

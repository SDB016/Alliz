package com.alliz.account;

import com.alliz.domain.Child;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface ChildRepository extends JpaRepository<Child, Long> {
}

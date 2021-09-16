package com.alliz.child;

import com.alliz.child.Child;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface ChildRepository extends JpaRepository<Child, Long> {
    Child findByName(String name);
}

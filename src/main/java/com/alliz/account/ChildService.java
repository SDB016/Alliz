package com.alliz.account;

import com.alliz.domain.Child;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChildService {
    private final ChildRepository childRepository;

    public Child saveChild(ChildForm childForm) {
        return childRepository.save(Child.builder().name(childForm.getChildName()).build());
    }

    public Child findChild(ChildForm childForm) {
        Child child = childRepository.findByName(childForm.getChildName());
        if (child == null) {
            throw new IllegalArgumentException(childForm.getChildName() + "에 해당하는 학생이 없습니다.");
        }
        return child;
    }
}

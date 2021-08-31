package com.alliz.account;

import com.alliz.domain.Account;
import com.alliz.domain.Child;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChildService {
    private final ChildRepository childRepository;
    private final ModelMapper modelMapper;

    public Child saveChild(ChildForm childForm) {
        return childRepository.save(Child.builder().name(childForm.getName()).build());
    }

    public Child findChild(ChildForm childForm) {
        Child child = childRepository.findByName(childForm.getName());
        if (child == null) {
            throw new IllegalArgumentException(childForm.getName() + "에 해당하는 학생이 없습니다.");
        }
        return child;
    }

    public void updateProfile(Child child, ChildForm childForm) {
        modelMapper.map(childForm, child);
        childRepository.save(child);
    }
}

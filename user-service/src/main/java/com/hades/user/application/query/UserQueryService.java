package com.hades.user.application.query;

import com.hades.common.model.PageResult;
import com.hades.user.application.dto.UserResult;
import com.hades.user.infrastructure.persistence.entity.UserJpaEntity;
import com.hades.user.infrastructure.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService {

    private final UserJpaRepository userJpaRepository;
    private final UserQueryMapper userQueryMapper;

    public UserResult getUserById(UUID id) {
        var entity = userJpaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + id));
        return userQueryMapper.toResult(entity);
    }

    public PageResult<UserResult> getAllUsers(int page, int size) {
        Page<UserJpaEntity> jpaPage =
                userJpaRepository.findAll(PageRequest.of(page, size));
        return userQueryMapper.toPageResult(jpaPage);
    }
}

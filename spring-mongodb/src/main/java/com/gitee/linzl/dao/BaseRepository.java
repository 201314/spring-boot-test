package com.gitee.linzl.dao;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.QueryByExampleExecutor;

@NoRepositoryBean
public interface BaseRepository<T, ID> extends CrudRepository<T, ID>, QueryByExampleExecutor<T> {
    default <S extends T> Page<S> findAll(Example<S> var1, int page,
                                          int pageSize) {
        page = page <= 0 ? 1 : page;
        pageSize = pageSize <= 0 ? 15 : pageSize;
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        return findAll(var1, pageable);
    }
}
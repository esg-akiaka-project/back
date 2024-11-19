package com.haru.doyak.harudoyak.repository.querydsl.impl;

import com.haru.doyak.harudoyak.repository.querydsl.FileCustomRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.haru.doyak.harudoyak.entity.QFile.file;

@Repository
@RequiredArgsConstructor
public class FileCustomRepositoryImpl implements FileCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public long fileDelete(Long fileId) {
        long fileDelete = jpaQueryFactory
                .delete(file)
                .where(file.fileId.eq(fileId))
                .execute();
        return fileDelete;
    }
}

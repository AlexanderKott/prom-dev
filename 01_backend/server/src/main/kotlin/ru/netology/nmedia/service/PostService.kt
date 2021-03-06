package ru.netology.nmedia.service

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.exception.NotFoundException
import ru.netology.nmedia.repository.PostRepository
import java.time.OffsetDateTime

@Service
class PostService(private val repository: PostRepository) {
    fun getAll(): List<Post> = repository
            .findAll(Sort.by(Sort.Direction.DESC, "id"))
            .map { it.toDto() }

    fun getById(id: Long): Post = repository
            .findById(id)
            .map { it.toDto() }
            .orElseThrow(::NotFoundException)

    fun save(dto: Post): Post = repository
        .findById(dto.id)
        .orElse(
            PostEntity.fromDto(
                dto.copy(
                    likes = 0,
                    likedByMe = false,
                    published = OffsetDateTime.now().toEpochSecond()
                )
            )
        )
        .let {
            if (it.id == 0L) repository.save(it) else it.content = dto.content
            it
        }.toDto()

    fun removeById(id: Long): Unit = repository.deleteById(id)
}
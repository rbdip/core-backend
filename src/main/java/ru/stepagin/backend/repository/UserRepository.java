package ru.stepagin.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.stepagin.backend.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @Query("select u from UserEntity u where upper(u.username) = upper(:username) and u.isDeleted = false")
    UserEntity findByUsername(@Param("username") String username);

    @Query("select (count(u) > 0) from UserEntity u where upper(u.username) = upper(:username)")
    boolean existsByUsername(@Param("username") String username);

    @Transactional
    @Modifying
    @Query("delete from UserEntity u where upper(u.username) = upper(:username)")
    void deleteByUsername(String username);

}

package ru.stepagin.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.stepagin.backend.entity.FavouriteProjectEntity;

import java.util.List;

@Repository
public interface FavouriteProjectRepository extends JpaRepository<FavouriteProjectEntity, Long> {
    @Query("""
            select f from FavouriteProjectEntity f where upper(f.user.username) = upper(:username)
            order by f.createdOn desc""")
    List<FavouriteProjectEntity> findAllByUsername(@Param("username") String username); // todo pageable

    @Query("""
            select f from FavouriteProjectEntity f
            where upper(f.user.username) = upper(:username) and f.projectCard.id = :projectCardId""")
    FavouriteProjectEntity findByUsernameAndProjectCard(
            @Param("username") String username,
            @Param("projectCardId") Long projectCardId
    );


}

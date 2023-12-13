package com.example.easytourneybe.category.interfaces;

import com.example.easytourneybe.category.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category,Long> {
    @Query("SELECT count(c) FROM Category c WHERE LOWER(c.categoryName) LIKE LOWER(CONCAT('%', :keyword, '%')) AND c.isDeleted = false")
    long totalCategory( @Param("keyword") String keyword);
    @Query("SELECT c FROM Category c WHERE LOWER(c.categoryName) LIKE LOWER(CONCAT('%', :keyword, '%')) AND c.isDeleted = false")
    List<Category> findCategoriesByName(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT c FROM Category c WHERE LOWER(c.categoryName) = LOWER(:categoryName) AND c.isDeleted = false")
    Category findCategoriesByName(@Param("categoryName") String categoryName);
    @Query("SELECT c FROM Category c WHERE c.categoryId = :id AND c.isDeleted = false")
    Optional<Category> findCategoryById(@Param("id") Long id);
    List<Category> findAllByIsDeletedFalse();


}

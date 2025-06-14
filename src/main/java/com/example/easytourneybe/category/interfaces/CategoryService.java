package com.example.easytourneybe.category.interfaces;

import com.example.easytourneybe.category.Category;
import com.example.easytourneybe.category.CategoryDto;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    Category createCategory(String name);

    Category hasExistCategoryName(String categoryName);

    Optional<Category> findCategoryById(Long id);

    List<Category> searchAndSortCategories(String keyword, String sortType, int page, int size, String sortValue);

    Optional<Category> updateCategoryIsDelete(Long id);

    Optional<Category> updateCategory(Long id, String categoryName);

    long totalCategory(String keyword);

    Sort getSorting(String sortType, String sortValue);

    List<Category> findAllCategories();

    CategoryDto findCategoryDtoById(Integer categoryId);

    Integer countTournamentByCategory(Integer categoryId);
}

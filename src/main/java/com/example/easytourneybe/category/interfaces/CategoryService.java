package com.example.easytourneybe.category.interfaces;

import com.example.easytourneybe.category.Category;
import org.springframework.data.domain.Sort;
import java.util.List;
import java.util.Optional;

public interface CategoryService {
    Category createCategory(String name);

    boolean hasExistCategoryName(String categoryName);

    Optional<Category> findCategoryById(Long id);

    List<Category> searchAndSortCategories(String keyword, String sortType, int page, int size, String sortValue);

    Optional<Category> updateCategoryIsDelete(Long id);

    Optional<Category> updateCategory(Long id, String categoryName);

    long totalCategory(String keyword);

    void validatePageAndSize(int page, int size);

    Sort getSorting(String sortType, String sortValue);
}

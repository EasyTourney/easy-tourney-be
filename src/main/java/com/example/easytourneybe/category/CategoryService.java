package com.example.easytourneybe.category;

import com.example.easytourneybe.category.interfaces.CategoryRepository;
import com.example.easytourneybe.category.interfaces.ICategoryService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CategoryService implements ICategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category createCategory(String categoryName) {
        Category category = new Category();
        category.setCategoryName(categoryName);
        category.setCreatedAt(LocalDateTime.now());
        return categoryRepository.save(category);
    }

    @Override
    public boolean hasExistCategoryName(String categoryName) {
        return categoryRepository.findCategoryByCategoryNameIgnoreCaseAndIsDeletedFalse(categoryName) != null;
    }
}

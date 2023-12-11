package com.example.easytourneybe.category;

import com.example.easytourneybe.exceptions.InvalidRequestException;
import com.example.easytourneybe.category.interfaces.CategoryRepository;
import com.example.easytourneybe.validations.CommonValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class CategoryServiceImpl implements com.example.easytourneybe.category.interfaces.CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    private final CommonValidation commonValidation=new CommonValidation();

    @Override
    public Category createCategory(String categoryName) {
        if (hasExistCategoryName(categoryName.trim())) {
            throw new InvalidRequestException("Category name has already existed");
        }

        Category category = new Category();
        category.setCategoryName(categoryName.trim());
        category.setCreatedAt(LocalDateTime.now());

        return categoryRepository.save(category);
    }


    @Override
    public boolean hasExistCategoryName(String categoryName) {
        return categoryRepository.findCategoriesByName(categoryName) != null;
    }

    @Override
    public Sort getSorting(String sortType, String sortValue) {
        if (Objects.equals(sortType, "") || sortType == null)  {
            sortValue="categoryId";
            sortType="desc";
        }
        Sort sorting = Sort.by(sortValue);

        if (sortType != null) {
            sorting = sortType.equalsIgnoreCase("desc") ? sorting.descending() : sorting.ascending();
        }

        return sorting;
    }

    @Override
    public Optional<Category> findCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public List<Category> searchAndSortCategories(String keyword, String sortType, int page, int size, String sortValue) {
        commonValidation.validatePageAndSize(page, size);

        Sort sorting = getSorting(sortType, sortValue);
        Pageable pageable = PageRequest.of(page, size, sorting);
        List<Category> foundCategories = categoryRepository.findCategoriesByName(keyword.trim(), pageable);

        if (foundCategories.isEmpty()) {
            throw new NoSuchElementException("Category not found");
        }

        return foundCategories;
    }

    @Override
    public Optional<Category> updateCategoryIsDelete(Long id) {
        Optional<Category> categoryOptional = categoryRepository.findCategoryById(id);

        if (categoryOptional.isPresent()) {
            Category category = categoryOptional.get();
            category.setDeleted(true);
            category.setDeletedAt(LocalDateTime.now());
            categoryRepository.save(category);
            return Optional.of(category);
        } else {
            throw new NoSuchElementException("Category not found");
        }
    }

    @Override
    public Optional<Category> updateCategory(Long id, String categoryName) {
        Optional<Category> categoryOptional = categoryRepository.findCategoryById(id);

        if(hasExistCategoryName(categoryName)){
            throw new InvalidRequestException("Category name has already exist");
        }

        if (categoryOptional.isPresent()) {
            Category category = categoryOptional.get();
            category.setCategoryName(categoryName);
            category.setUpdatedAt(LocalDateTime.now());
            categoryRepository.save(category);
            return Optional.of(category);
        } else {
            throw new NoSuchElementException("Category not found");
        }
    }
    public long totalCategory(String keyword) {
        return categoryRepository.totalCategory(keyword);
    }

    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }
}

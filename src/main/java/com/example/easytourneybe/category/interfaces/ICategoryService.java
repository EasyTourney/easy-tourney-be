package com.example.easytourneybe.category.interfaces;

import com.example.easytourneybe.category.Category;

public interface ICategoryService {
    Category createCategory(String name);

    boolean hasExistCategoryName(String categoryName);
}

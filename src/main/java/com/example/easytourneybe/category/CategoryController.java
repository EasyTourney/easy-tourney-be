package com.example.easytourneybe.category;

import com.example.easytourneybe.category.interfaces.CategoryService;
import com.example.easytourneybe.model.ResponseObject;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import static com.example.easytourneybe.constants.DefaultListParams.PAGE;
import static com.example.easytourneybe.constants.DefaultListParams.SIZE;


@RestController
@CrossOrigin
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService CategoryService;
    private static final String DEFAULT_SORT_VALUE = "categoryName";
    public CategoryController(CategoryService CategoryService) {
        this.CategoryService = CategoryService;
    }
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> findCategoryById(@PathVariable Long id) {
        Optional<Category> foundCategory = CategoryService.findCategoryById(id);

        if (foundCategory.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject(true, 1, foundCategory)
            );
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ResponseObject(false, 0, "")
        );
    }

    @GetMapping("")
    public ResponseEntity<ResponseObject> searchCategoryByNameContaining(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "") String sortType,
            @RequestParam(defaultValue = PAGE) int page,
            @RequestParam(defaultValue = SIZE) int size,
            @RequestParam(defaultValue = DEFAULT_SORT_VALUE) String sortValue)
    {
        long totalCategories = CategoryService.totalCategory(keyword.trim());
        List<Category> foundCategories = CategoryService.searchAndSortCategories(keyword.trim(), sortType, page-1, size, sortValue);
        ResponseObject responseObject = new ResponseObject(
                    true,
                    foundCategories.size(),
                    foundCategories
            );
        responseObject.setAdditionalData(java.util.Collections.singletonMap("totalCategories", totalCategories));
        return ResponseEntity.status(HttpStatus.OK).body(responseObject);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> deleteCategory(@PathVariable Long id) {
        Optional<Category> updatedCategory = CategoryService.updateCategoryIsDelete(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject(true, 1, updatedCategory));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject> updateCategory(@PathVariable Long id, @Valid @RequestBody Category category) {
        Optional<Category> updatedCategory = CategoryService.updateCategory(id, category.getCategoryName().trim());
        return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject(true, 1, updatedCategory)
            );
    }

    @PostMapping

    public ResponseEntity<ResponseObject> createCategory(@Valid @RequestBody Category category) {
        Category temp = CategoryService.createCategory(category.getCategoryName().trim());
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseObject(true, 1, temp)
        );
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseObject> findAllCategories() {
        List<Category> foundCategories = CategoryService.findAllCategories();
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(true, foundCategories.size(), foundCategories)
        );
    }
}

package com.example.easytourneybe.category;

import com.example.easytourneybe.category.interfaces.ICategoryService;
import com.example.easytourneybe.model.ResponseObject;
import jakarta.validation.Valid;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/category")
public class CategoryController {
    private final ICategoryService ICategoryService;

    public CategoryController(ICategoryService ICategoryService){
        this.ICategoryService = ICategoryService;
    }

    @PostMapping
    public ResponseEntity<ResponseObject> createCategory(@Valid @RequestBody Category category, BindingResult result) {
        //Validate of Category Model
        if (result.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseObject(false, 0, "", result.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList().get(0))
            );
        }

        String categoryName = category.getCategoryName().trim();

        //Validate of Category is already exists
        if (ICategoryService.hasExistCategoryName(categoryName)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseObject(false, 0, "", "Category name already exists")
            );
        }

        Category temp = ICategoryService.createCategory(categoryName);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseObject(true, 1, temp, "Create category successfully")
        );
    }
}

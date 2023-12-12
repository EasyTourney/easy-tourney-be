package com.example.easytourneybe.category;

import com.example.easytourneybe.util.RegexpUtils;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categoryId")
    private Long categoryId;

    @Pattern(regexp = RegexpUtils.CATEGORY_REGEXP, message = "Category name must be alphanumeric")
    @NotBlank(message = "Category name must be between 2 and 30 characters")
    @Length(min = 2, max = 30, message = "Category name must be between 2 and 30 characters")
    @Column(name = "categoryName", nullable = false)
    private String categoryName;

    @Column(name = "isDeleted")
    private boolean isDeleted;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @Column(name = "deletedAt")
    private LocalDateTime deletedAt;

    public void setCategoryName(String categoryName) {
        this.categoryName = (categoryName != null) ? categoryName.trim() : null;
    }
}

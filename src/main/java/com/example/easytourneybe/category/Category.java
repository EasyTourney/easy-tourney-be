package com.example.easytourneybe.category;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Category name must be alphanumeric")
    @NotBlank(message = "Category name must be between 2 and 30 characters")
    @Size(min = 2, max = 30, message = "Category name must be between 2 and 30 characters")
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

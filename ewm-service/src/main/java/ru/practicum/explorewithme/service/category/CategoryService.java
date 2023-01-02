package ru.practicum.explorewithme.service.category;

import org.springframework.data.domain.PageRequest;
import ru.practicum.explorewithme.model.category.dto.CategoryDto;

import java.util.Collection;

public interface CategoryService {

    CategoryDto createCategory(CategoryDto categoryDto);

    CategoryDto updateCategory(CategoryDto categoryDto);

    Collection<CategoryDto> getAllCategories(PageRequest pageRequest);

    CategoryDto getCategoryById(Long catId);

    void deleteCategory(Long catId);
}

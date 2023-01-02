package ru.practicum.explorewithme.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.model.category.dto.CategoryDto;
import ru.practicum.explorewithme.service.category.CategoryService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/categories")
public class CategoryControllerAdmin {

    private final CategoryService categoryService;

    @PostMapping
    public CategoryDto createCategory(@RequestBody CategoryDto categoryDto) {
        log.info("Получен POST запрос к эндпоинту /admin/categories. Category name: {}",
                categoryDto.getName());
        return categoryService.createCategory(categoryDto);
    }

    @PatchMapping
    public CategoryDto updateCategory(@RequestBody CategoryDto categoryDto) {
        log.info("Получен PATCH запрос к эндпоинту /admin/categories. Category name: {}",
                categoryDto.getName());
        return categoryService.updateCategory(categoryDto);
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategoryById(@PathVariable Long catId) {
        log.info("Получен GET запрос к эндпоинту /admin/categories/{}", catId);
        return categoryService.getCategoryById(catId);
    }

    @DeleteMapping("/{catId}")
    public void deleteCategory(@PathVariable Long catId) {
        log.info("Получен DELETE запрос к эндпоинту /admin/categories/{}", catId);
        categoryService.deleteCategory(catId);
    }
}

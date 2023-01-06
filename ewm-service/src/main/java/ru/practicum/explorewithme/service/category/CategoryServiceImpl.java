package ru.practicum.explorewithme.service.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.exception.BadRequestException;
import ru.practicum.explorewithme.exception.ConflictException;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.exception.ValidationException;
import ru.practicum.explorewithme.model.category.Category;
import ru.practicum.explorewithme.model.category.dto.CategoryDto;
import ru.practicum.explorewithme.model.event.Event;
import ru.practicum.explorewithme.repository.CategoryRepository;
import ru.practicum.explorewithme.repository.EventRepository;
import ru.practicum.explorewithme.util.mapper.CategoryMapper;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto createCategory(@NotNull CategoryDto categoryDto) {
        log.info("Запрос на сохранение категории {}", categoryDto.getName());
        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new ConflictException("Категория с именем " + categoryDto.getName() + " уже существует.");
        }
        Category category = CategoryMapper.toCategory(categoryDto);
        validateCategory(category);
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        log.info("Запрос на обновление категории {}", categoryDto.getName());
        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new ConflictException("Категория с именем " + categoryDto.getName() + " уже существует.");
        }
        Category category = CategoryMapper.toCategory(categoryDto);
        validateCategory(category);
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(Long catId) {
        log.info("Запрос на удаление категории {}", catId);
        Category category = CategoryMapper.toCategory(getCategoryById(catId));
        List<Event> events = eventRepository.findAllByCategoryId(catId);
        if (!events.isEmpty()) {
            throw new ValidationException("Удаление категорий, которые связаны с событиями запрещено");
        }
        categoryRepository.deleteById(category.getId());
    }


    @Override
    public List<CategoryDto> getAllCategories(PageRequest pageRequest) {
        return categoryRepository.getAllCategoriesByPage(pageRequest).stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Не найдена категория " + catId));
        log.info("Запрос на получение категории {}", category.getName());
        return CategoryMapper.toCategoryDto(category);
    }

    public void validateCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Передан null вместо категории.");
        }
        if (category.getName() == null || category.getName().isBlank()) {
            throw new BadRequestException("Имя категории не должно быть пустым.");
        }
    }
}

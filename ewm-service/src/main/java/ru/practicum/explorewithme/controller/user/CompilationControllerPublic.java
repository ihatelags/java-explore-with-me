package ru.practicum.explorewithme.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.model.compilation.dto.CompilationDto;
import ru.practicum.explorewithme.service.compilation.CompilationService;
import ru.practicum.explorewithme.util.ValidationPageParam;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/compilations")
public class CompilationControllerPublic {

    private final CompilationService compilationService;

    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(@PathVariable Long compId) {
        log.info("Получен GET запрос к эндпоинту /compilations/{}", compId);
        return compilationService.getCompilationById(compId);
    }

    @GetMapping
    public Collection<CompilationDto> getAllCompilations(@RequestParam(defaultValue = "false") Boolean pinned,
                                                         @RequestParam(defaultValue = "0") Integer from,
                                                         @RequestParam(defaultValue = "10") Integer size) {
        validatePage(from, size);
        log.info("Получен GET запрос к эндпоинту /compilations?pinned={}from={}size={}", pinned, from, size);
        final PageRequest pageRequest = findPageRequest(from, size);
        return compilationService.getAllCompilations(pinned, pageRequest);
    }

    public PageRequest findPageRequest(Integer from, Integer size) {
        int page = from / size;
        return PageRequest.of(page, size);
    }

    private void validatePage(Integer from, Integer size) {
        ValidationPageParam validationPageParam = new ValidationPageParam(from, size);
        validationPageParam.validatePageParam();
    }
}

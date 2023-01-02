package ru.practicum.explorewithme.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.model.compilation.dto.CompilationDto;
import ru.practicum.explorewithme.model.compilation.dto.NewCompilationDto;
import ru.practicum.explorewithme.service.compilation.CompilationService;
import ru.practicum.explorewithme.util.mapper.CompilationMapper;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/compilations")
public class CompilationControllerAdmin {

    private final CompilationService compilationService;

    @PostMapping
    public CompilationDto createCompilation(@RequestBody NewCompilationDto newCompilationDto) {
        log.info("Получен POST запрос к эндпоинту /admin/compilations. Compilation title: {}",
                newCompilationDto.getTitle());
        return CompilationMapper.toCompilationDto(compilationService.createCompilation(newCompilationDto));
    }

    @DeleteMapping("/{compId}")
    public void deleteCompilation(@PathVariable Long compId) {
        log.info("Получен DELETE запрос к эндпоинту /admin/compilations/{}", compId);
        compilationService.deleteCompilation(compId);
    }

    @PatchMapping("/{compId}/events/{eventId}")
    public void addEventToCompilation(@PathVariable Long compId,
                                      @PathVariable Long eventId) {
        log.info("Получен PATCH запрос к эндпоинту /admin/compilations/{}/events/{}", compId, eventId);
        compilationService.addEventToCompilation(compId, eventId);
    }

    @DeleteMapping("/{compId}/events/{eventId}")
    public void deleteEventFromCompilation(@PathVariable Long compId,
                                           @PathVariable Long eventId) {
        log.info("Получен DELETE запрос к эндпоинту /admin/compilations/{}/events/{}", compId, eventId);
        compilationService.deleteEventFromCompilation(compId, eventId);
    }

    @PatchMapping("/{compId}/pin")
    public void pinCompilation(@PathVariable Long compId) {
        log.info("Получен PATCH запрос к эндпоинту /admin/compilations//{}/pin", compId);
        compilationService.pinCompilation(compId);
    }

    @DeleteMapping("/{compId}/pin")
    public void deletePinCompilation(@PathVariable Long compId) {
        log.info("Получен DELETE запрос к эндпоинту /admin/compilations//{}/pin", compId);
        compilationService.delPinCompilation(compId);
    }
}

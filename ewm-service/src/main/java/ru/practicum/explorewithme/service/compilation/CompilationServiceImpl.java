package ru.practicum.explorewithme.service.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.exception.BadRequestException;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.model.compilation.Compilation;
import ru.practicum.explorewithme.model.compilation.dto.NewCompilationDto;
import ru.practicum.explorewithme.model.event.Event;
import ru.practicum.explorewithme.repository.CompilationRepository;
import ru.practicum.explorewithme.repository.EventRepository;
import ru.practicum.explorewithme.util.mapper.CompilationMapper;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public Compilation createCompilation(@NotNull NewCompilationDto newCompilationDto) {
        List<Event> events = newCompilationDto.getEvents()
                .stream()
                .map(id -> eventRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Compilation not found")))
                .collect(Collectors.toList());
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto);
        log.info("Запрос на создание подборки {}", compilation.getTitle());
        compilation.setEvents(events);
        validateCompilation(compilation);
        return compilationRepository.save(compilation);
    }

    @Override
    public void deleteCompilation(Long compId) {
        compilationRepository.deleteById(compId);
        log.info("Запрос на удаление подборки {}", compId);
    }

    @Override
    public void addEventToCompilation(Long compId, Long eventId) {
        Compilation compilation = getCompilationById(compId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Не найдено событие с ид " + eventId));
        Collection<Event> events = compilation.getEvents();
        events.add(event);
        compilation.setEvents(events);
        compilationRepository.save(compilation);
        log.info("Запрос на добавление события {} в подборку {}", eventId, compilation.getId());
    }

    @Override
    public void deleteEventFromCompilation(Long compId, Long eventId) {
        Compilation compilation = getCompilationById(compId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Не найдено событие с ид " +  eventId));
        Collection<Event> events = compilation.getEvents();
        events.remove(event);
        compilation.setEvents(events);
        compilationRepository.save(compilation);
        log.info("Запрос на удаление события {} в подборке {}", eventId, compilation.getId());
    }

    @Override
    public Collection<Compilation> getAllCompilations(Boolean pinned, PageRequest pageRequest) {
        log.info("Запрос на получение всех подборок");
        return compilationRepository.getAllCompilationsByPage(pinned, pageRequest).stream()
                .collect(Collectors.toList());
    }

    @Override
    public Compilation getCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Не найдена подборка с ид " + compId));
        log.info("Запрос на получение подборки {}", compId);
        return compilation;
    }

    @Override
    public void pinCompilation(Long compId) {
        Compilation compilation = getCompilationById(compId);
        log.info("Запрос на прикрепление подборки {} на главную страницу", compId);
        compilation.setPinned(true);
        compilationRepository.save(compilation);

    }

    @Override
    public void delPinCompilation(Long compId) {
        Compilation compilation = getCompilationById(compId);
        log.info("Запрос на открепление подборки {} с главной страницы", compId);
        compilation.setPinned(false);
        compilationRepository.save(compilation);
    }

    public void validateCompilation(Compilation compilation) {
        if (compilation == null) {
            throw new IllegalArgumentException("Передан null вместо подборки.");
        }
        if (compilation.getTitle() == null || compilation.getTitle().isBlank()) {
            throw new BadRequestException("Название подборки не может быть пустым.");
        }
    }
}

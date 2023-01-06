package ru.practicum.explorewithme.service.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.model.compilation.Compilation;
import ru.practicum.explorewithme.model.compilation.dto.CompilationDto;
import ru.practicum.explorewithme.model.compilation.dto.NewCompilationDto;
import ru.practicum.explorewithme.model.event.Event;
import ru.practicum.explorewithme.repository.CompilationRepository;
import ru.practicum.explorewithme.repository.EventRepository;
import ru.practicum.explorewithme.util.mapper.CompilationMapper;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto createCompilation(@NotNull NewCompilationDto newCompilationDto) {
        Collection<Long> eventIds = newCompilationDto.getEvents();
        Collection<Event> eventsFromDB = eventRepository.getEventsForCompilation(eventIds);
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto);
        compilation.setEvents(eventsFromDB);
        log.info("Запрос на создание подборки {}", compilation.getTitle());
        return CompilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    public void deleteCompilation(Long compId) {
        compilationRepository.deleteById(compId);
        log.info("Запрос на удаление подборки {}", compId);
    }

    @Override
    public void addEventToCompilation(Long compId, Long eventId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Не найдена подборка с ид " + compId));
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
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Не найдена подборка с ид " + compId));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Не найдено событие с ид " +  eventId));
        Collection<Event> events = compilation.getEvents();
        events.remove(event);
        compilation.setEvents(events);
        compilationRepository.save(compilation);
        log.info("Запрос на удаление события {} в подборке {}", eventId, compilation.getId());
    }

    @Override
    public Collection<CompilationDto> getAllCompilations(Boolean pinned, PageRequest pageRequest) {
        log.info("Запрос на получение всех подборок");
        return compilationRepository.getAllCompilationsByPage(pinned, pageRequest).stream()
                .map(CompilationMapper::toCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Не найдена подборка с ид " + compId));
        log.info("Запрос на получение подборки {}", compId);
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    public void pinCompilation(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Не найдена подборка с ид " + compId));
        log.info("Запрос на прикрепление подборки {} на главную страницу", compId);
        compilation.setPinned(true);
        compilationRepository.save(compilation);

    }

    @Override
    public void delPinCompilation(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Не найдена подборка с ид " + compId));
        log.info("Запрос на открепление подборки {} с главной страницы", compId);
        compilation.setPinned(false);
        compilationRepository.save(compilation);
    }

}

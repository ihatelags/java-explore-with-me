package ru.practicum.explorewithme.service.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.exception.BadRequestException;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.model.event.Event;
import ru.practicum.explorewithme.model.request.Request;
import ru.practicum.explorewithme.model.request.RequestStatus;
import ru.practicum.explorewithme.model.user.User;
import ru.practicum.explorewithme.repository.EventRepository;
import ru.practicum.explorewithme.repository.RequestRepository;
import ru.practicum.explorewithme.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@Service
public class RequestServiceImpl implements RequestService {

    private static final String NOT_FOUND_REQUEST = "In DB has not found request id ";
    private static final String NOT_FOUND_USER = "In DB has not found user id ";
    private static final String NOT_FOUND_EVENT = "In DB has not found event id ";
    private static final String BAD_REQUEST = "Event id could not be empty.";

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public Request createRequest(Long userId, Long eventId) {
        if (eventId == null) {
            throw new BadRequestException(BAD_REQUEST);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_USER + userId));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_EVENT + eventId));
        Request request = new Request();
        request.setRequester(user);
        request.setEvent(event);
        request.setStatus(RequestStatus.PENDING);
        request.setCreated(LocalDateTime.now());
        return requestRepository.save(request);
    }

    @Override
    public Request cancelRequest(Long userId, Long reqId) {
        Request request = requestRepository.findById(reqId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_REQUEST + reqId));
        request.setStatus(RequestStatus.CANCELED);
        return requestRepository.save(request);
    }

    @Override
    public Collection<Request> getAllRequestsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_USER + userId));
        return requestRepository.getAllRequestsByUser(user.getId());
    }

    @Override
    public Collection<Request> getAllRequestsByEvent(Long userId, Long eventId) {
        return requestRepository.getAllRequestsOnEventByUser(userId, eventId);
    }

    @Override
    public Request confirmRequest(Long userId, Long eventId, Long reqId) {
        Request request = requestRepository.findById(reqId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_REQUEST + reqId));
        request.setStatus(RequestStatus.CONFIRMED);
        log.info("Has changed status on " + request.getStatus() + " for request id " + reqId);
        return requestRepository.save(request);
    }

    @Override
    public Request rejectRequest(Long userId, Long eventId, Long reqId) {
        Request request = requestRepository.findById(reqId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_REQUEST + reqId));
        request.setStatus(RequestStatus.REJECTED);
        log.info("Has changed status on " + request.getStatus() + " for request id " + reqId);
        return requestRepository.save(request);
    }
}

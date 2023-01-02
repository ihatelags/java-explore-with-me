package ru.practicum.explorewithme.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.explorewithme.model.user.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u " +
            "from User u " +
            "where u.id in ?1 " +
            "group by u.id " +
            "order by u.id desc")
    List<User> getUsersFromIds(List<Long> users);

    @Query("select u " +
            "from User u " +
            "group by u.id " +
            "order by count(distinct u.id) desc")
    Page<User> getAllUsersByPage(PageRequest pageRequest);

    boolean existsByEmail(String email);
}

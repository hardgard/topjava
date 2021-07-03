package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.Util;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class DataJpaMealRepository implements MealRepository {
    private static final Sort SORT_DATE_TIME = Sort.by(Sort.Direction.DESC, "dateTime");

    private final CrudMealRepository crudRepository;

    @PersistenceContext
    private EntityManager em;

    public DataJpaMealRepository(CrudMealRepository crudRepository) {
        this.crudRepository = crudRepository;
    }

    @Override
    public Meal save(Meal meal, int userId) {
        if (!meal.isNew()) {
            if (get(meal.id(), userId) == null) return null;
        }
        meal.setUser(em.getReference(User.class, userId));
        return crudRepository.save(meal);
    }

    @Override
    public boolean delete(int id, int userId) {
        return crudRepository.delete(id, userId) != 0;
    }

    @Override
    public Meal get(int id, int userId) {
        Meal meal = crudRepository.findById(id).orElse(null);
        return meal != null && meal.getUser().getId() == userId ? meal : null;
    }

    @Override
    public List<Meal> getAll(int userId) {
        return crudRepository.findAll(SORT_DATE_TIME).stream()
                .filter(meal -> meal.getUser().getId() == userId).collect(Collectors.toList());
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        return getAll(userId).stream().filter(meal -> Util.isBetweenHalfOpen(meal.getDateTime(), startDateTime, endDateTime)).collect(Collectors.toList());
    }
}

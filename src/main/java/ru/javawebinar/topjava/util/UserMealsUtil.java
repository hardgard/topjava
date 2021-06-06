package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.chrono.ChronoLocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
       // mealsTo.forEach(System.out::println);

       // System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
        System.out.println(filteredByStreamsO2(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

        Map<LocalDateTime,Integer> daysMap = new HashMap<>();
        for (UserMeal meal:
                meals) {
            daysMap.merge(meal.getDateTime().truncatedTo(ChronoUnit.DAYS), meal.getCalories(), Integer::sum);
        }
        List<UserMealWithExcess> res = new ArrayList<>();
        for (UserMeal userMeal :
             meals) {
            if (TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime,endTime)) {
                if (daysMap.get(userMeal.getDateTime().truncatedTo(ChronoUnit.DAYS))> caloriesPerDay){
                    res.add(new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), true));
                }else {
                    res.add(new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(),false));
                }
            }
        }
        return res;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDateTime,Integer> daysMap = meals.stream().collect(Collectors.toMap(i->i.getDateTime().truncatedTo(ChronoUnit.DAYS), UserMeal::getCalories, Integer::sum));
        return meals.stream().filter(i -> TimeUtil.isBetweenHalfOpen(i.getDateTime().toLocalTime(),startTime ,endTime))
                .map(i -> new UserMealWithExcess(i.getDateTime(), i.getDescription(), i.getCalories(),daysMap.get(i.getDateTime().truncatedTo(ChronoUnit.DAYS)) > caloriesPerDay)).collect(Collectors.toList());

    }


    public static List<UserMealWithExcess> filteredByStreamsO2(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
       return meals.stream().collect(Collectors.collectingAndThen(Collectors.toMap(i->i.getDateTime().truncatedTo(ChronoUnit.DAYS), UserMeal::getCalories, Integer::sum),
                map -> meals.stream().filter(i -> TimeUtil.isBetweenHalfOpen(i.getDateTime().toLocalTime(),startTime ,endTime))
                        .map(i -> new UserMealWithExcess(i.getDateTime(), i.getDescription(), i.getCalories(),map.get(i.getDateTime().truncatedTo(ChronoUnit.DAYS)) > caloriesPerDay)).collect(Collectors.toList())));
    }

}

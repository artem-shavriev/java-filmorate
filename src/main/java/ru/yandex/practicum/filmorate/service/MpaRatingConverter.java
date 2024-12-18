package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MpaRating;

@Component
public class MpaRatingConverter {
    public static int convertFromMpa(MpaRating mpa) {
        switch (mpa) {
            case MpaRating.G:
                return 1;
            case MpaRating.PG:
                return 2;
            case MpaRating.PG13:
                return 3;
            case MpaRating.R:
                return 4;
            case MpaRating.NC:
                return 5;
        }
        return 5;
    }

    public static MpaRating convertToMpa(int mpa) {
        switch (mpa) {
            case 1:
                return MpaRating.G;
            case 2:
                return MpaRating.PG;
            case 3:
                return MpaRating.PG13;
            case 4:
                return MpaRating.R;
            case 5:
                return MpaRating.NC;
        }
        return MpaRating.NC;
    }
}

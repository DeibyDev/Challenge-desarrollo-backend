package co.com.sofka.cargame.usecase.services;

import co.com.sofka.cargame.usecase.model.Score;

import java.util.List;

public interface RankingService {
    List<Score> getScoreGame();
}

package co.com.sofka.cargame.infra.services;

import co.com.sofka.cargame.domain.carril.values.CarrilId;
import co.com.sofka.cargame.domain.carro.values.CarroId;
import co.com.sofka.cargame.domain.juego.values.JuegoId;
import co.com.sofka.cargame.usecase.model.TiempoDesplazamiento;
import co.com.sofka.cargame.usecase.model.Score;
import co.com.sofka.cargame.usecase.services.CarroDesplazadoService;
import co.com.sofka.cargame.usecase.services.CarroService;
import co.com.sofka.cargame.usecase.services.InformacionDeJuegoService;
import co.com.sofka.cargame.usecase.services.RankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class RankingQueryService implements RankingService {

    private final MongoTemplate mongoTemplate;
    private final InformacionDeJuegoService idQueryService;
    private final CarroService carroQueryService;
    private final CarroDesplazadoService carroDesplazadoService;

    @Autowired
    public RankingQueryService(MongoTemplate mongoTemplate, InformacionDeJuegoQueryService idQueryService, CarroQueryService carroQueryService, CarroDesplazadoService carroDesplazadoService) {
        this.mongoTemplate = mongoTemplate;
        this.idQueryService = idQueryService;
        this.carroQueryService = carroQueryService;
        this.carroDesplazadoService = carroDesplazadoService;
    }

    @Override
    public List<Score> getScoreGame() {
        List<Score> scores = new ArrayList<>();
        idQueryService
                .obtenerInformacionDeJuego()
                .forEach(id -> {
                    var carroId = CarroId.of(id.getCarroId());
                    var juegoId = JuegoId.of(id.getJuegoId());
                    var carrilId = CarrilId.of(id.getCarrilId());
                    var fechaInicio = carroDesplazadoService
                            .obtenerTiempoDeDesplazamiento(carrilId)
                            .stream()
                            .findFirst()
                            .get()
                            .getWhen();
                    var fechaFin = carroDesplazadoService
                            .obtenerTiempoDeDesplazamiento(carrilId)
                            .stream()
                            .sorted(Comparator.comparing(TiempoDesplazamiento::getWhen).reversed())
                            .findFirst()
                            .get()
                            .getWhen();

                    var nombre = carroQueryService.getNombreConductorPorId(carroId);
                    var score = new Score(id.getJuegoId(),id.getCarrilId(),id.getCarroId(),nombre,fechaInicio,fechaFin, id.getMetros());
                    scores.add(score);
                });

        return scores;
    }

}

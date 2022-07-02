package co.com.sofka.cargame.usecase.services;

import co.com.sofka.cargame.domain.carril.values.CarrilId;
import co.com.sofka.cargame.usecase.model.TiempoDesplazamiento;

import java.util.List;

public interface CarroDesplazadoService {
    List<TiempoDesplazamiento> obtenerTiempoDeDesplazamiento(CarrilId carrilId);
}

package co.com.sofka.cargame.infra.services;

import co.com.sofka.cargame.usecase.model.InformacionDeJuego;
import co.com.sofka.cargame.usecase.services.InformacionDeJuegoService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InformacionDeJuegoQueryService implements InformacionDeJuegoService {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public InformacionDeJuegoQueryService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<InformacionDeJuego> obtenerInformacionDeJuego() {
        var lookup = LookupOperation.newLookup()
                .from("carril.CarroAgregadoACarrail")
                .localField("aggregateRootId")
                .foreignField("aggregateRootId")
                .as("carroAgregadoACarrail");

        var aggregation =
                Aggregation.newAggregation(lookup);

        var tempo = mongoTemplate.aggregate(aggregation, "carril.CarrilCreado", String.class)
                .getMappedResults().stream().collect(Collectors.toList());

        return mongoTemplate.aggregate(aggregation, "carril.CarrilCreado", String.class)
                .getMappedResults().stream()
                .map(body -> new Gson().fromJson(body,IdRecord.class))
                .map(idRecord -> {
                    var informacionDeJuego = new InformacionDeJuego();
                    informacionDeJuego.setCarrilId(idRecord.getAggregateRootId());
                    informacionDeJuego.setCarroId(idRecord.getCarroAgregadoACarrail().get(0).getCarroId().getUuid());
                    informacionDeJuego.setJuegoId(idRecord.getJuegoId().getUuid());
                    informacionDeJuego.setMetros(idRecord.getMetros());
                    return informacionDeJuego;
                })
                .collect(Collectors.toList());
    }

    public static class IdRecord {
        private String aggregateRootId;
        private String metros;
        private JuegoId juegoId;
        private List<CarrilCarroQueryService.CarroSobreCarrilRecord.CarroAgregadoACarrail> carroAgregadoACarrail;

        public String getMetros() {
            return metros;
        }

        public void setMetros(String metros) {
            this.metros = metros;
        }

        public String getAggregateRootId() {
            return aggregateRootId;
        }

        public void setAggregateRootId(String aggregateRootId) {
            this.aggregateRootId = aggregateRootId;
        }

        public List<CarrilCarroQueryService.CarroSobreCarrilRecord.CarroAgregadoACarrail> getCarroAgregadoACarrail() {
            return carroAgregadoACarrail;
        }

        public void setCarroAgregadoACarrail(List<CarrilCarroQueryService.CarroSobreCarrilRecord.CarroAgregadoACarrail> carroAgregadoACarrail) {
            this.carroAgregadoACarrail = carroAgregadoACarrail;
        }

        public JuegoId getJuegoId() {
            return juegoId;
        }

        public void setJuegoId(JuegoId juegoId) {
            this.juegoId = juegoId;
        }

        public static class CarroAgregadoACarrail {
            private CarroId carroId;

            public CarroId getCarroId() {
                return carroId;
            }

            public void setCarroId(CarroId carroId) {
                this.carroId = carroId;
            }

            public static class CarroId {
                private String uuid;

                public String getUuid() {
                    return uuid;
                }

                public void setUuid(String uuid) {
                    this.uuid = uuid;
                }
            }
        }

        public static class JuegoId{
            private String uuid;

            public String getUuid() {
                return uuid;
            }

            public void setUuid(String uuid) {
                this.uuid = uuid;
            }

        }

    }
}

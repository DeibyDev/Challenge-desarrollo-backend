package co.com.sofka.cargame.infra.services;

import co.com.sofka.cargame.domain.carril.values.CarrilId;
import co.com.sofka.cargame.usecase.model.TiempoDesplazamiento;
import co.com.sofka.cargame.usecase.services.CarroDesplazadoService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
public class CarroDesplazadoQueryService implements CarroDesplazadoService {

    private final MongoTemplate mongoTemplate;
    @Autowired
    public CarroDesplazadoQueryService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<TiempoDesplazamiento> obtenerTiempoDeDesplazamiento(CarrilId carrilId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        var aggregation = Aggregation.newAggregation(
                Aggregation.match(where("aggregateRootId").is(carrilId.value()))
        );

        var tempo = mongoTemplate.aggregate(aggregation, "carril.CarroDesplazado", String.class)
                .getMappedResults().stream().collect(Collectors.toList());

        return mongoTemplate.aggregate(aggregation, "carril.CarroDesplazado", String.class)
                .getMappedResults()
                .stream()
                .map(body -> new Gson().fromJson(body, CarroDesplazadoRecord.class))
                .map(carroDesplazadoRecord -> {
                    var record = new TiempoDesplazamiento();
                    try {
                        Date date = sdf.parse(carroDesplazadoRecord.getWhen().get$date());
                        record.setWhen(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return record;
                }).collect(Collectors.toList());
    }

    public static class CarroDesplazadoRecord{

        private When when;

        public When getWhen() {
            return when;
        }

        public void setWhen(When when) {
            this.when = when;
        }

        public static class When{
            private String $date;

            public String get$date() {
                return $date;
            }

            public void set$date(String $date) {
                this.$date = $date;
            }
        }

    }

}

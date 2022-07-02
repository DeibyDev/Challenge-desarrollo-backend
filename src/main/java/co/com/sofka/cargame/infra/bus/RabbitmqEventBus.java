package co.com.sofka.cargame.infra.bus;

import co.com.sofka.business.generic.BusinessException;
import co.com.sofka.domain.generic.DomainEvent;
import co.com.sofka.infraestructure.bus.EventBus;
import co.com.sofka.infraestructure.bus.notification.ErrorNotification;
import co.com.sofka.infraestructure.bus.notification.SuccessNotification;
import co.com.sofka.infraestructure.bus.serialize.ErrorNotificationSerializer;
import co.com.sofka.infraestructure.bus.serialize.SuccessNotificationSerializer;
import co.com.sofka.infraestructure.event.ErrorEvent;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class RabbitmqEventBus implements EventBus {

    private static final String EXCHANGE = "gameextraction";
    private static final String ORIGIN = "cargame";
    private static final String TOPIC_ERROR = "cargame.error";
    private static final String TOPIC_BUSINESS_ERROR = "cargame.business.error";
    private static final Logger logger = Logger.getLogger(RabbitmqEventBus.class.getName());
    private final RabbitTemplate rabbitTemplate;
    private final RabbitAdmin rabbitAdmin;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public RabbitmqEventBus(@Value("${spring.bus.uri}") String uri, MongoTemplate mongoTemplate) throws IOException, InterruptedException{
        this.mongoTemplate = mongoTemplate;
        this.rabbitTemplate = new RabbitTemplate(new CachingConnectionFactory(URI.create(uri)));
        this.rabbitAdmin = new RabbitAdmin(this.rabbitTemplate);
    }

    @Override
    public void publish(DomainEvent domainEvent) {
        var notification = SuccessNotification.wrapEvent(ORIGIN, domainEvent);
        var notificationSerialization = SuccessNotificationSerializer.instance().serialize(notification);
        rabbitAdmin.declareExchange(new TopicExchange(EXCHANGE));
        rabbitTemplate.convertAndSend(EXCHANGE, domainEvent.type, notificationSerialization.getBytes());
        mongoTemplate.save(domainEvent, domainEvent.type);
    }

    @Override
    public void publishError(ErrorEvent errorEvent) {
        if (errorEvent.error instanceof BusinessException) {
            publishToTopic(TOPIC_BUSINESS_ERROR, errorEvent);
        } else {
            publishToTopic(TOPIC_ERROR, errorEvent);
        }
        logger.log(Level.SEVERE, errorEvent.error.getMessage());
    }

    public void publishToTopic(String topic, ErrorEvent errorEvent) {
        var notification = ErrorNotification.wrapEvent(ORIGIN, errorEvent);
        var notificationSerialization = ErrorNotificationSerializer.instance().serialize(notification);
        rabbitAdmin.declareExchange(new TopicExchange(EXCHANGE));
        rabbitTemplate.convertAndSend(EXCHANGE, errorEvent.identify, notificationSerialization.getBytes());
        logger.warning("###### Error Event published to " + topic);
    }
}

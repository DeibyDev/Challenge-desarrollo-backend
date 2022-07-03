package co.com.sofka.cargame.infra.bus;

import co.com.sofka.cargame.SocketController;
import co.com.sofka.infraestructure.bus.serialize.SuccessNotificationSerializer;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import java.io.IOException;
import java.util.Optional;


@Component
public class RabbitMQConsumer {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQConsumer.class);
    @Autowired
    private EventListenerSubscriber eventListenerSubscriber;
    @Autowired
    private SocketController socketController;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "juego.handles", durable = "true"),
            exchange = @Exchange(value = "gameextraction", type = "topic"),
            key = "juego.#"
    ))
    public void recievedMessageJuego(Message<String> message) {
        messageShared(message);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "carro.handles", durable = "true"),
            exchange = @Exchange(value = "gameextraction", type = "topic"),
            key = "carro.#"
    ))
    public void recievedMessageCarro(Message<String> message) {
        messageShared(message);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "carril.handles", durable = "true"),
            exchange = @Exchange(value = "gameextraction", type = "topic"),
            key = "carril.#"
    ))
    public void recievedMessageCarril(Message<String> message) throws IOException, InterruptedException{
        messageShared(message);
    }

    private void messageShared(Message<String> message) {
        var successNotification = SuccessNotificationSerializer.instance().deserialize(message.getPayload());
        var event = successNotification.deserializeEvent();
        log.info("Event: {}", event.getAggregateName());
        try {
            this.eventListenerSubscriber.onNext(event);
            Optional.ofNullable(event.aggregateParentId())
                    .ifPresentOrElse(id -> socketController.send(id, event), () -> socketController.send(event.aggregateRootId(), event));
        } catch (Exception e) {
            this.eventListenerSubscriber.onError(e);
        }
    }
}
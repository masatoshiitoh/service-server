package jp.dressingroom.ServiceServer.EventAccumulator;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.kafka.client.producer.KafkaProducer;

public class EventAccumulatorVerticle extends AbstractVerticle {
    @Override
    public void start() throws Exception {
      EventBus eb = vertx.eventBus();
      eb.consumer("eb:event", message -> {
        System.out.println("I have received a message: " + message.body());
        // TODO: write code to send messages to kafka
        message.reply("OK");
      });
      System.out.println("EventAccumulatorVerticle ready!");
    }
}

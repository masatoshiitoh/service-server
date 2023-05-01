package jp.dressingroom.ServiceServer.EventEvaluator;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;

public class EventEvaluatorVerticle extends AbstractVerticle {
    @Override
    public void start(Promise<Void> startPromise) throws Exception {
      EventBus eb = vertx.eventBus();
      eb.consumer("eb:incoming", message -> {
        // TODO: call external logic
        // ここで外部のロジックに送りつける
        // 得られた結果をaccumulatorに送りつける
        System.out.println("I have received a message: " + message.body());
        eb.request("eb:event", "request to accumulate", reply -> {
          if (reply.succeeded()) {
            message.reply("accumulated. OK from evaluator");
            System.out.println("Received reply: " + reply.result().body());
          } else {
            message.reply("accumulated. NG from evaluator");
            System.out.println("No reply");
          }
        });
      });
      System.out.println("Receiver ready!");
    }
}

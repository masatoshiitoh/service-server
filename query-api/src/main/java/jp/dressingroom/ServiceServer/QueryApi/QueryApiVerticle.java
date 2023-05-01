package jp.dressingroom.ServiceServer.QueryApi;

import io.vertx.core.AbstractVerticle;

public class QueryApiVerticle extends AbstractVerticle {

      @Override
      public void start() throws Exception {
        vertx.eventBus().consumer("eb:query", message -> {
          message.reply("Query OK");
        });

          System.out.println("QueryApiVerticle ready!");
      }
}

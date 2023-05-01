package jp.dressingroom.ServiceServer.EventAccumulator;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    // start EventAccumulatorVerticle
    vertx.deployVerticle(EventAccumulatorVerticle.class.getName());

    vertx.createHttpServer().requestHandler(req -> {
      req.response()
        .putHeader("content-type", "text/plain")
        .end("Hello from Vert.x!");
    }).listen(8886, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        System.out.println("HTTP server started on port 8886");
      } else {
        startPromise.fail(http.cause());
      }
    });
  }
}

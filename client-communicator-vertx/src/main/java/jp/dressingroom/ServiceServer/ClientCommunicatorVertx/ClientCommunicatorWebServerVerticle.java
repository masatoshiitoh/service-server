package jp.dressingroom.ServiceServer.ClientCommunicatorVertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;

public class ClientCommunicatorWebServerVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.get("/").handler(ctx -> {
            HttpServerResponse response = ctx.response();
            response.putHeader("content-type", "text/plain");
            response.end("top from Vert.x!");
        });
        router.post("/command").handler(ctx2 -> {
            HttpServerRequest request = ctx2.request();
            request.bodyHandler(body -> {
                System.out.println("Received data: " + body.toString());
                vertx.eventBus().request("eb:incoming", body.toString(), reply -> {
                    if (reply.succeeded()) {
                        System.out.println("Received reply: " + reply.result().body());
                        ctx2.response().putHeader("content-type", "text/plain");
                        ctx2.response().end("command from Vert.x!");
                    } else {
                        System.out.println("eb:incoming No reply");
                        ctx2.response().putHeader("content-type", "text/plain");
                        ctx2.response().end("eb:incoming No reply");
                    }
                });
            });
        });
        router.post("/query").handler(ctx3 -> {

            HttpServerRequest request = ctx3.request();
            request.bodyHandler(body -> {
                System.out.println("[Query]Received data: " + body.toString());
                vertx.eventBus().request("eb:query", body.toString(), reply -> {
                    if (reply.succeeded()) {
                        System.out.println("[Query]Received reply: " + reply.result().body());
                        ctx3.response().putHeader("content-type", "text/plain");
                        ctx3.response().end("[Query]command from Vert.x!");
                    } else {
                        System.out.println("[Query]eb:query No reply");
                        ctx3.response().putHeader("content-type", "text/plain");
                        ctx3.response().end("[Query]eb:incoming No reply");
                    }
                });
            });
        });
        router.get("/event").handler(ctx4 -> {
            // ロングポーリングをイメージした遅延応答
            vertx.setTimer(15000, tid -> {
                HttpServerResponse response = ctx4.response();
                response.putHeader("content-type", "text/plain");
                response.end("after 15sec, event from Vert.x!");
            });
        });
        server.requestHandler(router).listen(8888, http -> {
            if (http.succeeded()) {
                System.out.println("HTTP server started on port 8888");
            } else {
                System.out.println("HTTP server failed to start on port 8888");
            }
        });

    }
}

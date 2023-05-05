package jp.dressingroom.ServiceServer.ClientCommunicatorVertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

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
        router.get("/top/:userID/").handler(getRoutingContextHandlerTop());
        router.post("/command/:userID/").handler(getRoutingContextHandlerCommand());
        router.post("/query/:userID/").handler(getRoutingContextHandlerQuery());
        router.get("/event/:userID/").handler(getRoutingContextHandlerEvent());
        server.requestHandler(router).listen(8888, http -> {
            if (http.succeeded()) {
                System.out.println("HTTP server started on port 8888");
            } else {
                System.out.println("HTTP server failed to start on port 8888");
            }
        });

    }

    private static Handler<RoutingContext> getRoutingContextHandlerTop() {
        return ctx -> {
            String userID = ctx.request().getParam("userID");

            HttpServerResponse response = ctx.response();
            response.putHeader("content-type", "text/plain");
            response.end("Hello, " + userID + "! Top page from Vert.x!");
        };
    }

    private Handler<RoutingContext> getRoutingContextHandlerEvent() {
        return ctx -> {
            String userID = ctx.request().getParam("userID");
            // ロングポーリングをイメージした遅延応答
            vertx.setTimer(21000, tid -> {
                HttpServerResponse response = ctx.response();
                response.putHeader("content-type", "text/plain");
                response.end("Hello, " + userID + " after 21 sec, event from Vert.x!");
            });
        };
    }

    private Handler<RoutingContext> getRoutingContextHandlerQuery() {
        return ctx -> {
            String userID = ctx.request().getParam("userID");

            HttpServerRequest request = ctx.request();
            request.bodyHandler(body -> {
                System.out.println("[Query]Received data: " + body.toString());
                vertx.eventBus().request("eb:query", body.toString(), reply -> {
                    if (reply.succeeded()) {
                        System.out.println("[Query]Received reply: " + reply.result().body());
                        ctx.response().putHeader("content-type", "text/plain");
                        ctx.response().end("[Query]command from Vert.x!");
                    } else {
                        System.out.println("[Query]eb:query No reply");
                        ctx.response().putHeader("content-type", "text/plain");
                        ctx.response().end("[Query]eb:incoming No reply");
                    }
                });
            });
        };
    }

    private Handler<RoutingContext> getRoutingContextHandlerCommand() {
        return ctx -> {
            String userID = ctx.request().getParam("userID");

            HttpServerRequest request = ctx.request();
            request.bodyHandler(body -> {
                System.out.println("Received data: " + body.toString());
                vertx.eventBus().request("eb:incoming", body.toString(), reply -> {
                    if (reply.succeeded()) {
                        System.out.println("Received reply: " + reply.result().body());
                        ctx.response().putHeader("content-type", "text/plain");
                        ctx.response().end("command from Vert.x!");
                    } else {
                        System.out.println("eb:incoming No reply");
                        ctx.response().putHeader("content-type", "text/plain");
                        ctx.response().end("eb:incoming No reply");
                    }
                });
            });
        };
    }
}

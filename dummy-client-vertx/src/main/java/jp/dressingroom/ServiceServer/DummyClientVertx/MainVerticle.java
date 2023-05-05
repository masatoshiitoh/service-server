package jp.dressingroom.ServiceServer.DummyClientVertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;

import static io.vertx.core.http.HttpMethod.GET;

public class MainVerticle extends AbstractVerticle {
    private List<WebCommandSequence> cmds = new ArrayList<>();

    public void listRunner(WebClient webClient, List<WebCommandSequence> cmds) {
        cmds.forEach(cmd -> {
            vertx.setTimer(cmd.milliSec, t -> {
                if (cmd.method == HttpMethod.GET) {
                    webClient.get(8888, "localhost", cmd.path).send(ar -> {
                        if (ar.succeeded()) {
                            System.out.println("Received response with status code" + ar.result().statusCode()
                                    + " with data " + ar.result().bodyAsString());
                        } else {
                            System.out.println("Something went wrong " + ar.cause().getMessage());
                        }
                    });
                } else if (cmd.method == HttpMethod.POST) {
                    System.out.println("cmd.postBody:" + cmd.postBody);
                    JsonObject json = new JsonObject(cmd.postBody);
                    System.out.println("cmd.postBody jsonObject:" + json.toString());

                    webClient.post(8888, "localhost", cmd.path).sendJsonObject(json).onSuccess(response -> {
                        System.out.println("Received response with status code" + response.statusCode() + " with data "
                                + response.bodyAsString());
                    }).onFailure(err -> {
                        System.out.println("Something went wrong " + err.getMessage());
                    });
                }
            });
        });
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        cmds.add(new WebCommandSequence(1, GET, "/top/1/", null));
        cmds.add(new WebCommandSequence(1000, HttpMethod.POST, "/command/1/", "{\"k1\":\"v1\",\"k2\":\"v2\"}"));
        cmds.add(new WebCommandSequence(1000, HttpMethod.POST, "/query/1/", "{\"command\":\"get_status\",\"id\":1}"));
        cmds.add(new WebCommandSequence(1000, HttpMethod.POST, "/query/1/", "{\"command\":\"get_status\",\"id\":2}"));
        cmds.add(new WebCommandSequence(200, GET, "/event/1/", null));
        WebClient webClient = WebClient.create(vertx);

        vertx.createHttpServer().requestHandler(req -> {
            this.listRunner(webClient, cmds);
            req.response().putHeader("content-type", "text/plain").end("I'm dummy client.");
        }).listen(8881, http -> {
            if (http.succeeded()) {
                startPromise.complete();
                System.out.println("HTTP server started on port 8881");
            } else {
                startPromise.fail(http.cause());
            }
        });
    }
}

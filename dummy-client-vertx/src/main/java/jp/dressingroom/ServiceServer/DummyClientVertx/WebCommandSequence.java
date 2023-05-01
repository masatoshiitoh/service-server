package jp.dressingroom.ServiceServer.DummyClientVertx;

import io.vertx.core.http.HttpMethod;

public class WebCommandSequence {
    int milliSec;
    HttpMethod method;
    String path;
    String postBody;

    public WebCommandSequence(int milliSec, HttpMethod method, String path, String postBody) {
        this.milliSec = milliSec;
        this.method = method;
        this.path = path;
        this.postBody = postBody;
    }
}

package com.sunkenpotato.updater;

import com.sunkenpotato.APIBlock;
import com.sunkenpotato.command.MethodArgumentType;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.async.methods.SimpleRequestBuilder;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.reactor.IOReactorConfig;

import java.util.ArrayList;
import java.util.List;

public class APIUpdater {
    public String httpLocation;
    public List<Header> headers = new ArrayList<>();
    private SimpleHttpRequest REQUEST;
    private static final CloseableHttpAsyncClient client = HttpAsyncClients.custom()
            .setIOReactorConfig(
                    IOReactorConfig.custom()
                            .setIoThreadCount(Runtime.getRuntime().availableProcessors())
                            .build()
            )
            .build();
    private MethodArgumentType.Method method = MethodArgumentType.Method.GET;
    private volatile boolean success = false;

    static {
        client.start();
    }

    public APIUpdater(String httpString) {
        httpLocation = httpString;
        REQUEST = SimpleRequestBuilder.get(httpLocation).build();
    }

    public void executeRequest() {
        if (this.httpLocation != null)
            client.execute(REQUEST, new APIResponseHandler(this));
    }

    private record APIResponseHandler(APIUpdater updater) implements FutureCallback<SimpleHttpResponse> {
        @Override
        public void completed(SimpleHttpResponse result) {
            updater.success = result.getCode() == 200;
        }

        @Override
        public void failed(Exception ex) {
            updater.success = false;
            APIBlock.LOGGER.error("Request to {} failed", ex.getMessage(), ex);
        }

        @Override
        public void cancelled() {
            updater.success = false;
            APIBlock.LOGGER.warn("Request to {} was cancelled", updater.httpLocation);
        }
    }

    public void setURL(String url) {
        httpLocation = url;
        REQUEST = SimpleRequestBuilder.get(httpLocation).build();
    }

    public void setHeaders(List<Header> headers) {
        this.headers = headers;
        REQUEST = SimpleRequestBuilder.get(httpLocation).build();
        REQUEST.setHeaders();

        for (Header header : headers) {
            REQUEST.addHeader(header);
        }
    }

    public void setMethod(MethodArgumentType.Method method) {
        this.method = method;
    }

    public MethodArgumentType.Method getMethod() {
        return method;
    }

    public void addHeader(String name, String value) {
        Header header = new BasicHeader(name, value);
        this.headers.add(header);
        REQUEST = SimpleRequestBuilder.get(httpLocation).build();
        REQUEST.addHeader(header);
    }

    public boolean isSuccess() {
        return success;
    }
}

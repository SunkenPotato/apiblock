package com.sunkenpotato.updater;

import com.sunkenpotato.APIBlock;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.async.methods.SimpleRequestBuilder;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.reactor.IOReactorConfig;

import java.net.*;
import java.util.List;

public class APIUpdater {
    private static final CloseableHttpAsyncClient client = HttpAsyncClients.custom()
            .setIOReactorConfig(
                    IOReactorConfig.custom()
                            .setIoThreadCount(Runtime.getRuntime().availableProcessors())
                            .build()
            )
            .build();

    static {
        client.start();
    }

    public URI httpLocation;
    private SimpleRequestBuilder REQUEST_BUILDER;
    private Method method = Method.GET;
    private volatile boolean success = true;

    public APIUpdater(String httpString) throws IllegalArgumentException {
        httpLocation = URI.create(httpString);
        REQUEST_BUILDER = SimpleRequestBuilder.create(method).setUri(httpLocation);
    }

    public void executeRequest() {
        client.execute(REQUEST_BUILDER.build(), new APIResponseHandler(this));
    }

    public void setURI(String uri) throws IllegalArgumentException, MalformedURLException {
        var parsedUri = URI.create(uri);

        //noinspection ResultOfMethodCallIgnored
        parsedUri.toURL();

        httpLocation = parsedUri;
        REQUEST_BUILDER.setUri(httpLocation);
    }

    public void setHeaders(List<Header> headers) {
        REQUEST_BUILDER.setHeaders(headers.iterator());
    }

    public List<Header> getHeaders() {
        Header[] headers = REQUEST_BUILDER.getHeaders();
        if (headers == null) headers = new Header[]{};

        return List.of(headers);
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
        REQUEST_BUILDER = SimpleRequestBuilder.create(method).setHeaders(REQUEST_BUILDER.getHeaders()).setUri(httpLocation);
    }

    public void addHeader(String name, String value) {
        Header header = new BasicHeader(name, value);
        REQUEST_BUILDER.addHeader(header);
    }

    public boolean isSuccess() {
        return success;
    }

    private record APIResponseHandler(APIUpdater updater) implements FutureCallback<SimpleHttpResponse> {
        @Override
        public void completed(SimpleHttpResponse result) {
            System.out.println(result.getCode());
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
}

package com.sunkenpotato.updater;

import com.sunkenpotato.APIBlock;
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

    private String httpLocation;
    private List<Header> headers = new ArrayList<>();
    private SimpleHttpRequest GET_REQUEST;
    private int tickSpace = 20;
    private static final CloseableHttpAsyncClient client;
    private volatile boolean success = false;

    static {
        client = HttpAsyncClients.custom()
                .setIOReactorConfig(
                        IOReactorConfig.custom()
                                .setIoThreadCount(Runtime.getRuntime().availableProcessors())
                                .build()
                )
                .build();
        client.start();
    }

    public APIUpdater(String httpString) {
        httpLocation = httpString;
        GET_REQUEST = SimpleRequestBuilder.get(httpLocation).build();
    }

    public void executeRequest() {
        client.execute(GET_REQUEST, new APIResponseHandler(this));
    }

    private static class APIResponseHandler implements FutureCallback<SimpleHttpResponse> {

        APIUpdater updater;

        public APIResponseHandler(APIUpdater updater) {
            this.updater = updater;
        }

        @Override
        public void completed(SimpleHttpResponse result) {
            if (result.getCode() == 200) updater.success = true;
        }

        @Override
        public void failed(Exception ex) {
            updater.success = false;
        }

        @Override
        public void cancelled() {
            updater.success = false;
            APIBlock.LOGGER.warn("Request to {} was cancelled", updater.httpLocation);
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public void setHttpLocation(String url) {
        httpLocation = url;
        GET_REQUEST = SimpleRequestBuilder.get(httpLocation).build();
    }

    public String getHttpLocation() {
        return httpLocation;
    }

    public void setTickSpace(int tickSpace) {
        this.tickSpace = tickSpace;
    }

    public int getTickSpace() {
        return tickSpace;
    }

    public void setHeaders(List<Header> headers) {
        this.headers = headers;
        GET_REQUEST = SimpleRequestBuilder.get(httpLocation).build();
        GET_REQUEST.setHeaders();

        for (Header header : headers) {
            GET_REQUEST.addHeader(header);
        }
    }

    public void addHeader(String name, String value) {
        Header header = new BasicHeader(name, value);
        this.headers.add(header);
        GET_REQUEST = SimpleRequestBuilder.get(httpLocation).build();
        GET_REQUEST.addHeader(header);
    }

    public boolean deleteHeader(String name) {
        this.headers.removeIf(x -> x.getName().equals(name));
        return GET_REQUEST.removeHeaders(name);
    }

    public List<Header> getHeaders() {
        return headers;
    }
}

package com.erenes.generals;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.ws.rs.core.Response;

import java.util.Map;
@RegisterForReflection
public class HttpResponse {

    HttpResponse(){}

    public String timeStamp;
    public int statusCode;

    public Response.Status status;

    public String reason;

    public String message;

    public String developerMessage;

    public Map<?,?> data;

    public static Builder newBuilder(){
        return new Builder();
    }

    public static class Builder {
        private final HttpResponse httpResponse;

        public Builder() {
            this.httpResponse = new HttpResponse();
        }

        public Builder timeStamp(String timeStamp) {
            httpResponse.timeStamp = timeStamp;
            return this;
        }

        public Builder statusCode(int statusCode) {
            httpResponse.statusCode = statusCode;
            return this;
        }

        public Builder status(Response.Status status) {
            httpResponse.status = status;
            return this;
        }

        public Builder reason(String reason) {
            httpResponse.reason = reason;
            return this;
        }

        public Builder message(String message) {
            httpResponse.message = message;
            return this;
        }

        public Builder developerMessage(String developerMessage) {
            httpResponse.developerMessage = developerMessage;
            return this;
        }

        public Builder data(Map<?, ?> data) {
            httpResponse.data = data;
            return this;
        }

        public HttpResponse build() {
            return httpResponse;
        }
    }
}

FROM envoyproxy/envoy:v1.26-latest
ARG envoy_env
RUN echo "Building configuration for '$envoy_env' environment..."

COPY ./utils/certs/ca-cert.pem                              /etc/ca.cert
COPY ./utils/certs/$envoy_env/server-key.pem                /etc/server.key
COPY ./utils/certs/$envoy_env/server-cert-$envoy_env.pem    /etc/server.cert
COPY ./src/city-tasks-api-proxy/envoy.yaml                  /etc/envoy/envoy.yaml

RUN chmod go+r /etc/envoy/envoy.yaml
EXPOSE 443

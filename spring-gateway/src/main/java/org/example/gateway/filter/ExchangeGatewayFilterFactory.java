package org.example.gateway.filter;

import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.security.Security;

/**
 * @author renc
 */
@Component
public class ExchangeGatewayFilterFactory extends AbstractBodyRewriteGatewayFilterFactory<Object> {

    static { if (Security.getProvider("BC") == null) Security.addProvider(new BouncyCastleProvider());}

    private static final X9ECParameters X9EC_PARAMETERS = GMNamedCurves.getByOID(GMObjectIdentifiers.sm2p256v1);

    private static final ECParameterSpec EC_PARAMETER_SPEC = new ECParameterSpec(
            X9EC_PARAMETERS.getCurve(), X9EC_PARAMETERS.getG(), X9EC_PARAMETERS.getN());

    public ExchangeGatewayFilterFactory() {
        super(Object.class);
    }

    @Override
    public GatewayFilter apply(Object config) {
        return new OrderedGatewayFilter((exchange, chain) -> {
            return rewrite(exchange, chain, requestTrans(), responseTrans())
                    .doOnError(ex -> {
                        System.out.println("exchange error");
                    });
        }, NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 10);
    }

    private RewriteFunction<byte[], byte[]> responseTrans() {
        return ((exchange, bytes) -> Mono.just(bytes));
    }

    private RewriteFunction<byte[], byte[]> requestTrans() {
        return (exchange, bytes) -> Mono.just(bytes);
    }
}

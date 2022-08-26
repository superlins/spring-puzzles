// package org.example.gateway.filter;
//
// import org.bouncycastle.asn1.gm.GMNamedCurves;
// import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
// import org.bouncycastle.asn1.x9.X9ECParameters;
// import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
// import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
// import org.bouncycastle.jce.provider.BouncyCastleProvider;
// import org.bouncycastle.jce.spec.ECParameterSpec;
// import org.bouncycastle.jce.spec.ECPrivateKeySpec;
// import org.bouncycastle.jce.spec.ECPublicKeySpec;
// import org.bouncycastle.util.encoders.Base64;
// import org.bouncycastle.util.encoders.Hex;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.cloud.gateway.filter.GatewayFilter;
// import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
// import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
// import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
// import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
// import org.springframework.data.util.Pair;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.server.reactive.ServerHttpRequest;
// import org.springframework.http.server.reactive.ServerHttpResponse;
// import org.springframework.stereotype.Component;
// import org.springframework.util.Assert;
// import org.springframework.web.server.ResponseStatusException;
// import reactor.core.publisher.Mono;
//
// import javax.crypto.Cipher;
// import javax.crypto.KeyGenerator;
// import javax.crypto.SecretKey;
// import javax.crypto.spec.IvParameterSpec;
// import javax.crypto.spec.SecretKeySpec;
// import java.math.BigInteger;
// import java.security.*;
// import java.security.spec.ECGenParameterSpec;
// import java.security.spec.KeySpec;
// import java.util.Arrays;
// import java.util.List;
// import java.util.Map;
//
// import static java.util.Objects.requireNonNull;
// import static java.util.stream.Collectors.toMap;
// import static org.bouncycastle.asn1.gm.GMObjectIdentifiers.sm2sign_with_sm3;
// import static org.example.gateway.filter.HeaderConstants.*;
// import static org.springframework.data.util.Pair.of;
// import static org.springframework.http.HttpStatus.BAD_REQUEST;
// import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
//
// /**
//  * @author renc
//  */
// @Component
// public class GMExchangeGatewayFilterFactory extends AbstractBodyRewriteGatewayFilterFactory<GMExchangeGatewayFilterFactory.Config> {
//
//     private static final Logger log = LoggerFactory.getLogger(GMExchangeGatewayFilterFactory.class);
//
//     static { if (Security.getProvider("BC") == null) Security.addProvider(new BouncyCastleProvider());}
//
//     private static final X9ECParameters X9EC_PARAMETERS = GMNamedCurves.getByOID(GMObjectIdentifiers.sm2p256v1);
//
//     private static final ECParameterSpec EC_PARAMETER_SPEC = new ECParameterSpec(
//             X9EC_PARAMETERS.getCurve(), X9EC_PARAMETERS.getG(), X9EC_PARAMETERS.getN());
//
//     private static final String GATEWAY_ROUTE_SECRET_KEY_ATTR = ServerWebExchangeUtils.class.getName() + ".gatewayGMSK";
//
//     public GMExchangeGatewayFilterFactory() {
//         super(Config.class);
//     }
//
//     @Override
//     public GatewayFilter apply(Config config) {
//         final Map<String, Pair<PublicKey, PrivateKey>> idMappings = config.getConsumers().stream()
//                 .collect(toMap(Config.Consumer::getId, c -> of(publicKey(c.getPbk()), privateKey(c.getPvk()))));
//         final Pair<PublicKey, PrivateKey> ownKP = idMappings.get(Config.Consumer.OWN);
//         if (ownKP == null) {
//             throw new NullPointerException("the consumer '" + Config.Consumer.OWN + "' not found");
//         }
//         return new OrderedGatewayFilter((exchange, chain) -> {
//             ServerHttpRequest request = exchange.getRequest();
//             String id = request.getHeaders().getFirst(HeaderConstants.X_KONG_CONSUMER_CUSTOM_ID);
//             Pair<PublicKey, PrivateKey> kp = idMappings.get(id);
//             if (kp == null) {
//                 throw new NullPointerException("unknown consumer id: " + id);
//             }
//
//             return rewrite(exchange, chain, requestTrans(ownKP, kp), responseTrans(ownKP, kp))
//                     .doOnError(ex -> log.error("gm exchange error", ex));
//         }, NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 10);
//     }
//
//     private RewriteFunction<byte[], byte[]> responseTrans(Pair<PublicKey, PrivateKey> self, Pair<PublicKey, PrivateKey> other) {
//         return ((exchange, bytes) -> {
//             ServerHttpRequest request = exchange.getRequest();
//             ServerHttpResponse response = exchange.getResponse();
//             try {
//                 String accessId = request.getHeaders().getFirst(X_ACCESS_ID);
//                 SecretKeySpec key = (SecretKeySpec) exchange.getAttributes().get(GATEWAY_ROUTE_SECRET_KEY_ATTR);
//
//                 Cipher cipher = Cipher.getInstance("SM4/CBC/PKCS5PADDING", "BC");
//                 cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(Hex.decode(accessId)));
//                 byte[] enc = cipher.doFinal(bytes);
//
//                 Signature signature = Signature.getInstance(sm2sign_with_sm3.toString(), "BC");
//                 signature.initSign(self.getSecond());
//                 signature.update(enc);
//                 byte[] sign = signature.sign();
//                 response.getHeaders().put(HeaderConstants.X_SIGNATURE, Arrays.asList(Base64.toBase64String(sign)));
//
//                 return Mono.just(enc);
//             } catch (Exception e) {
//                 return Mono.error(new ResponseStatusException(INTERNAL_SERVER_ERROR, "response transform error", e));
//             }
//         });
//     }
//
//     private RewriteFunction<byte[], byte[]> requestTrans(Pair<PublicKey, PrivateKey> self, Pair<PublicKey, PrivateKey> other) {
//         return (exchange, bytes) -> {
//             try {
//                 Assert.notNull(bytes, "the body must not be null");
//                 HttpHeaders headers = exchange.getRequest().getHeaders();
//                 String accessId = requireNonNull(headers.getFirst(X_ACCESS_ID), "the access id must not be null");
//                 String sign = requireNonNull(headers.getFirst(X_SIGNATURE), "the sign must not be null");
//                 String dek = requireNonNull(headers.getFirst(X_DEK), "the dek must not be null");
//
//                 Signature signature = Signature.getInstance(sm2sign_with_sm3.toString(), "BC");
//                 signature.initVerify(other.getFirst());
//                 signature.update(bytes);
//                 Assert.isTrue(signature.verify(Base64.decode(sign)), "sign verify failed");
//
//                 Cipher sm2 = Cipher.getInstance("SM2", "BC");
//                 sm2.init(Cipher.DECRYPT_MODE, self.getSecond());
//                 byte[] dec = sm2.doFinal(Base64.decode(dek));
//                 SecretKeySpec key = new SecretKeySpec(dec, "SM4");
//                 exchange.getAttributes().put(GATEWAY_ROUTE_SECRET_KEY_ATTR, key);
//
//                 Cipher sm4 = Cipher.getInstance("SM4/CBC/PKCS5PADDING", "BC");
//                 sm4.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(Hex.decode(accessId)));
//                 byte[] decipher = sm4.doFinal(bytes);
//
//                 return Mono.just(decipher);
//             } catch (NullPointerException | IllegalArgumentException e) {
//                 return Mono.error(new ResponseStatusException(BAD_REQUEST, e.getMessage()));
//             } catch (Exception e) {
//                 return Mono.error(new ResponseStatusException(INTERNAL_SERVER_ERROR, "request transform error", e));
//             }
//         };
//     }
//
//     public static class Config {
//
//         private List<Consumer> consumers;
//
//         public List<Consumer> getConsumers() {
//             return consumers;
//         }
//
//         public void setConsumers(List<Consumer> consumers) {
//             this.consumers = consumers;
//         }
//
//         static class Consumer {
//
//             public static final String OWN = "OWN";
//
//             /**
//              * The customer id (or bizNo)
//              */
//             private String id;
//
//             /**
//              * The sm2 asymmetric cipher as public-key hex string
//              */
//             private String pbk;
//
//             /**
//              * The sm2 asymmetric cipher as private-key hex string
//              */
//             private String pvk;
//
//             public String getId() {
//                 return id;
//             }
//
//             public void setId(String id) {
//                 this.id = id;
//             }
//
//             public String getPbk() {
//                 return pbk;
//             }
//
//             public void setPbk(String pbk) {
//                 this.pbk = pbk;
//             }
//
//             public String getPvk() {
//                 return pvk;
//             }
//
//             public void setPvk(String pvk) {
//                 this.pvk = pvk;
//             }
//         }
//     }
//
//     /**
//      * Generates a sm2 key pair from BouncyCastle provider.
//      *
//      * @return the generated key pair
//      */
//     public static KeyPair genKeyPair() throws Exception {
//         KeyPairGenerator generator = KeyPairGenerator.getInstance("EC", "BC");
//         generator.initialize(new ECGenParameterSpec("sm2p256v1"));
//         return generator.generateKeyPair();
//     }
//
//     /**
//      * Transform BCECPublicKey to hex string.
//      *
//      * @param p the BCECPublicKey instance
//      * @return a hex string represent the BCECPublicKey
//      */
//     public static String encodedHex(BCECPublicKey p) {
//         return Hex.toHexString(p.getQ().getEncoded(false));
//     }
//
//     /**
//      * Transform BCECPrivateKey to hex string.
//      *
//      * @param p the BCECPrivateKey instance
//      * @return a hex string represent the BCECPrivateKey
//      */
//     public static String encodedHex(BCECPrivateKey p) {
//         return Hex.toHexString(p.getD().toByteArray());
//     }
//
//     /**
//      * Transform hex string to BCECPublicKey.
//      *
//      * @param encodedHex a hex string represent the BCECPublicKey
//      * @return the BCECPublicKey instance
//      */
//     public static PublicKey publicKey(String encodedHex) {
//         try {
//             KeySpec keySpec = new ECPublicKeySpec(X9EC_PARAMETERS.getCurve().decodePoint(Hex.decode(encodedHex)), EC_PARAMETER_SPEC);
//             KeyFactory keyFactory = KeyFactory.getInstance("EC", "BC");
//             return keyFactory.generatePublic(keySpec);
//         } catch (Exception e) {
//             throw new RuntimeException(e);
//         }
//     }
//
//     /**
//      * Transform hex string to BCECPrivateKey.
//      *
//      * @param encodedHex a hex string represent the BCECPrivateKey
//      * @return the BCECPrivateKey instance
//      */
//     public static PrivateKey privateKey(String encodedHex) {
//         try {
//             KeySpec keySpec = new ECPrivateKeySpec(new BigInteger(encodedHex, 16), EC_PARAMETER_SPEC);
//             KeyFactory keyFactory = KeyFactory.getInstance("EC", "BC");
//             return keyFactory.generatePrivate(keySpec);
//         } catch (Exception e) {
//             throw new RuntimeException(e);
//         }
//     }
//
//     /**
//      * Generates a sm4 random secret key.
//      *
//      * @return the sm4 secret key instance
//      */
//     public static SecretKey genKey() throws Exception {
//         KeyGenerator generator = KeyGenerator.getInstance("SM4");
//         generator.init(128);
//         return generator.generateKey();
//     }
// }

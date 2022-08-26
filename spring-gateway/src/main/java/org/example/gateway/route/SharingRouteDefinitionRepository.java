// package org.example.gateway.route;
//
// import com.fasterxml.jackson.core.JsonProcessingException;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import org.springframework.cloud.gateway.route.RouteDefinition;
// import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
// import org.springframework.cloud.gateway.support.NotFoundException;
// import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
// import org.springframework.stereotype.Repository;
// import org.springframework.util.ObjectUtils;
// import reactor.core.publisher.Flux;
// import reactor.core.publisher.Mono;
//
// import static org.springframework.data.relational.core.query.Criteria.where;
// import static org.springframework.data.relational.core.query.Query.query;
//
// /**
//  * @author renc
//  */
// @Repository
// public class SharingRouteDefinitionRepository implements RouteDefinitionRepository {
//
//     private final ObjectMapper objectMapper;
//
//     private final R2dbcEntityTemplate r2dbcEntityTemplate;
//
//     public SharingRouteDefinitionRepository(R2dbcEntityTemplate r2dbcEntityTemplate, ObjectMapper objectMapper) {
//         this.objectMapper = objectMapper;
//         this.r2dbcEntityTemplate = r2dbcEntityTemplate;
//     }
//
//     @Override
//     public Mono<Void> save(Mono<RouteDefinition> route) {
//         return route.flatMap(r -> {
//             if (ObjectUtils.isEmpty(r.getId())) {
//                 return Mono.error(new IllegalArgumentException("id may not be empty"));
//             }
//             RouteDefinitionEntity routeDefinitionEntity = new RouteDefinitionEntity();
//             routeDefinitionEntity.setId(r.getId());
//             try {
//                 routeDefinitionEntity.setContent(objectMapper.writeValueAsString(r));
//             } catch (JsonProcessingException e) {
//                 return Mono.error(e);
//             }
//
//             return r2dbcEntityTemplate.insert(routeDefinitionEntity).then();
//         });
//     }
//
//     @Override
//     public Mono<Void> delete(Mono<String> routeId) {
//         return routeId.flatMap(id -> r2dbcEntityTemplate.selectOne(query(where("id").is(id)), RouteDefinitionEntity.class)
//                 .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("RouteDefinition not found: " + routeId))))
//                 .flatMap(entity -> r2dbcEntityTemplate.delete(entity))
//                 .then()
//         );
//     }
//
//     @Override
//     public Flux<RouteDefinition> getRouteDefinitions() {
//         return r2dbcEntityTemplate.select(RouteDefinitionEntity.class)
//                 .all()
//                 .map(r -> {
//                     try {
//                         return objectMapper.readValue(r.getContent(), RouteDefinition.class);
//                     } catch (JsonProcessingException e) {
//                         throw new RuntimeException(e);
//                     }
//                 });
//     }
// }

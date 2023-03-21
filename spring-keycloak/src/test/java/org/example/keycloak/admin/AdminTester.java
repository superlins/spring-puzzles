package org.example.keycloak.admin;

import com.oceanum.adapters.keycloak.idm.HierarchicalResourceRepresentation;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.*;
import org.keycloak.admin.client.resource.*;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.resource.AuthorizationResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.authorization.*;
import org.keycloak.representations.idm.authorization.PolicyEvaluationResponse.EvaluationResultRepresentation;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * @author renc
 */
public class AdminTester {

    AuthzClient authzClient;

    Keycloak keycloak;

    @BeforeEach
    public void init() {
        System.setProperty("org.jboss.logging", "info");

        ClientBuilder clientBuilder = ClientBuilderWrapper.create(null, true);
        clientBuilder.register(JacksonProvider.class, 100);
        ResteasyClient resteasyClient = (ResteasyClient) clientBuilder.build();

        // authzClient = AuthzClient.create();
        keycloak = KeycloakBuilder.builder()
                .serverUrl("https://keycloak.oceanum.local")
                .realm("master")
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId("admin-cli")
                .clientSecret("nlDrcluuWTJkaH0M3VICIv4c5t2fLsga")
                .resteasyClient(resteasyClient)
                .build();
    }

    @Test
    public void testAuthz() {

        RealmResource oceanum = keycloak.realm("oceanum");
        ClientResource obi_dataease = oceanum.clients().get("f5eff98e-52c4-416d-b25c-4d3553fffdc8");
        ClientResource ocp_data_manage = oceanum.clients().get("7e83bb7e-fa2b-47db-8676-a32b283ea303");
        ClientResource ocp_web_service = oceanum.clients().get("f5230b5b-cac1-4dee-b6b4-ff916b899d68");
        ClientResource ocp_exchange = oceanum.clients().get("53735e50-fbde-40e9-b9e3-e206d28ab245");

        Arrays.asList(ocp_exchange, obi_dataease, ocp_data_manage, ocp_web_service)
                .forEach(clientResource -> {
                    ResourcesResource resources = clientResource.authorization().resources();
                    List<UserRepresentation> users = oceanum.users().list(0, 1000);
                    users.forEach(u -> {
                        PolicyEvaluationRequest policyEvaluationRequest = new PolicyEvaluationRequest();
                        policyEvaluationRequest.setUserId(u.getId());

                        List<EvaluationResultRepresentation> results = clientResource
                                .authorization()
                                .policies()
                                .evaluate(policyEvaluationRequest)
                                .getResults();

                        Map<String, HierarchicalResourceRepresentation> mappings = results.stream()
                                .filter(ev -> ev.getStatus() == DecisionEffect.PERMIT)
                                .map(EvaluationResultRepresentation::getResource)
                                .map(ResourceRepresentation::getId)
                                .map(rid -> resources.resource(rid).toRepresentation())
                                .map(HierarchicalResourceRepresentation::transform)
                                .collect(toMap(m -> m.getName(), identity()));

                        List<HierarchicalResourceRepresentation> representations = mappings.values().stream()
                                .filter(representation -> {
                                    Map<String, List<String>> attributes = representation.getAttributes();
                                    if (attributes == null || attributes.isEmpty()) {
                                        return true;
                                    }
                                    List<String> resourceNames = attributes.get("parent");
                                    if (resourceNames == null || resourceNames.isEmpty() || resourceNames.get(0).isEmpty()) {
                                        return true;
                                    }
                                    HierarchicalResourceRepresentation parent = mappings.get(resourceNames.get(0));
                                    if (parent == null) {
                                        throw new IllegalArgumentException(String.format("Resource with name '%s' not found", resourceNames.get(0)));
                                    }
                                    parent.addSubResource(representation);
                                    return false;
                                }).sorted().collect(toList());

                        representations.forEach(this::sort);

                        representations.forEach(top -> {
                            List<String> names = new ArrayList<>();
                            names.add(joining(names, top));
                            Collections.reverse(names);


                            Path path = Paths.get("/Users/renc/iCoder/IdeaProjects/spring-puzzles/spring-keycloak/src/test/resources")
                                    .resolve(Paths.get(clientResource.toRepresentation().getName() + ".csv"));
                            try {
                                BufferedWriter bw = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                                bw.write(u.getLastName() + u.getFirstName());
                                bw.write(',');
                                bw.write(names.stream().collect(Collectors.joining("/")));
                                bw.newLine();
                                bw.flush();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    });
                });
    }

    private String joining(List<String> names, HierarchicalResourceRepresentation top) {
        List<HierarchicalResourceRepresentation> subResources = top.getSubResources();
        if (subResources != null && !subResources.isEmpty()) {
            for (HierarchicalResourceRepresentation sub : subResources) {
                names.add(joining(names, sub));
            }
        }
        return top.getDisplayName();
    }

    private void sort(HierarchicalResourceRepresentation top) {
        List<HierarchicalResourceRepresentation> subResources = top.getSubResources();
        if (subResources != null && !subResources.isEmpty()) {
            subResources.sort(HierarchicalResourceRepresentation::compareTo);
            for (HierarchicalResourceRepresentation resource : subResources) {
                sort(resource);
            }
        }
    }

    @Test
    public void testViewUsers() {
        List<UserRepresentation> search = keycloak.realm("extract").users().search("chenchen");
        System.out.println(search.get(0));
    }

    @Test
    public void testNotNull() {
        Assertions.assertNotNull(authzClient);
        Assertions.assertNotNull(keycloak);
    }

    @Test
    public void testPerms() {
        AuthorizationResource authorization = authzClient.authorization();
        AuthorizationResponse authorize = authorization.authorize();
        String accessToken = authorize.getToken();
        System.out.println(accessToken);

        PermissionRequest permissionRequest = new PermissionRequest();
        permissionRequest.setResourceId("de332a99-4e65-4191-a112-172f27ae0358");

        PermissionResponse permissionResponse = authzClient.protection()
                .permission()
                .create(permissionRequest);
        System.out.println(permissionResponse);
    }

    @Test
    public void testResources() {
        List<ResourceRepresentation> resources = keycloak.realm("hello-world-authz")
                .clients()
                .get("7a1b3a25-0528-4a7a-adf1-be06d9a9ac03")
                .authorization()
                .resources()
                .resources();
        System.out.println(resources);
    }

    @Test
    public void testPolicies() {
        List<PolicyRepresentation> policies = keycloak.realm("hello-world-authz")
                .clients()
                .get("7a1b3a25-0528-4a7a-adf1-be06d9a9ac03")
                .authorization()
                .policies()
                .policies();
        System.out.println(policies);
    }

    @Test
    public void testPermissions() {
        PermissionsResource permissions = keycloak.realm("hello-world-authz")
                .clients()
                .get("7a1b3a25-0528-4a7a-adf1-be06d9a9ac03")
                .authorization()
                .permissions();

        List<ResourceRepresentation> resources = permissions.resource()
                .findById("f941ca57-c12b-4094-8ba6-d3600b0b7a6f")
                .resources();

        List<PolicyRepresentation> policyRepresentations = permissions.resource()
                .findById("f941ca57-c12b-4094-8ba6-d3600b0b7a6f")
                .dependentPolicies();


        // ResourcePermissionsResource resourcePermissions = permissions.resource();
        // ScopePermissionsResource scopePermissions = permissions.scope();

        System.out.println();
    }

    @Test
    public void testUserProfile() {
        UsersResource users = keycloak.realm("oceanum").users();
        UserRepresentation mike = users.search("mike").get(0);
        UserResource userResource = users.get(mike.getId());

        Map<String, List<String>> attributes = mike.getAttributes();
        System.out.println(attributes);
        attributes.put("department", Arrays.asList("biz"));

        userResource.update(mike);

        System.out.println();
    }

    @Test
    public void testApi() {
        String serverUrl = "http://sso.tdlabs.local:8899/u/auth";
        String realm = "acme";
// idm-client needs to allow "Direct Access Grants: Resource Owner Password Credentials Grant"
        String clientId = "idm-client";
        String clientSecret = "0d61686d-57fc-4048-b052-4ce74978c468";
//		// Client "idm-client" needs service-account with at least "manage-users, view-clients, view-realm, view-users" roles for "realm-management"
//		Keycloak keycloak = KeycloakBuilder.builder() //
//				.serverUrl(serverUrl) //
//				.realm(realm) //
//				.grantType(OAuth2Constants.CLIENT_CREDENTIALS) //
//				.clientId(clientId) //
//				.clientSecret(clientSecret).build();
// User "idm-admin" needs at least "manage-users, view-clients, view-realm, view-users" roles for "realm-management"
        Keycloak keycloak = KeycloakBuilder.builder() //
                .serverUrl(serverUrl) //
                .realm(realm) //
                .grantType(OAuth2Constants.PASSWORD) //
                .clientId(clientId) //
                .clientSecret(clientSecret) //
                .username("idm-admin") //
                .password("admin") //
                .build();
// Define user
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername("tester1");
        user.setFirstName("First");
        user.setLastName("Last");
        user.setEmail("tom+tester1@tdlabs.local");
        user.setAttributes(Collections.singletonMap("origin", Arrays.asList("demo")));
// Get realm
        RealmResource realmResource = keycloak.realm(realm);
        UsersResource usersRessource = realmResource.users();
// Create user (requires manage-users role)
        Response response = usersRessource.create(user);
        System.out.printf("Repsonse: %s %s%n", response.getStatus(), response.getStatusInfo());
        System.out.println(response.getLocation());
        String userId = CreatedResponseUtil.getCreatedId(response);
        System.out.printf("User created with userId: %s%n", userId);
// Define password credential
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue("test");
        UserResource userResource = usersRessource.get(userId);
// Set password credential
        userResource.resetPassword(passwordCred);
//        // Get realm role "tester" (requires view-realm role)
        RoleRepresentation testerRealmRole = realmResource.roles()//
                .get("tester").toRepresentation();
//
//        // Assign realm role tester to user
        userResource.roles().realmLevel() //
                .add(Arrays.asList(testerRealmRole));
//
//        // Get client
        ClientRepresentation app1Client = realmResource.clients() //
                .findByClientId("app-frontend-springboot").get(0);
//
//        // Get client level role (requires view-clients role)
        RoleRepresentation userClientRole = realmResource.clients().get(app1Client.getId()) //
                .roles().get("user").toRepresentation();
//
//        // Assign client level role to user
        userResource.roles() //
                .clientLevel(app1Client.getId()).add(Arrays.asList(userClientRole));
// Send password reset E-Mail
// VERIFY_EMAIL, UPDATE_PROFILE, CONFIGURE_TOTP, UPDATE_PASSWORD, TERMS_AND_CONDITIONS
//        usersRessource.get(userId).executeActionsEmail(Arrays.asList("UPDATE_PASSWORD"));
// Delete User
//        userResource.remove();
    }
}

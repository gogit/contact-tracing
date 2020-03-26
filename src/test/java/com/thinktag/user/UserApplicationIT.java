package com.thinktag.user;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * This test can only be run when the JWT service is up and running
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        ContactTracingApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserApplicationIT {

    @LocalServerPort
    private int port;

    private RestTemplate restTemplate = new RestTemplate();


    @Test
    public void testCreateUserCheckLogin() {
        HttpHeaders headers = new HttpHeaders();
        //headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        Map<String, String> params = new HashMap<String, String>();
        ResponseEntity<String> uuid =
                this.restTemplate.postForEntity("http://localhost:" + port + "/public/api/register?username=John&password=pass",
                        entity,
                        String.class, params);
        System.out.println(uuid.toString());

        Assert.assertEquals(200, uuid.getStatusCode().value());

        ResponseEntity<String> token =
                this.restTemplate.postForEntity("http://localhost:" + port + "/public/api/login?username=John&password=pass",
                        entity,
                        String.class, params);
        System.out.println(token.toString());
        Assert.assertEquals(200, token.getStatusCode().value());

        ParameterizedTypeReference<HashMap<String, String>> responseType =
                new ParameterizedTypeReference<HashMap<String, String>>() {
                };

        RequestEntity<Void> request = RequestEntity.post(URI.create("http://localhost:8081/api/token/validate?token=" + token.getBody()))
                .accept(MediaType.APPLICATION_JSON).build();

        ResponseEntity<HashMap<String, String>> response = restTemplate.exchange(request, responseType);

        System.out.println(response.toString());
        //{"iss":"thinktag","exp":"1578925506","iat":"1578923706","username":"John"}
        System.out.println(response.toString());
        Assert.assertEquals(200, response.getStatusCode().value());
        Assert.assertTrue(response.getBody().containsKey("iss"));
        Assert.assertTrue(response.getBody().get("iss").equals("thinktag"));
        Assert.assertTrue(response.getBody().get("username").equals("John"));

        long issuedAt = Long.parseLong(response.getBody().get("iat"));
        long expAt = Long.parseLong(response.getBody().get("exp"));


    }

}

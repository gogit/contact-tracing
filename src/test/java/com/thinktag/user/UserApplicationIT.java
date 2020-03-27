package com.thinktag.user;

import com.thinktag.user.model.User;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
    public void testRegisterMobile() {
        register("071");
    }

    @Test
    public void testContact() {
        User user1 = register("01");
        User user2 = register("02");
        User user3 = register("03");
        contact(user1.getValidationCode(), user2.getValidationCodeHash());
        contact(user2.getValidationCode(), user3.getValidationCodeHash());
    }

    @Test
    public void testDirectContact() {
        User user1 = register("01");
        User user2 = register("02");
        User user3 = register("03");
        contact(user1.getValidationCode(), user2.getValidationCodeHash());
        contact(user2.getValidationCode(), user3.getValidationCodeHash());

        List<User> user1Contacts = getDirectContact(user1.getValidationCode());
        Assert.assertEquals(1, user1Contacts.size());
        Assert.assertEquals("02", user1Contacts.get(0).getMobile());

        List<User> user2Contacts = getDirectContact(user2.getValidationCode());
        Assert.assertEquals(1, user2Contacts.size());
        Assert.assertEquals("03", user2Contacts.get(0).getMobile());
    }

    @Test
    public void testDirectContactRegisterBoth() {
        User user1 = register("01");
        User user2 = register("02");
        contact(user1.getValidationCode(), user2.getValidationCodeHash());
        contact(user2.getValidationCode(), user1.getValidationCodeHash());

        List<User> user1Contacts = getDirectContact(user1.getValidationCode());
        Assert.assertEquals(1, user1Contacts.size());
        Assert.assertEquals("02", user1Contacts.get(0).getMobile());

        List<User> user2Contacts = getDirectContact(user2.getValidationCode());
        Assert.assertEquals(1, user2Contacts.size());
        Assert.assertEquals("01", user2Contacts.get(0).getMobile());
    }

    @Test
    public void testTraceContact() {
        User user1 = register("01");
        User user2 = register("02");
        User user3 = register("03");
        contact(user1.getValidationCode(), user2.getValidationCodeHash());
        contact(user2.getValidationCode(), user3.getValidationCodeHash());

        List<User> user1Contacts = getTraceContact(user1.getValidationCode());
        Assert.assertEquals(2, user1Contacts.size());

    }


    @Test
    public void testRepeatedDirectContact() {
        User user1 = register("01");
        User user2 = register("02");
        contact(user1.getValidationCode(), user2.getValidationCodeHash());
        contact(user2.getValidationCode(), user1.getValidationCodeHash());

        contact(user1.getValidationCode(), user2.getValidationCodeHash());
        contact(user2.getValidationCode(), user1.getValidationCodeHash());

        List<User> user1Contacts = getDirectContact(user1.getValidationCode());
        Assert.assertEquals(1, user1Contacts.size());
        Assert.assertEquals("02", user1Contacts.get(0).getMobile());

        List<User> user2Contacts = getDirectContact(user2.getValidationCode());
        Assert.assertEquals(1, user2Contacts.size());
        Assert.assertEquals("01", user2Contacts.get(0).getMobile());
    }
    private List<User> getTraceContact(String validationCode){
        HttpHeaders headers = new HttpHeaders();
        //headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        Map<String, String> params = new HashMap<String, String>();
        ResponseEntity<User[]> ruser =
                this.restTemplate.getForEntity("http://localhost:" + port + "/api/contactTrace?validationCode="+validationCode,
                        User[].class, params);
        Assert.assertEquals(200, ruser.getStatusCode().value());
        return Arrays.asList(ruser.getBody());
    }

    private List<User> getDirectContact(String validationCode){
        HttpHeaders headers = new HttpHeaders();
        //headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        Map<String, String> params = new HashMap<String, String>();
        ResponseEntity<User[]> ruser =
                this.restTemplate.getForEntity("http://localhost:" + port + "/api/directContact?validationCode="+validationCode,
                        User[].class, params);
        Assert.assertEquals(200, ruser.getStatusCode().value());
        return Arrays.asList(ruser.getBody());
    }

    private void contact(String validationCode, String validationCodeHash){
        HttpHeaders headers = new HttpHeaders();
        //headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        Map<String, String> params = new HashMap<String, String>();
        ResponseEntity<User> ruser =
                this.restTemplate.postForEntity("http://localhost:" + port + "/api/contact?validationCode="+validationCode+
                        "&validationHash="+validationCodeHash,
                        entity,
                        User.class, params);
        Assert.assertEquals(200, ruser.getStatusCode().value());
    }


    private User register(String mobile){
        HttpHeaders headers = new HttpHeaders();
        //headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        Map<String, String> params = new HashMap<String, String>();
        ResponseEntity<User> ruser =
                this.restTemplate.postForEntity("http://localhost:" + port + "/api/register?mobile="+mobile,
                        entity,
                        User.class, params);
        Assert.assertEquals(200, ruser.getStatusCode().value());
        Assert.assertEquals(mobile, ruser.getBody().getMobile());
        return ruser.getBody();
    }

}

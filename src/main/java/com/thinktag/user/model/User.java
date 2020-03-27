package com.thinktag.user.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bouncycastle.util.encoders.Hex;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

@Entity
public class User implements Serializable{

    private static final long serialVersionUID = 2396501168768746670L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;
    @Column(unique=true)
    String mobile;
    @Column(unique=true)
    String validationCode;
    @Column(unique=true)
    String validationCodeHash;

    @ManyToMany
    @JoinTable(name="contact", joinColumns = @JoinColumn(name="id"),
            inverseJoinColumns = @JoinColumn(name="user_id"))
    @JsonIgnore
    List<User> users = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getValidationCode() {
        return validationCode;
    }

    public void setValidationCode(String validationCode) {
        this.validationCode = validationCode;
    }

    public String getValidationCodeHash() {
        return validationCodeHash;
    }

    public void setValidationCodeHash(String validationCodeHash) {
        this.validationCodeHash = validationCodeHash;
    }

    public String getHash(String code)throws Exception{
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(
                code.getBytes(StandardCharsets.UTF_8));
        return new String(Hex.encode(hash));
    }

    public void associate(User ...users){
        for(User u: users){
            if(!this.users.contains(u)) {
                this.users.add(u);
            }
        }
    }


    public List<User> getUsers() {
        return users;
    }
}

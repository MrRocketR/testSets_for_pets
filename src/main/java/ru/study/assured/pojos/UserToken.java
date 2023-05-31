package ru.study.assured.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserToken {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("token")
    private String token;

    public UserToken() {
    }


    public UserToken(int id, String token) {
        this.id = id;
        this.token = token;
    }

    public Integer getId() {
        return id;
    }

    public String getToken() {
        return token;
    }
}

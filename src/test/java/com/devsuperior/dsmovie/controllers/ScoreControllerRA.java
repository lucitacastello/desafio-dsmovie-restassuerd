package com.devsuperior.dsmovie.controllers;

import com.devsuperior.dsmovie.tests.TokenUtil;
import io.restassured.http.ContentType;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;

public class ScoreControllerRA {

    private Long existingMovieId, nonExistingMovieId;

    private String clientUsername, clientPassword, adminUsername, adminPassword;
    private String clientToken, adminToken, invalidToken;

    private Map<String, Object> putScoreInstance;

    @BeforeEach
    public void setUp() throws JSONException {

        baseURI = "http://localhost:8080";

        existingMovieId = 1L;
        nonExistingMovieId = 100L;

        clientUsername = "alex@gmail.com";
        clientPassword = "123456";
        adminUsername = "maria@gmail.com";
        adminPassword = "123456";

        clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
        adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);
        invalidToken = adminToken + "xpto";

        putScoreInstance = new HashMap<>();
        putScoreInstance.put("movieId", 1);
        putScoreInstance.put("score", 4);
    }

    @Test
    public void saveScoreShouldReturnNotFoundWhenMovieIdDoesNotExist() throws Exception {

        putScoreInstance.put("movieId", nonExistingMovieId);
        JSONObject newScore = new JSONObject(putScoreInstance);

        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(newScore)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/scores")
                .then()
                .statusCode(404);
    }

    @Test
    public void saveScoreShouldReturnUnprocessableEntityWhenMissingMovieId() throws Exception {

        putScoreInstance.put("movieId", null);
        JSONObject newScore = new JSONObject(putScoreInstance);

        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(newScore)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/scores")
                .then()
                .statusCode(422);
    }

    @Test
    public void saveScoreShouldReturnUnprocessableEntityWhenScoreIsLessThanZero() throws Exception {

        putScoreInstance.put("score", -4.0F);
        JSONObject newScore = new JSONObject(putScoreInstance);

        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(newScore)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/scores")
                .then()
                .statusCode(422);
    }

}


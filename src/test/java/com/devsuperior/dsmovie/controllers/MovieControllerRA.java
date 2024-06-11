package com.devsuperior.dsmovie.controllers;

import com.devsuperior.dsmovie.tests.TokenUtil;
import io.restassured.http.ContentType;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

public class MovieControllerRA {

    private String clientUsername, clientPassword, adminUsername, adminPassword;
    private String clientToken, adminToken, invalidToken;

    private String movieTitle;
    private Long existingMovieID, nonExistingMovieID;

    private Map<String, Object> postMoviesInstance;

    @BeforeEach
    public void setUp() throws JSONException {

        baseURI = "http://localhost:8080";

        clientUsername = "alex@gmail.com";
        clientPassword = "123456";
        adminUsername = "maria@gmail.com";
        adminPassword = "123456";

        clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
        adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);
        invalidToken = adminToken + "xpto";

        movieTitle = "Titanic";

        existingMovieID = 1L;
        nonExistingMovieID = 100L;

        postMoviesInstance = new HashMap<>();
        postMoviesInstance.put("title", "Test Movie");
        postMoviesInstance.put("score", 0.0F);
        postMoviesInstance.put("count", 0);
        postMoviesInstance.put("image", "https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg");
    }

    @Test
    public void findAllShouldReturnOkWhenMovieNoArgumentsGiven() {

        given()
                .get("/movies")
                .then()
                .statusCode(200);
    }

    @Test
    public void findAllShouldReturnPagedMoviesWhenMovieTitleParamIsNotEmpty() {

        given()
                .get("movies?title={movieTitle}", movieTitle)
                .then()
                .statusCode(200)
                .body("content.id[0]", is(7))
                .body("content.title[0]", equalTo("Titanic"))
                .body("content.score[0]", is(0.0F))
                .body("content.count[0]", is(0))
                .body("content.image[0]", equalTo("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/yDI6D5ZQh67YU4r2ms8qcSbAviZ.jpg"));
    }

    @Test
    public void findByIdShouldReturnMovieWhenIdExists() {

        given()
                .get("/movies/{id}", existingMovieID)
                .then()
                .statusCode(200)
                .body("id", is(1))
                .body("title", equalTo("The Witcher"))
                .body("score", is(4.5F))
                .body("count", is(2))
                .body("image", equalTo("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg"));
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() {

        given()
                .header("Content-Type", "application/json")
                //.header("Authorization", "Bearer " + adminToken)
                .accept(ContentType.JSON)
                .when()
                .get("/movies/{id}", nonExistingMovieID)
                .then()
                .statusCode(404)
                .body("error", equalTo("Recurso não encontrado"));
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndBlankTitle() throws JSONException {

        postMoviesInstance.put("title", "   ");
        JSONObject newMovie = new JSONObject(postMoviesInstance);

        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(newMovie)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/movies")
                .then()
                .statusCode(422);
    }

    @Test
    public void insertShouldReturnForbiddenWhenClientLogged() throws Exception {

		JSONObject newMovie = new JSONObject(postMoviesInstance);

		given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + clientToken)
				.body(newMovie)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.when()
				.post("/movies")
				.then()
				.statusCode(403);
	}

    @Test
    public void insertShouldReturnUnauthorizedWhenInvalidToken() throws Exception {

		JSONObject newMovie = new JSONObject(postMoviesInstance);

		given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + invalidToken)
				.body(newMovie)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.when()
				.post("/movies")
				.then()
				.statusCode(401);
    }
}

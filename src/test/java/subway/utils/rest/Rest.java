package subway.utils.rest;

import static io.restassured.RestAssured.*;

import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class Rest<T> {
	private final String uri;
	private T body;

	public ExtractableResponse<Response> post() {
		return given().log().all()
			.body(body)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().post(uri)
			.then().log().all()
			.statusCode(HttpStatus.CREATED.value())
			.extract();
	}

	public ExtractableResponse<Response> put() {
		return given().log().all()
			.body(body)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().put(uri)
			.then().log().all()
			.statusCode(HttpStatus.OK.value())
			.extract();
	}

	private ExtractableResponse<Response> delete() {
		return given().log().all()
			.when().delete(uri)
			.then().log().all()
			.statusCode(HttpStatus.NO_CONTENT.value())
			.extract();
	}

	private ExtractableResponse<Response> delete(HashMap<String, String> params) {
		return given().log().all()
			.when().params(params).delete(uri)
			.then().log().all()
			.statusCode(HttpStatus.NO_CONTENT.value())
			.extract();
	}

	private ExtractableResponse<Response> get() {
		return given().log().all()
			.when().get(uri)
			.then().log().all()
			.statusCode(HttpStatus.OK.value())
			.extract();
	}

	private ExtractableResponse<Response> get(HashMap<String, String> params) {
		return given().log().all()
			.when().params(params).get(uri)
			.then().log().all()
			.statusCode(HttpStatus.OK.value())
			.extract();
	}

	private Rest(String uri, T body) {
		this.uri = uri;
		this.body = body;
	}

	private Rest(String uri) {
		this.uri = uri;
	}

	public static <T> Builder<T> builder() {
		return new Builder<T>();
	}

	public static class Builder<T> {
		private String uri;

		Builder() {
		}

		public Builder<T> uri(String uri) {
			this.uri = uri;
			return this;
		}

		public Rest<T> body(T body) {
			return new Rest<T>(uri, body);
		}

		public ExtractableResponse<Response> get(String uri) {
			return new Rest<T>(uri).get();
		}

		public ExtractableResponse<Response> get(HashMap<String, String> params) {
			return new Rest<T>(uri).get(params);
		}

		public ExtractableResponse<Response> delete(String uri) {
			return new Rest<T>(uri).delete();
		}

		public ExtractableResponse<Response> delete(HashMap<String, String> params) {
			return new Rest<T>(uri).delete(params);
		}
	}
}

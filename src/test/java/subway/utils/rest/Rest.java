package subway.utils.rest;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

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

	private ExtractableResponse<Response> get() {
		return given().log().all()
			.when().get(uri)
			.then().log().all()
			.statusCode(HttpStatus.OK.value())
			.extract();
	}

	public void checkException(String errorCode, String errorMessage, HttpMethod method) {
		RequestSpecification requestSpecification = given().log().all()
			.body(body)
			.contentType(MediaType.APPLICATION_JSON_VALUE);

		switch (method) {
			case POST:
				Response post = requestSpecification.when().post(uri);
				check(post, errorCode, errorMessage);
				break;
			case DELETE:
				Response delete = requestSpecification.when().delete(uri);
				check(delete, errorCode, errorMessage);
				break;
			default:
				break;
		}
	}

	private void check(Response response, String errorCode, String errorMessage) {
		response.then().log().all()
			.body("errorCode", equalTo(errorCode))
			.body("errorMessage", equalTo(errorMessage));
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

		public ExtractableResponse<Response> delete(String uri) {
			return new Rest<T>(uri).delete();
		}
	}
}

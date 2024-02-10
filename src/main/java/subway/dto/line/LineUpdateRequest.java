package subway.dto.line;

public class LineUpdateRequest {
	private final String name;
	private final String color;

	public LineUpdateRequest(String name, String color) {
		this.name = name;
		this.color = color;
	}

	public static Builder builder() {
		return new Builder();
	}

	public String getName() {
		return name;
	}

	public String getColor() {
		return color;
	}

	public static class Builder {
		private String name;
		private String color;

		Builder() {
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder color(String color) {
			this.color = color;
			return this;
		}

		public LineUpdateRequest build() {
			return new LineUpdateRequest(name, color);
		}
	}
}

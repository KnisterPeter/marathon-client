package mesosphere.marathon.client;

import mesosphere.marathon.client.utils.MarathonException;
import mesosphere.marathon.client.utils.ModelUtils;
import feign.Feign;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Response;
import feign.codec.ErrorDecoder;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;

public class MarathonClient {
	static class MarathonHeadersInterceptor implements RequestInterceptor {
		@Override
		public void apply(RequestTemplate template) {
			template.header("Accept", "application/json");
			template.header("Content-Type", "application/json");
		}
	}
	
	static class MarathonErrorDecoder implements ErrorDecoder {
		@Override
		public Exception decode(String methodKey, Response response) {
			return new MarathonException(response.status(), response.toString());
		}
	}
	
	public static Marathon getInstance(String endpoint) {
		return getInstance(Feign.builder(), endpoint);
	}

	public static Marathon getInstance(Feign.Builder builder, String endpoint) {
		final GsonDecoder decoder = new GsonDecoder(ModelUtils.GSON);
		final GsonEncoder encoder = new GsonEncoder(ModelUtils.GSON);
		return builder
				.encoder(encoder).decoder(decoder)
				.errorDecoder(new MarathonErrorDecoder())
				.requestInterceptor(new MarathonHeadersInterceptor())
				.target(Marathon.class, endpoint);
	}
}

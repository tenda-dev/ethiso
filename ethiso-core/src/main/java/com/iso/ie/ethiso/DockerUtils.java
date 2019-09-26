package com.iso.ie.ethiso;

import static java.lang.System.getenv;

import java.net.URI;
import java.net.URISyntaxException;

public class DockerUtils {

	static final String LOCALHOST = "localhost";

	private static final String DOCKER_HOST = "DOCKER_HOST";

	private DockerUtils() {
		// Prevent instantiation
	}

	public static String getDockerHostName() {
		return getDockerHostName(getenv(DOCKER_HOST));
	}

	static String getDockerHostName(final String dockerHostVar) {
		String dockerHost = LOCALHOST;

		if (dockerHostVar != null) {
			URI dockerHostUri;
			try {
				dockerHostUri = new URI(dockerHostVar);

				dockerHost = dockerHostUri.getHost() != null ? dockerHostUri.getHost() : dockerHost;
			} catch (final URISyntaxException e) {
				// Do nothing, continue with localhost
			}
		}

		return dockerHost;
	}
}

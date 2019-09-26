package com.std.ie.ethiso;

import static com.std.ie.ethiso.DockerUtils.getDockerHostName;
import static org.junit.Assert.*;

import org.junit.Test;

public class DockerUtilsTest {

	@Test
	public void getDockerHostNameNull() {
		assertEquals(DockerUtils.LOCALHOST, getDockerHostName(null));
	}

	@Test
	public void getDockerHostNameUri() {
		assertEquals("192.168.99.100", getDockerHostName("tcp://192.168.99.100:2376"));
	}

	@Test
	public void getDockerHostNameBadUri() {
		assertEquals(DockerUtils.LOCALHOST, getDockerHostName("blah"));
	}

}
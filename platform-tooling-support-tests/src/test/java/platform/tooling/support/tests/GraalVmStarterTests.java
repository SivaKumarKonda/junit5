/*
 * Copyright 2015-2024 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * https://www.eclipse.org/legal/epl-v20.html
 */

package platform.tooling.support.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static platform.tooling.support.tests.XmlAssertions.verifyContainsExpectedStartedOpenTestReport;

import java.nio.file.Paths;
import java.time.Duration;

import de.sormuras.bartholdy.tool.GradleWrapper;

import com.gradle.develocity.testing.annotations.LocalOnly;

import org.junit.jupiter.api.Test;

import platform.tooling.support.MavenRepo;
import platform.tooling.support.Request;

/**
 * @since 1.9.1
 */
@LocalOnly(because = "GraalVM is not installed on Test Distribution agents")
class GraalVmStarterTests {

	@Test
	void runsTestsInNativeImage() {
		var request = Request.builder() //
				.setTool(new GradleWrapper(Paths.get(".."))) //
				.setProject("graalvm-starter") //
				.addArguments("-Dmaven.repo=" + MavenRepo.dir()) //
				.addArguments("javaToolchains", "nativeTest", "--no-daemon", "--stacktrace") //
				.addArguments("-Porg.gradle.java.installations.fromEnv=GRAALVM_HOME") //
				.setTimeout(Duration.ofMinutes(5)) //
				.build();

		var result = request.run();

		assertFalse(result.isTimedOut(), () -> "tool timed out: " + result);

		assumeFalse(
			result.getOutputLines("err").stream().anyMatch(
				line -> line.contains("No locally installed toolchains match")),
			"Abort test if GraalVM is not installed");

		assertEquals(0, result.getExitCode());
		assertTrue(result.getOutputLines("out").stream().anyMatch(line -> line.contains("BUILD SUCCESSFUL")));

		var testResultsDir = Request.WORKSPACE.resolve(request.getWorkspace()).resolve("build/test-results/test");
		verifyContainsExpectedStartedOpenTestReport(testResultsDir);
	}
}

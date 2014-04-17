package net.anthavio.phanbedded;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.anthavio.phanbedder.Phanbedder;

import org.fest.assertions.api.Assertions;
import org.junit.Test;

/**
 * 
 * @author martin.vanek
 *
 */
public class PhanbedderTest {

	@Test
	public void testJavaIoTmpDirectory() throws IOException {
		File binary = Phanbedder.unpack();
		assertProcessExecution(binary);

		String javaIoTmpdir = System.getProperty("java.io.tmpdir");
		Assertions.assertThat(binary.getParentFile()).isEqualTo(
				new File(javaIoTmpdir, "phantomjs-" + Phanbedder.PHANTOMJS_VERSION));
	}

	@Test
	public void testLocalTargetDirectory() throws IOException {
		String binary = Phanbedder.unpack("target/phanbedder-test/unpack");
		assertProcessExecution(new File(binary));

		String javaUserDir = System.getProperty("user.dir");
		Assertions.assertThat(new File(binary).getParentFile()).isEqualTo(
				new File(javaUserDir, "target/phanbedder-test/unpack"));
	}

	private void assertProcessExecution(File binary) throws IOException {
		Assertions.assertThat(binary).exists();
		Assertions.assertThat(binary).isFile();
		Assertions.assertThat(binary.canExecute()).isTrue();

		Process process = new ProcessBuilder(binary.getAbsolutePath(), "--version").start();
		String sysout = capture(process.getInputStream());
		String syserr = capture(process.getErrorStream());

		Assertions.assertThat(process.exitValue()).isEqualTo(0);
		Assertions.assertThat(syserr).isEmpty();
		Assertions.assertThat(sysout).isEqualTo(Phanbedder.PHANTOMJS_VERSION); //phantomjs binary version check!
	}

	private String capture(InputStream stream) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		String line = null;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		br.close();
		return sb.toString();
	}
}

package net.anthavio.phanbedded;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.anthavio.phanbedder.Phanbedder;

import org.fest.assertions.api.Assertions;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * 
 * @author martin.vanek
 *
 */
public class PhanbedderTest {

	@Test
	public void testJavaIoTmpDirectory() throws IOException {
		File binary = Phanbedder.unpack();

		String javaIoTmpdir = System.getProperty("java.io.tmpdir");
		File expectedDir = new File(javaIoTmpdir, "phantomjs-" + Phanbedder.PHANTOMJS_VERSION);
		Assertions.assertThat(binary.getParentFile()).isEqualTo(expectedDir);
		assertProcessExecution(binary);

		long lastModified = binary.lastModified();

		File binary2 = Phanbedder.unpack(); //existing file is returned
		Assertions.assertThat(binary2.lastModified()).isEqualTo(lastModified); // SAME
		assertProcessExecution(binary2);

		binary.delete(); //purge cached
		Assertions.assertThat(binary.exists()).isFalse();

		File binary3 = Phanbedder.unpack(); //new file must be unpacked
		Assertions.assertThat(binary3.lastModified()).isNotEqualTo(lastModified); //DIFF
		assertProcessExecution(binary3);
	}

	@Test
	public void testLocalTargetDirectory() throws IOException {
		String targetDir = "target/phanbedder-test/unpack";
		String binaryPath = Phanbedder.unpack(targetDir);
		File binary = new File(binaryPath);

		String javaUserDir = System.getProperty("user.dir");
		Assertions.assertThat(binary.getParentFile()).isEqualTo(new File(javaUserDir, targetDir));
		assertProcessExecution(binary);

		long lastModified = binary.lastModified();

		String binaryPath2 = Phanbedder.unpack(targetDir); //existing file is returned
		File binary2 = new File(binaryPath2);
		Assertions.assertThat(binary2.lastModified()).isEqualTo(lastModified); // SAME
		assertProcessExecution(binary2);

		binary.delete(); //purge cached
		Assertions.assertThat(binary.exists()).isFalse();

		String binaryPath3 = Phanbedder.unpack(targetDir); //new file must be unpacked
		File binary3 = new File(binaryPath3);
		Assertions.assertThat(binary3.lastModified()).isNotEqualTo(lastModified); //DIFF
		assertProcessExecution(binary3);
	}

	@Test
	public void testSeleniumGhostDriver() {

		File phantomjs = Phanbedder.unpack();
		DesiredCapabilities dcaps = new DesiredCapabilities();
		dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, phantomjs.getAbsolutePath());
		PhantomJSDriver driver = new PhantomJSDriver(dcaps);
		try {
			driver.get("https://www.google.com");
			WebElement query = driver.findElement(By.name("q"));
			query.sendKeys("Phanbedder");
			query.submit();

			Assertions.assertThat(driver.getTitle()).contains("Phanbedder");
		} finally {
			driver.quit();
		}

	}

	private void assertProcessExecution(File binary) throws IOException {
		Assertions.assertThat(binary).exists();
		Assertions.assertThat(binary).isFile();
		Assertions.assertThat(binary.canExecute()).isTrue();

		Process process = new ProcessBuilder(binary.getAbsolutePath(), "--version").start();
		String sysout = capture(process.getInputStream());
		String syserr = capture(process.getErrorStream());
		try {
			process.waitFor();
		} catch (InterruptedException ix) {
			System.out.println("Interrupted process.waitFor()");
		}
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

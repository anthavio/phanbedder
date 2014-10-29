/**
 * Copyright © 2014, Anthavio
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */
package net.anthavio.phanbedded;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.anthavio.phanbedder.Phanbedder;

import org.assertj.core.api.Assertions;
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

	@Test
	public void testJavaIoTmpDirectory() throws IOException, InterruptedException {
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
		Thread.sleep(1000); //1 second at least!

		File binary3 = Phanbedder.unpack(); //new file must be unpacked
		Assertions.assertThat(binary3.lastModified()).isNotEqualTo(lastModified); //DIFF
		assertProcessExecution(binary3);
	}

	@Test
	public void testLocalTargetDirectory() throws IOException, InterruptedException {
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
		Thread.sleep(1000); //1 second at least!

		String binaryPath3 = Phanbedder.unpack(targetDir); //new file must be unpacked
		File binary3 = new File(binaryPath3);
		Assertions.assertThat(binary3.lastModified()).isNotEqualTo(lastModified); //DIFF
		assertProcessExecution(binary3);
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

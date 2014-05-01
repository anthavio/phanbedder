package net.anthavio.phanbedder;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * @author martin.vanek
 *
 */
public class Phanbedder {

	public static final String PHANTOMJS_VERSION = "1.9.2";

	/**
	 * Unpack bundled phantomjs binary into ${java.io.tmpdir}/phantomjs-${phantomjs.version}/phantomjs
	 * 
	 * @return File of the unbundled phantomjs binary
	 */
	public static File unpack() {
		String javaIoTmpdir = System.getProperty("java.io.tmpdir");
		//multiple versions can coexist
		return unpack(new File(javaIoTmpdir, "phantomjs-" + PHANTOMJS_VERSION));
	}

	/**
	 * Unpack bundled phantomjs binary into specified directory
	 * 
	 * @param directory
	 * @return file path of the unbundled phantomjs binary
	 */
	public static String unpack(String directory) {
		File file = unpack(new File(directory));
		return file.getAbsolutePath();
	}

	/**
	 * Unpack bundled phantomjs binary into specified directory
	 * 
	 * @param directory
	 * @return File of the unbundled phantomjs binary
	 */
	public static File unpack(File directory) {
		if (!directory.exists()) {
			if (!directory.mkdirs()) {
				throw new IllegalArgumentException("Failed to make target directory: " + directory);
			}
		}

		File file;
		boolean chmodx;
		String osname = System.getProperty("os.name").toLowerCase();
		if (osname.contains("win")) {
			file = new File(directory, "phantomjs.exe");
			unpack("windows/phantomjs.exe", file);
			chmodx = false;
		} else if (osname.contains("mac os")) {
			file = new File(directory, "phantomjs");
			unpack("macosx/phantomjs", file);
			chmodx = true;

		} else if (osname.contains("linux")) {
			file = new File(directory, "phantomjs");
			//Linux has i386 or amd64
			String osarch = System.getProperty("os.arch");
			if (osarch.equals("i386")) {
				unpack("linux86/phantomjs", file);
			} else {
				unpack("linux64/phantomjs", file);
			}
			chmodx = true;

		} else {
			throw new IllegalArgumentException("Unsupported OS " + osname);
		}

		if (chmodx) {
			if (!file.setExecutable(true)) {
				throw new IllegalArgumentException("Failed to make executable " + file);
			}
		}

		return file;
	}

	private static void unpack(String resource, File target) {
		if (target.exists() && target.isFile() && target.canExecute()) {
			return; //keep existing
		}
		ClassLoader classLoader = Phanbedder.class.getClassLoader(); //same jarfile -> same classloader
		InputStream stream = classLoader.getResourceAsStream(resource);
		if (stream == null) {
			throw new IllegalStateException("Resource not found " + resource + " using ClassLoader " + classLoader);
		}
		BufferedInputStream input = new BufferedInputStream(stream);
		BufferedOutputStream output = null;
		try {
			output = new BufferedOutputStream(new FileOutputStream(target));
			while (input.available() > 0) {
				byte[] buffer = new byte[input.available()];
				input.read(buffer);
				output.write(buffer);
			}
			output.flush();

		} catch (Exception x) {
			throw new IllegalStateException("Failed to unpack resource: " + resource + " into: " + target, x);
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException iox) {
					//ignore
				}
			}
			try {
				input.close();
			} catch (IOException iox) {
				//ignore
			}
		}
	}

}

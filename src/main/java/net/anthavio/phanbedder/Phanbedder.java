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

    public static final String PHANTOMJS_VERSION = "2.0.0";

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

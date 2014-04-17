Phanbedder
==========

[![Build Status](https://anthavio.ci.cloudbees.com/buildStatus/icon?job=phanbedder-snapshot)](https://anthavio.ci.cloudbees.com/job/phanbedder-snapshot/)

PhantomJS Windows/Mac OS X/Linux native binary embedder

Selenium 2 has annoying habit of wanting full path to browser binary. For headless browsers that does not need installation and can be placed anywhere, this is simply unacceptable and action is unevitable.

Another obstacle is usualy different Operating System on developer machine (MacOS, Windows) and continuous integration server (Linux). Because PhantomJS is native library therefore every OS needs particular executable binary.

```java
//Phanbedder to the rescue!
		File phantomjs = Phanbedder.unpack();
		DesiredCapabilities dcaps = new DesiredCapabilities();
		dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, phantomjs.getAbsolutePath());
		PhantomJSDriver driver = new PhantomJSDriver(dcaps);
//Usual selenium stuff follows
		driver.get("https://www.google.com");
		WebElement query = driver.findElement(By.name("q"));
		query.sendKeys("Phanbedder");
		query.submit();

		Assertions.assertThat(driver.getTitle()).contains("Phanbedder - Google Search");
		driver.close();
```

Maven pom.xml dependencies

```xml
    <dependency>
      <groupId>net.anthavio</groupId>
      <artifactId>phanbedder</artifactId>
      <version>1.0.0-SNAPSHOT</version>
      <classifier>phantomjs-1.9.7</classifier>
    </dependency>
    
    <dependency>
      <groupId>com.github.detro.ghostdriver</groupId>
      <artifactId>phantomjsdriver</artifactId>
      <version>1.1.0</version>
    </dependency>
```

Maven pom.xml repository

```xml
    <repository>
        <id>sonatype-oss-public</id>
        <url>https://oss.sonatype.org/content/groups/public/</url>
        <releases>
            <enabled>true</enabled>
        </releases>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
```

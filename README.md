Phanbedder
==========

[![Build Status](https://anthavio.ci.cloudbees.com/buildStatus/icon?job=phanbedder-snapshot)](https://anthavio.ci.cloudbees.com/job/phanbedder-snapshot/)

PhantomJS Windows/Mac OS X/Linux native binary embedder

Tired of `java.lang.IllegalStateException: The path to the driver executable must be set by the phantomjs.binary.path capability/system property/PATH variable; for more information, see https://github.com/ariya/phantomjs/wiki. The latest version can be downloaded from http://phantomjs.org/download.html` ?!?

This library bundles PhantomJS binaries and unpacks right one for you on any of supported platforms - Linux, Windows and Mac OSX.

Simple as this...
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

Maven pom.xml dependencies. phanbedder-1.9.7 means that PhantomJS is bundeled inside

```xml
    <dependency>
      <groupId>net.anthavio</groupId>
      <artifactId>phanbedder-1.9.7</artifactId>
      <version>1.0.0</version>
    </dependency>
    
    <dependency>
      <groupId>com.github.detro.ghostdriver</groupId>
      <artifactId>phantomjsdriver</artifactId>
      <version>1.1.0</version>
    </dependency>
```

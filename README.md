Phanbedder [![Cloudbees DEV@cloud](http://www.cloudbees.com/sites/default/files/Button-Powered-by-CB.png)](http://www.cloudbees.com/)
==========
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.anthavio/phanbedder-2.1.1/badge.svg)](https://maven-badges.herokuapp.com/maven-central/net.anthavio/phanbedder-2.1.1)
[![Coverage Status](https://coveralls.io/repos/anthavio/phanbedder/badge.png?branch=master)](https://coveralls.io/r/anthavio/phanbedder?branch=master)

Jenkins [![Jenkins Build Status](https://vanek.ci.cloudbees.com/buildStatus/icon?job=phanbedder-snapshot)](https://vanek.ci.cloudbees.com/job/phanbedder-snapshot/)
Drone CI [![Drone CI Status](https://drone.io/github.com/anthavio/phanbedder/status.png)](https://drone.io/github.com/anthavio/phanbedder/latest)
Circle CI [![Circle CI Status](https://circleci.com/gh/anthavio/phanbedder.png?style=badge)](https://circleci.com/gh/anthavio/phanbedder)
Codeship [![Codeship Status](https://www.codeship.io/projects/073e30d0-ac64-0131-647d-5a52ac2c4589/status)](https://codeship.com/projects/19461)
Travis CI [![Build Status](https://travis-ci.org/anthavio/phanbedder.svg)](https://travis-ci.org/anthavio/phanbedder)


PhantomJS Windows/Linux/MacOSX native binary embedder

Tired of `java.lang.IllegalStateException: The path to the driver executable must be set by the phantomjs.binary.path capability/system property/PATH variable; for more information, see https://github.com/ariya/phantomjs/wiki. The latest version can be downloaded from http://phantomjs.org/download.html` when creating PhantomJSDriver instance?

This library bundles [PhantomJS](http://phantomjs.org/) binaries and unpacks right one for you on any of supported platforms - Linux, Windows and Mac OS X.

Simply with [Ghost Driver](https://github.com/detro/ghostdriver)
```java
	//Phanbedder to the rescue!
		File phantomjs = Phanbedder.unpack();
		DesiredCapabilities dcaps = new DesiredCapabilities();
		dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, phantomjs.getAbsolutePath());
		PhantomJSDriver driver = new PhantomJSDriver(dcaps);
	//Usual Selenium stuff...
		driver.get("https://www.google.com");
		WebElement query = driver.findElement(By.name("q"));
		query.sendKeys("Phanbedder");
		query.submit();

		Assertions.assertThat(driver.getTitle()).contains("Phanbedder - Google Search");
		driver.quit();
```

Add maven dependency or [download jar](http://search.maven.org/#artifactdetails|net.anthavio|phanbedder-2.1.1|1.0.0|jar). Number 2.1.1 stands for PhantomJS version bundled inside.

```xml
    <dependency>
      <groupId>net.anthavio</groupId>
      <artifactId>phanbedder-2.1.1</artifactId>
      <version>1.0.0</version>
    </dependency>
    
    <dependency>
      <groupId>com.github.detro.ghostdriver</groupId>
      <artifactId>phantomjsdriver</artifactId>
      <version>1.1.0</version>
    </dependency>
```
Previous stable version is 1.9.8
```xml
    <dependency>
      <groupId>net.anthavio</groupId>
      <artifactId>phanbedder-1.9.8</artifactId>
      <version>1.0.0</version>
    </dependency>
    
    <dependency>
      <groupId>com.github.detro.ghostdriver</groupId>
      <artifactId>phantomjsdriver</artifactId>
      <version>1.1.0</version>
    </dependency>
```

# EasyNavigation
底部导航栏
------------------------------------------------------------------------

<div align="center">
    <img src="https://github.com/FPhoenixCorneaE/EasyNavigation/blob/master/images/pic_preview.png" width="360" align="top"/>
</div>

How to include it in your project:
--------------
**Step 1.** Add the JitPack repository to your build file
```groovy
allprojects {
	repositories {
        google()
        mavenCentral()
		maven { url 'https://jitpack.io' }
	}
}
```

**Step 2.** Add the dependency
```groovy
dependencies {
	implementation 'com.github.FPhoenixCorneaE:EasyNavigation:$latest'
}
```

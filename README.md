needle-flow
==========

A data flow analyzer for (eventual) use with [needle](https://github.com/camsoupa/needle).

## CLONE THIS REPO

## GET JAVA

- java-8-oracle is best (other versions may work, but have not tested)

## GET ANT (TO BUILD ANDROID APPS)

- Run cmd: sudo apt-get install ant (or equivalent for your OS)

## GET THE ANDROID SDK

- Go to:    http://developer.android.com/sdk/index.html
- Click:    VIEW ALL DOWNLOADS AND SIZES
- Download: In the "SDK Tools Only" table, choose the distro for your OS

## ADD ANDROID EXECUTABLES TO PATH 

- Add <android-sdk>/tools & <android-sdk>/platform-tools to your path
- source your .bashrc (or equivalent) so that you can run android from command line

## GET ANDROID PLATFORM VERSIONS AND PLATFORM TOOLS USING SDK MANAGER

- Run cmd: android sdk (This will start the Android SDK Manager GUI.)
- Select:  the tools and platforms to download (All tools and platforms 15 & 19 should do.
- Click:   Install # packages

## GET ECLIPSE FOR JAVA (FOR SOOT DEVELOPEMENT)

- Go to: https://eclipse.org/downloads/packages/eclipse-ide-java-developers/lunasr1
- Download: On the right under "Download Links" choose the distro for your OS

## GET SOOT

- Go to: https://github.com/Sable/soot/wiki/Building-Soot-with-Eclipse
- Follow step 2+ to get soot+dependencies the fast way

## IMPORT needle-flow ECLIPSE PROJECT INTO YOUR ECLIPSE WORKSPACE

- In eclipse, choose File -> Import -> General -> Existing Project Into Workspace
- Browse to and select needle-flow directory and select OK.
- You should then see a needle-flow project in the "Projects" list (it should be checked).
- Ensure "Copy into Workspace" below the Projects list is unchecked.

## CHECK FOLDER STRUCTURE

Target folder structure:
```
android-sdk/
  tools/ 
	platforms/
    android-15/
    android-19/
    ...
  platform-tools/
  ...

needle-flow/ (this java project will produce a .jar file to be invoked from needle/server.js)
  ...
  
needle/
  server.js.
  ...
```

## CHECK ECLIPSE WORKSPACE

Your workspace should include these projects:
```
needle-flow/
heros/
jasmin/
soot/
```

## Run needle-flow from eclipse

- Create a run configuration by clickong on the small down arrow to the right green play arrow.
- Select "Run Configrations..." 
- Create a run configuration with arguments for: <path/to/android-platforms> <path/to/apk>



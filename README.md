# spotx-mopub-android
MoPub plugin for integrating with SpotXchange.



## Installing

There are three ways to install this plugin:


#### As a Gradle dependency

This is the preferred way. Simply add the following to your build.gradle.

```
compile 'com.spotxchange:spotx-mobpub-android:+'
```


#### As a Maven dependency

Declare the dependency in Maven:

```
<dependency>
    <groupId>com.github.satyan</groupId>
    <artifactId>spotx-mopub-android</artifactId>
    <version>1.0</version>
</dependency>
```

#### As a library project

Download the source code and import it as a library project in Android Studio or Eclipse. The project is available from our GitHub repository at [https://github.com/spotxmobile/spotx-mopub-android](https://github.com/spotxmobile/spotx-mopub-android).

For more information on how to do this, read [here](http://developer.android.com/tools/projects/index.html#LibraryProjects).

----------------

## Configuration

### SpotXchange

You'll need to apply to become a SpotX publisher if you haven't already.
You will receive a publisher channel ID and an account to log in the [SpotXchange Publisher Tools](https://publisher.spotxchange.com/)

### MoPub
  You'll need to create an account with MoPub if you haven't already. For more information on MoPub custom events, read http://mopub

You will use the custom data field to pass configuration parameters to the SpotXchange plugin. The custom data is a [JSON](http://json.org) object with the following keys:

* channel_id - Your SpotXchange  publisher channel ID
* playstore_url - URL to your app in the Google Play store.
* app_domain - Internet domain for your app's website
* iab_category - IAB category used to classify your app
* auto_init -
* prefetch -
* in\_app\_browser - If true, ad interactions will be displayed in an internal browser rather than the system default

For step by step instructions on how to specifiy parameters through the MoPub UI, read [here](https://dev.twitter.com/mopub/ad-networks).
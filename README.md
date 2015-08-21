##Who Can Use the Plugin

To use the plugin, you need to be a SpotXchange publisher and have an active account with MoPub.

### Become a SpotXchange Publisher

If you are not already a SpotXchange publisher, click [here](http://www.spotxchange.com/publishers/apply-to-become-a-spotx-publisher/) to apply.

### Create a MoPub Account

If you don't yet have a MoPub account, click [here](https://app.mopub.com/account/register/) to sign up.


## What the Plugin Does

The plugin allows the SpotX SDK and the MoPub SDK to communicate with each other seamlessly. To use this new plugin, SpotX publishers will need to integrate the SpotX SDK and the MoPub SDK into their App.


## How to Install the Plugin

There are three ways to install this plugin:

### Gradle Dependency (preferred)

Simply add the following to your build.gradle.

```groovy
compile 'com.spotxchange:spotx-mopub-android:+'
```

### Maven Dependency

Declare the dependency in Maven:

```xml
<dependency>
    <groupId>com.spotxchange</groupId>
    <artifactId>spotx-mopub-android</artifactId>
    <version>1.0</version>
</dependency>
```

### As a Library Project

Download the source code and import it as a library project in Android Studio or Eclipse. The project is available from our GitHub repository [here](https://github.com/spotxmobile/spotx-mopub-android).

Get more information on how to do this [here](http://developer.android.com/tools/projects/index.html#LibraryProjects).


## Configuration

Use the custom data field to pass configuration parameters to the SpotX plugin. Get more information on MoPub custom events [here](https://dev.twitter.com/mopub/ad-networks). The custom data is a [JSON](http://json.org) object with the following keys:

* channel_id - Your SpotXchange  publisher channel ID
* playstore_url - URL to your app in the Google Play store.
* app_domain - Internet domain for your app's website
* iab_category - IAB category used to classify your app
* in\_app\_browser - If true, ad interactions will be displayed in an internal browser rather than the system default

Get step-by-step instructions on how to specify parameters through the MoPub UI [here](https://dev.twitter.com/mopub/ad-networks).

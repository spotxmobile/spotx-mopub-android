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
compile 'com.spotxchange:spotx-mobpub-android:+'
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

## Rewarded Video

Before integrating the SpotX custom event class library for MoPub reewarded video, please review the [Rewarded Video for Android Documentation](https://github.com/mopub/mopub-android-sdk/wiki/Rewarded-Video-Integration).

Follow the instructions in the Basic Integration section of the documentation.  If you have already installed the SpotX-MoPub-Plugin, you will have completed step one of the integration process.

### Rewarded Video - SpotX Mediation Settings

Mediation settings may be used to pass additional configuration parameters to the SpotX network during the rwarded video load call. The scurrent SpotX mediation settings class contains the following function, where channel_id is your SpotX publisher channel ID.:

```java
withChannelId(String channel_id)
```

The following code snipit demonstrates how to initialize and load a rewarded video:

```java
// Initialize the rewarded video

MoPub.initializeRewardedVideo(this);
MoPub.onCreate(this);

// Optionally, create and set the rewarded video listener

MoPubRewardedVideoListener rewardedVideoListener = new MoPubRewardedVideoListener(){...};
MoPub.setRewardedVideoListener(rewardedVideoListener);

// Create the SpotxMediationSettings object
SpotxMediationSettings sptxMediationSettings = new SpotxMediationSettings
	.Builder()
    .withChannelId(MY_SPOTX_CHANNEL_ID)
    .build();

// Load the rewarded video
MoPub.loadRewardedVideo(MY_MOPUB_AD_UNIT_ID, sptxMediationSettings);
```
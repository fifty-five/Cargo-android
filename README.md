# Cargo-ios

![Build Status](https://travis-ci.org/fifty-five/Cargo-android.svg?branch=master)


## Mission
Cargo is a tool developed by [fifty-five](http://fifty-five.com).
It allows to integrate third party SDK in Google Tag Manager.

#### Current supported SDK
- Tune - Attribution Analytics


#### Google Tag Manager
Google Tag Manager enables developers to change configuration values in their mobile applications using the Google Tag Manager interface without having to rebuild and resubmit application binaries to app marketplaces.



## Install Cargo in your iOS app

##### 1) add to your app Cargo and Google Tag Manager.
It is very easy if you are using Cocoapods.
Simply add this pod :
```
pod 'Cargo', :git => 'https://github.com/fifty-five/Cargo-ios.git'
```

By default it will add all supported handlers to your application.
To install only Facebook for instance,
```
pod 'Cargo/Facebook', :git => 'https://github.com/fifty-five/Cargo-ios.git'
```

##### 2) GTM set up
You can follow GTM set up [there](https://developers.google.com/tag-manager/ios/v3/

##### 3) Cargo set up
Once GTM is installed, Cargo set up is one line.
```
[[Cargo sharedHelper] initTagHandlerWithManager:self.tagManager
                                                  container:self.container];
[[Cargo sharedHelper] setLaunchOptions:launchOptions];
[[Cargo sharedHelper] registerHandlers];
```

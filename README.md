# FamilyTracker
<p align="left">
[![Maintenance](https://img.shields.io/badge/Maintained%3F-yes-green.svg)](https://github.com/RegNex/FamilyTracker/graphs/commit-activity)

[![made-with-Java](https://img.shields.io/badge/Made%20with-Java-1f425f.svg)](https://www.java.com)

[![made-with-Java](https://img.shields.io/badge/Made%20with-Android-1f425f.svg)](https://www.android.com/)

[![ForTheBadge built-with-love](http://ForTheBadge.com/images/badges/built-with-love.svg)](https://github.com/RegNex/)
</p>

Udacity Android Developer Nanodegree Capstone 2 project. A simple App to track family and friends


## Screenshots
<img align="left" src="https://github.com/RegNex/FamilyTracker/blob/master/screenshot/1.png" width="200" height="400"/>
<img src="https://github.com/RegNex/FamilyTracker/blob/master/screenshot/2.png" width="200" height="400"/>

## Getting Started

To clone this project,

open your terminal or cmd

```
cd folder/to/clone-into/
```

```
git clone https://github.com/RegNex/FamilyTracker.git
```

Then 
locate the project on your system and open with android studio


## Prerequisites

What things you need to install the software and how to install them

```
* Android Studio
* Java JDK 8+
* Android SDK
* Firebase Account
```

## Keystore Configuration
Create a file "keystore.properties" in app/ folder of project and add & edit the following code to suit your details
```
storePassword=myStorePassword //your store password
keyPassword=mykeyPassword //your keystore password
keyAlias=myKeyAlias //your keystore alias
storeFile=/Documents/uploadKeys/keystore.jks //path to your keystore
```

## Helliomessaging sms service(optional)
You can create an account with [Helliomessaging](https://helliomessaging.com/) to get their API client id and auth key.
Add the Client Id and App secret to your grade.properties

```
SMS_CLIENT_ID="xxxxxxxxxxxxx"
SMS_APP_SECRET="xxxxxxxxxxxxxxxxxxxxxxx"
```

## Mapbox API
Get [Mapbox API](https://account.mapbox.com/auth/signup/) for free and add it your string.xml under res/values

```
<string name="mapbox_api" translatable="false">YOUR-PRIVATE-KEY-HERE</string>
```


## How to contribute
Contributing to XYZ Reader App is pretty straight forward! Fork the project, clone your fork and start coding!



## To set up an emulator
* Select Run > Run 'app'
* Click 'Create New Emulator'
* Select the device you would like to emulate 
* Select the API level you would like to run - click 'Download' if not available
* Select configuration settings for emulator
* Click 'Finish' and allow Emulator to run

## To Run on an Android OS Device
* Connect the device to the computer through its USB port
* Make sure USB debugging is enabled (this may pop up in a window when you connect the device or it may need to be checked in the phone's settings)
* Select Run > Run 'app'
* Select the device (If it does not show, USB debugging is probably not enabled)
* Click 'OK'

## Built With

* [Android Studio](https://developer.android.com/studio/install) - How to install Android Studio
* [Firebase](https://firebase.google.com) - Get started with Firebase


## Author

* **Sunu Bright Etornam** 


## License

Copywrite 2018 Sunu Bright Etornam

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


## Acknowledgments

* Hat tip to anyone whose code was used
* Inspiration
* etc

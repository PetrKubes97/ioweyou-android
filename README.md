# Android client for the IOweYou app

Android client for the [IOweYou app](https://petrkubes.cz/ioweyou/). The source code of the backend can be found [here](https://github.com/PetrKubes97/ioweyou-server).

## Installation

1. Clone the github repository.
```
git clone git@github.com:PetrKubes97/ioweyou-android.git
```

2. Import it into Android Studio.

3. You may want to change facebook_app_id and fb_login_protocol_scheme in app/src/main/res/values/strings.

4. Set the URL of your backend server in app/src/main/java/cz/petrkubes/ioweyou/Tools/Const.java

5. Build and run the app

## Libraries

* [Android support library](https://developer.android.com/topic/libraries/support-library/index.html)
* [Facebook Android SDK](https://developers.facebook.com/docs/android/)
* [Stetho](http://facebook.github.io/stetho/)
* [Parceler](https://github.com/johncarl81/parceler)
* [Android Betterpickers](https://github.com/code-troopers/android-betterpickers)

### License

MIT

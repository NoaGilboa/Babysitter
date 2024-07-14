
# Babysitter App

The Babysitter app connects parents with babysitters, facilitating the management of babysitting events sourced from the Kinderkit app. Both applications run on the same server and share the same database, ensuring seamless integration and data consistency.

## Table of Contents
1. [Introduction](#introduction)
2. [Features](#features)
3. [Installation](#installation)
4. [Configuration](#configuration)
5. [Usage](#usage)
6. [API Integration](#api-integration)
7. [Permissions](#permissions)
8. [Screenshots](#screenshots)
9. [Video Demonstration](#video-demonstration)

## Introduction

The Babysitter app is designed to help parents find and manage babysitters. It integrates with the Kinderkit app to pull babysitting events created by parents. The app uses Firebase for authentication and real-time data storage, and Google Maps for location services.

## Features

- User Registration and Login for both Babysitters and Parents
- Profile Management
- Real-time Chat between Parents and Babysitters
- Search Filters based on Location, Experience, and Hourly Wage
- Integration with Kinderkit for managing Babysitting Events

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/NoaGilboa/Babysitter.git
   ```
2. Open the project in Android Studio.
3. Ensure you have the necessary SDKs and dependencies installed.
4. Connect your Firebase project to the app.

## Configuration

Add your Firebase configuration file (`google-services.json`) to the `app` directory.

Update the `network_security_config.xml` file in the `res/xml` directory for your network security configurations.

## Usage

1. Run the app on an Android device or emulator.
2. Register as a Babysitter or Parent.
3. Login to your account.
4. Parents can create and manage babysitting events through the Kinderkit app.
5. Babysitters can view available babysitting events and apply for them.

## API Integration

The Babysitter app connects to the server using Retrofit for API calls. The shared server hosts both the Babysitter and Kinderkit apps, ensuring data consistency.

### RetrofitClient.java

```java
public class RetrofitClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient(String baseUrl) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
```

### UserService.java

```java
public interface UserService {
    @GET("users/{id}")
    Call<User> getUserById(@Path("id") String id);
}
```

### BabysitterService.java

```java
public interface BabysitterService {
    @GET("babysitters")
    Call<List<Babysitter>> loadAllBabysitters();
}
```

### EventService.java

```java
public interface EventService {
    @GET("events")
    Call<List<BabysittingEvent>> loadAllEvents();
}
```

## Permissions

The app requires the following permissions, as specified in the `AndroidManifest.xml` file:

```xml
<uses-permission android:name="android.permission.VIBRATE" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
```

## Screenshots

Here are some screenshots and mockups of the application:

![Mockup 1](https://github.com/NoaGilboa/Babysitter1/assets/143444119/63d8b860-8fae-44d5-9f82-d12658c44656)
![Mockup 2](https://github.com/NoaGilboa/Babysitter1/assets/143444119/3defa6c2-09f0-46cc-bdb7-c4942fac862d)
![Mockup 3](https://github.com/NoaGilboa/Babysitter1/assets/143444119/f8398cd4-b8bf-4770-99d6-575136cb3555)
![Mockup 4](https://github.com/NoaGilboa/Babysitter1/assets/143444119/2bc31e99-5eb2-4540-a4ce-cdfd86215e19)
![Mockup 5](https://github.com/NoaGilboa/Babysitter1/assets/143444119/5d82da94-307b-4f27-98ad-4a3b30358abd)
![Mockup 6](https://github.com/NoaGilboa/Babysitter1/assets/143444119/3ae54dd9-4746-4e5e-bbf5-6b783fdab7ee)
![Mockup 7](https://github.com/NoaGilboa/Babysitter1/assets/143444119/02b6dc8a-bd07-43d2-8997-2b12cea7854f)

## Video Demonstration

Watch a video demonstration of the Babysitter app:

[![Watch the video](https://github.com/NoaGilboa/Babysitter1/assets/143444119/f4b01abc-a3ae-4e6d-af1d-ff4e4f75433f)](https://github.com/NoaGilboa/Babysitter1/assets/143444119/f4b01abc-a3ae-4e6d-af1d-ff4e4f75433f)

---



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

<img src="https://github.com/user-attachments/assets/e57a0564-0f80-4c5e-b500-6c9a138b7079" alt="WhatsApp Image 2024-07-16 at 01 55 24_0e0ee1ed" width="300" height="500">

<img src="https://github.com/user-attachments/assets/25d1d49c-7def-407e-b4a4-e8a17a624993" alt="WhatsApp Image 2024-07-16 at 01 56 05_b286a038" width="300" height="500">

<img src="https://github.com/user-attachments/assets/35ffc367-dc05-4f9a-91e3-35d74c161941" alt="WhatsApp Image 2024-07-16 at 01 55 51_56e47054" width="300" height="500">

## Video Demonstration

Watch a video demonstration of the Babysitter app:

https://github.com/user-attachments/assets/c49040e7-6e0b-4dd6-a3bd-b8d97d325eb6



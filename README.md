# Babysitter App

## Overview
The Babysitter app is designed to connect parents with babysitters. This app allows parents to find babysitters based on various criteria like location, experience, and hourly wage. Babysitters can register and offer their services, setting up profiles that include their details and preferences.

## Features
- **User Registration and Login**: Separate registration processes for babysitters and parents. Users can log in to their accounts to access their profiles and settings.
- **Profile Management**: Users can update their profiles, including contact information, services offered, and availability.
- **Search and Filter**: Parents can search for babysitters using filters like location, experience, and hourly wage.
- **Real-time Chat**: Once connected, parents and babysitters can communicate through a built-in messaging system.
- **Location Services**: Integration with Google Maps to help users find babysitters near their location.

## Technical Details
- **Platform**: Android
- **Languages**: Java
- **Database**: Firebase Realtime Database for storing user data and chat messages.
- **Authentication**: Firebase Authentication is used for handling user authentication.
- **APIs**: Google Maps API for location services.

## Setup and Configuration

### 1. Firebase Setup:
1. Create a Firebase project in the Firebase console.
2. Enable Firebase Authentication and configure it to use email and password authentication.
3. Set up Firebase Realtime Database with read/write permissions for authenticated users.

### 2. Google Maps Setup:
1. Enable Google Maps API in the Google Cloud Console.
2. Obtain and configure an API key in your Android project to use Google Maps features.

### 3. Android Project Configuration:
1. Import the project into Android Studio.
2. Configure `build.gradle` with dependencies for Firebase, Google Maps, and other required libraries.
3. Ensure the Android manifest includes permissions for internet and location services.

### 4. Running the Application:
1. Build the application in Android Studio and run it on an emulator or a physical device.
2. Register as a new user (either as a parent or a babysitter) and explore the features.

## Common Issues and Troubleshooting
- **Firebase Database Rules**: If read/write operations fail, ensure that Firebase Database rules are set correctly to allow access to authenticated users.
- **API Key Restrictions**: If Google Maps features do not work, verify that the API key is correctly set up in the Google Cloud Console and is not restricted.
- **Authentication Errors**: Ensure that Firebase Authentication is properly set up in the Firebase console, and the app’s configuration matches the settings.

## Future Enhancements
- **Advanced Filtering Options**: Implement more advanced filters for searching babysitters based on additional criteria like certifications or special needs experience.
- **Scheduling and Calendar Integration**: Add a feature to schedule and manage appointments directly through the app, including integration with external calendars.
- **Ratings and Reviews**: Users can rate and review babysitters after availing their services.
- **Payment Integration**: Integrate a payment gateway to handle transactions directly through the app, providing a seamless experience for booking and payments.

## Mockups
Here are some screenshots and mockups of the application:
![image](https://github.com/NoaGilboa/24A10357-Noa-Gilboa-final-project---B-/assets/143444119/969b8adc-14f0-4297-97ac-7a349e471de9) 
![image](https://github.com/NoaGilboa/24A10357-Noa-Gilboa-final-project---B-/assets/143444119/82dd3b72-0ac4-4e11-b5d7-d271c802b7b4)
![image](https://github.com/NoaGilboa/24A10357-Noa-Gilboa-final-project---B-/assets/143444119/982dccb1-80cd-4d9b-a4d8-f4d51f4e7b38)
![image](https://github.com/NoaGilboa/24A10357-Noa-Gilboa-final-project---B-/assets/143444119/041b4a44-907e-4161-b9eb-28a649c4b0ee)
![image](https://github.com/NoaGilboa/24A10357-Noa-Gilboa-final-project---B-/assets/143444119/5d9a7716-7e95-4b5d-ba12-e57a120bb897)
![image](https://github.com/NoaGilboa/24A10357-Noa-Gilboa-final-project---B-/assets/143444119/6dcd9137-351e-49a2-9f85-527b444ec1b9)
![image](https://github.com/NoaGilboa/24A10357-Noa-Gilboa-final-project---B-/assets/143444119/eef154db-530c-4977-ae57-e68ba91c097a)

## Video Demonstration
Watch a video demonstration of the Babysitter app: 

https://github.com/NoaGilboa/24A10357-Noa-Gilboa-final-project---B-/assets/143444119/b62bb41f-61c7-4dbe-bd84-d832b8ca2125



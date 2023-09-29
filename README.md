# Driving-Behavior
My Thesis for University Of Piraeus

Android app built with Android Studio in Java. Utilizing Android Studio’s features such as the location tracker and accelerometer, it gathers data
such as average/top speed, sudden accelerations/ breaks, distancecovered et al. The results after each session are added to the user’s profile
where he can view his statistics and compare them with other drivers of the application. Also, by calling multiple different Google Maps SDK APIs,
it implements several GPS related features such as tracking the user’s location, finding the optimal route to a destination, driving time
estimations to a destination in real time and more. The UI is constructed with Android Studio using multiple different
layouts and other features. The backend is built with Spring Boot which manages all the HTTP Requests to the different API’s used in the app as
well as retrieving/storing user data. User information is persisted to PostgreSQL Database using Spring Data JPA.

Spring Boot Version 2.6.0, Minimum SDK Version 21, Maximum SDK Version 31

# AndroidAssistantHelper

Android Assisstant Helper

The Android Assistant Helper is an Android application that was created 
to play around with Android and Dialog Flow web API. 
The Android Assistant Application takes advantage of the 
Android Text to Speech API (TTS), Android Speech Recognizer, DialogFlow, 
Spotify Web/Android API, and various google API for maps.

Android Assistant takes advantage of Android Speech Recognizer to parse 
text to a String and send that speech to the Dialog Flow Web Service Endpoint. 
The end point will map speech input to an Intent / Action and in conjunction 
with Android Assistant Helper prompt the user for all necessary parameters with TTS.

After an activity has been mapped and parameters have been gathered Android Assistant Helper 
uses Intent / Action and Parameters to execute business logic of the Action within Android Assistant business Classes.

Android Assistant is built on an N Tier design pattern with Domain , Service, and Activity (View) components. 
Room is used for the ORM, and Dagger2 is used for dependency injection. 

Android SDK 26 was used to develop the application, Spring Rest Template was used for various Web Service Calls, 
and ai.api was used to simplify some of the Dialog Flow web service calls.

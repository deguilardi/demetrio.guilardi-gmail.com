# truckie 
This is an academic project developed for the discipline "Programmation de Plateformes Mobiles" conducted by Prof. Bob Menelas at University of Quebec at Chicoutimi, Canada.
<br />
![intro](/github_assets/intro.gif)
  
### About the app
Truckie is an app that connects people who needs to transport things with people who own vehicles eligible to transport these things.
Within this app, one can request a delivery, by just setting up the origin, destination, dates and times.
Drivers can consult the delivery list and make an offer for deliveries he can transport, other drivers can also make an offer.
Finnaly, the user who requested the delivery will accept the best offer. 

### Setting up and running
For academic purposes, this app was made in JAVA, not Kotlin.
You will need Android Studio, a Firebase Real Time Database and a Google Maps API key.

#### Firebase
You can use our Firebase Database =D. If you want to set up your own, just follow this [link](https://developer.android.com/studio/write/firebase.html) to generate your own database;

#### Google Maps
* Get an [API key](https://developers.google.com/maps/documentation/javascript/get-api-key).
* Create a api key entry in any `gradle.properties` file (we recommend ~/.gradle/gradle.properties file).
`
GOOGLE_MAPS_API_KEY=YOUR_API_KEY_HERE
`

#### Running
After setting up, import the project on Android Studio, select a device or emulator and hit the play button.

## Team
* GUID09058608 Guilardi, Demetrio
* FERR02088908 Barbosa, Raphael

## Screenshots
| | |
| --- | --- |
| ![screenshot 1](/github_assets/screen_details.png) | ![screenshot 2](/github_assets/screen_request.png)   |
| | |
| ![screenshot 3](/github_assets/screen_main.png)    | ![screenshot 4](/github_assets/screen_main_dark.png) |

## Functionalities

### User login
* Fill up the login form with your email and password;
* Then click on the Login button to access your account.

### Create account
Click on the link underneath the Login button on the application login screen to create a new account.

* Fill up the login form with your email and password;
* The password field must be typed twice;
* Hit the "next" button keep going.
* On the second page, enter your personal information: Full name, phone numbers and drivers license. User hits the "next" button to go to the third page.
* On the third page, enter your full address, with ZIP code. User hits the next button to go to the fourth and last page.
* On the last page, register your vehicle with the make, model, year, type, size and cargo capacity.


### Request delivery
* To request a new delivery, you hit the "Request new Delivery" button on the mains activity.
* You then select the pickup location, by searching an address on Google maps platform.
* Select one of the results and goes to the next screen.
* The second screen is where the you must inform the pickup date, time and any other further information, such as “the key is under the mat”.
* The third screen is pretty similar to the first one of this case. You must select an address for the delivery.
* The last screen is also a details information, but this time, you must fill in with the delivery details, such as “the gate’s pass code is #123”.

### Track my deliveries
* On the main activity, you can view a list of deliveries, select a delivery and view details.


### View delivery auction
* On the main activity, you can view a list of auctions with basic status
* Select a delivery to see the details;
* If you are the requester or the auction winner, more specific details must be shown, such as specific address and further delivery instructions.
* If you aren't the auction winner or the requester, a radius of the address must be shown in the map. Instead of the exact address, just the neighborhood is displayed. And the delivery instructions are hidden.

### Bid on a delivery request
* To bid in a delivery auction, you must select one of the deliveries from the auction list;
* then click on Bid button, enter a bid value and click on Bid button to confirm.


### Accept bid
* To accept a bid, you select one of your own delivery requests on the auction list;
* Then hit the bid button and on the confirm button to accept.


### Track my bids
* A list of made by you is shown to you.
* You can track your bids. The ones you lost and the ones he won.


### Night Mode
* You can switch the night mode on and off. By doing that, a dark theme must be applied or disabled.

### Truckie Widget
* On the Home screen, you can add a widget with list of your deliveries on truckie.

# Cargo-ios

![Build Status](https://travis-ci.org/fifty-five/Cargo-android.svg?branch=master)


## Mission
Cargo is a tool developed by [fifty-five](http://fifty-five.com).
It allows to integrate third party SDK in Google Tag Manager.

#### Current supported SDK
- Tune - Attribution Analytics


#### Google Tag Manager
Google Tag Manager enables developers to change configuration values in their mobile applications using the Google Tag Manager interface without having to rebuild and resubmit application binaries to app marketplaces.

#### Tracker DataLayer

| Name            | Definittion   |
|----------       |:-------------|
| **Tracker DataLayer** |
| enableDebug     |  Enable debug mode for your tracker |
| enableOptOut    |  Opt-out tracking for a specific customer |
| disableTracking |  Disable all tracking |
| dispatchPeriod   |  Define an interval of time to dispatch hits |
| **Screen DataLayer** |
| screenName     |  Name of the screen |
| **Event DataLayer** |
| eventName     |  Name of the event |
| **User DataLayer** |
| userGoogleId     |  Google Id of the user |
| userFacebookId     |  Facebook Id of the user |
| userId     |  CRM Id of the user |
| **Transaction DataLayer** |
| transactionId       |  A unique Id of the transaction |
| transactionTotal    |  Total amount of the transaction |
| transactionProducts |  An array of products in the transaction |
| **Product DataLayer** |
| name                |  Name of the product |
| sku                 | Sku of the product |
| price               |  Price of the product |
| category            |  Category of the product |
| quantity            |  Quantity of the product |

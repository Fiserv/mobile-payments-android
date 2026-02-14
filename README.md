# Overview
The MobilePayments SDK is a library containing a suite of payment tools designed to be simple and easy to integrate into an existing, or new, mobile application.  It is a collection of easy to use UI widgets that can support varying levels of integration, everything from an isolated full-screen takeover to individual widgets that can be embedded into an existing UI, with extensive customization to fit nearly any look and feel.

In addition, for those looking for a truly custom experience, the full underlying Payments API is available to be plugged directly into a custom-built UI.

<div class="container" style="width:100%; display: flex; justify-content: space-evenly;" align="center">
	<img src="/images/sheet.png" alt="Sheet" style="width:25%; height:auto;">&nbsp;&nbsp;<img src="/images/singlecardmode.png" alt="Single Card Mode" style="width:25%; height:auto;">&nbsp;&nbsp;<img src="/images/uicomponents.png" alt="UI Components" style="width:25%; height:auto;">
</div>


# Getting Started

## Prerequisites
To use the MobilePayments SDK on Android, your app must have

1. a `minSdkVersion` of `26` or higher
2. a `compileSdkVersion` of `36` or higher
	
In addition, you must set up a merchant account and associated payment configurations with CardFree

## Installation
To install on Android, first ensure you have added the maven repository to your project.  To do so, you must ensure the following is in your project’s settings.gradle.kts file

```
dependencyResolutionManagement {
	repositories {
		google()
		mavenCentral()
	}
}
```

Next you need to add the dependency to your module’s Gradle build file (app/build.gradle.kts):

```
dependencies {
   implementation("com.cardfree.fiserv.payments:mobile-payments-sdk:1.0.1")
}
```

Once added, simply sync your gradle files and the MobilePayments SDK will be available in your code, and you are ready.

# Initialization
Initializing the MobilePayments SDK is done on app start, in the Application.onCreate method.  Here, you will provide:
  1. a reference to the host Application
  2. the Environment (SANDBOX or PRODUCTION) you wish to run the SDK on
  3. the clientToken provided when your merchant account was configured
  4. **(OPTIONAL)** the ID of a store within your merchant, that will assign transactions and payments made to that store

The invocation will look like this:
```
MobilePayments.initialize(
  application = this,
  environment = Environment.SANDBOX,
  clientToken = "<clientToken>",
  businessLocationId = "<businessLocationId>",
)
```

## Optional Parameters
In addition, you are able to configure certain optional parameters to control and influence the behavior of the MobilePayments SDK.  These are:

  - A user ID value (typically an account ID or otherwise unique identifier for a customer).  This will allow a customer with this ID value to access previously saved Credit Cards for use in future payments.
    ```
    MobilePayments.setUserId("<USER_ID>")
    ```
    
  * The ID of a store within your merchant, this is the same value that can be passed into inititalize.  This will direct payments to that location specifically as well as ensure payment configurations are accurate to the specific store in the event of different configurations within the same merchant.
    ```
    MobilePayments.setBusinessLocationId("<STORE_ID>"
    ```

  + A boolean to control GooglePay access in the Sheets integration
    ```
    MobilePayments.setGooglePayEnabled(true)
    ```

# Integration
The first step to integrating the MobilePayments SDK is determining what method of integration is right for you.

The simplest and easiest of the available options is Sheets. This is as quick and easy as firing off an intent and monitoring the response.  This is by far the easiest path to go, but simultaneously the least flexible.

If you require more flexibility, but don’t want to deal with customer information directly, or just want a personalized touch to the user experience, then using the MobilePayments UI Components is the way to go.

If even that’s not enough, and you really must have a unique UI, then you’re looking to interface with MobilePayments Directly.  This will take the most work, and you will have to collect user information to pass to MobilePayments, but you will be able to make your app look and behave exactly the way you want to.

**Quick Links**

- [Sheets](/documentation/sheets.md)
* [UI Components](/documentation/ui_components.md)
+ [Sheets](/documentation/direct.md)

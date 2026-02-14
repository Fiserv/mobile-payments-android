# Overview
The MobilePayments SDK is a library containing a suite of payment tools designed to be simple and easy to integrate into an existing, or new, mobile application.  It is a collection of easy to use UI widgets that can support varying levels of integration, everything from an isolated full-screen takeover to individual widgets that can be embedded into an existing UI, with extensive customization to fit nearly any look and feel.

In addition, for those looking for a truly custom experience, the full underlying Payments API is available to be plugged directly into a custom-built UI.

<div class="container" style="display: flex; justify-content: space-evenly;">
	<img src="/images/sheet.png" alt="Sheet" style="width:25%; height:auto;">	<img src="/images/singlecardmode.png" alt="Single Card Mode" style="width:25%; height:auto;">	<img src="/images/uicomponents.png" alt="UI Components" style="width:25%; height:auto;">
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


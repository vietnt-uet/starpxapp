# Starpx Assignment

This is assignment project using:
- AWS Cognito
- Apollo GraphQL
- Jetpack Compose
- Android Provider

## Installation
Clone this repository and import into **Android Studio**
```bash
git clone git@github.com:vietnt-uet/starpxapp.git
```

## Configuration
### Keystores:
Update `app/keystore.gradle` with the following info:
```gradle
ext.key_alias='...'
ext.key_password='...'
ext.store_password='...'
```
And place keystore under `app/keystores/` directory:
- `starpx.keystore`

### Config endpoint and API key:
Create `local.properties` with the following info:
```
api_endpoint="https://yourapiendpoint.com"
api_key="YOUR_API_KEY"
```

## Generating signed APK
From Android Studio:
1. ***Build*** menu
2. ***Generate Signed APK...***
3. Fill in the keystore information *(you only need to do this once manually and then let Android Studio remember it)*

## Maintainers
This project is mantained by:
* [Viet Nguyen](https://github.com/vietnt-uet)
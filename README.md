![moko-permissions](img/logo.png)  
[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0) [![Download](https://img.shields.io/maven-central/v/dev.icerock.moko/permissions) ](https://repo1.maven.org/maven2/dev/icerock/moko/permissions) ![kotlin-version](https://kotlin-version.aws.icerock.dev/kotlin-version?group=dev.icerock.moko&name=permissions)

# Mobile Kotlin runtime permissions multiplatform controller
**moko-permissions** - Kotlin MultiPlatform library for providing runtime permissions on iOS & Android.

## Table of Contents
- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
- [List of supported permissions](#list-of-supported-permissions)
- [Usage](#usage)
- [Samples](#samples)
- [Set Up Locally](#set-up-locally)
- [Contributing](#contributing)
- [License](#license)

## Features
- **Permission** - enumeration with primary types of device permissions
- **PermissionsController** - handler for runtime permission requests can be used in the common code with lifecycle safety for Android
- **DeniedException** and **DeniedAlwaysException** - exceptions to handle user denial of permissions
- **Compose Multiplatform** support

## Requirements
- Gradle version 6.8+
- Android API 16+
- iOS version 12.0+

## Installation
root **build.gradle**
```groovy
allprojects {
    repositories {
      mavenCentral()
    }
}
```

project **build.gradle**
```groovy
dependencies {
    commonMainApi("dev.icerock.moko:permissions:0.17.0")
    
    // compose multiplatform
    commonMainApi("dev.icerock.moko:permissions-compose:0.17.0") // permissions api + compose extensions
    
    commonTestImplementation("dev.icerock.moko:permissions-test:0.17.0")
}
```

## List of supported permissions

The full list can be found in `dev.icerock.moko.permissions.Permission` enum.

* Camera: **Permission.CAMERA**
* Gallery: **Permission.GALLERY**
* Storage read: **Permission.STORAGE**
* Storage write: **Permission.WRITE_STORAGE**
* Fine location: **Permission.LOCATION**
* Coarse location: **Permission.COARSE_LOCATION**
* Background location: **Permission.BACKGROUND_LOCATION**
* Remote notifications: **Permission.REMOTE_NOTIFICATION**
* Audio recording: **Permission.RECORD_AUDIO**
* Bluetooth LE: **Permission.BLUETOOTH_LE**
* Bluetooth Scan: **Permission.BLUETOOTH_SCAN**
* Bluetooth Connect: **Permission.BLUETOOTH_CONNECT**
* Bluetooth Advertise: **Permission.BLUETOOTH_ADVERTISE**
* Motion: **Permission.MOTION**

## Usage

Common code:
```kotlin
class ViewModel(val permissionsController: PermissionsController): ViewModel() {
    fun onPhotoPressed() {
        viewModelScope.launch {
            try {
                permissionsController.providePermission(Permission.GALLERY)
                // Permission has been granted successfully.
            } catch(deniedAlways: DeniedAlwaysException) {
                // Permission is always denied.
            } catch(denied: DeniedException) {
                // Permission was denied.
            }
        }
    }
}
```

Android:
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
        
    val viewModel = getViewModel {
        // Pass the platform implementation of the permission controller to a common code.
        ViewModel(PermissionsController())
    }
    
    // Binds the permissions controller to the activity lifecycle.
    viewModel.permissionsController.bind(activity)
}
```

Compose:
```kotlin
@Composable
fun TestScreen() {
    val viewModel = getViewModel {
        // Pass the platform implementation of the permission controller to a common code.
        ViewModel(PermissionsController())
    }
    
    // Binds the permissions controller to the LocalLifecycleOwner lifecycle.
    BindEffect(viewModel.permissionsController)
}
```

iOS:
```swift
// Just pass the platform implementation of the permission controller to a common code.
let viewModel = ViewModel(permissionsController: PermissionsController())
```

### Compose Multiplatform
```kotlin
@Composable
fun Sample() {
    val factory: PermissionsControllerFactory = rememberPermissionsControllerFactory()
    val controller: PermissionsController = remember(factory) { factory.createPermissionsController() }
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    
    Button(
        onClick = {
            coroutineScope.launch {
                controller.providePermission(Permission.REMOTE_NOTIFICATION)
            }
        }
    ) {
        Text(text = "give permissions")
    }
}
```

Or with `moko-mvvm` with correct configuration change handle on android:
```kotlin
@Composable
fun Sample() {
    val factory: PermissionsControllerFactory = rememberPermissionsControllerFactory()
    val viewModel: PermissionsViewModel = getViewModel(
        key = "permissions-screen",
        factory = viewModelFactory { PermissionsViewModel(factory.createPermissionsController()) }
    )
    
    BindEffect(viewModel.permissionsController)

    Button(onClick = viewModel::onButtonClick) {
        Text(text = "give permissions")
    }
}

class PermissionsViewModel(
    val permissionsController: PermissionsController
) : ViewModel() {
    fun onButtonClick() {
        viewModelScope.launch {
            permissionsController.providePermission(Permission.REMOTE_NOTIFICATION)
        }
    }
}
```

## Samples
More examples can be found in the [sample directory](sample).

## Set Up Locally 
- In [permissions directory](permissions) contains `permissions` library;
- In [sample directory](sample) contains samples on android, ios & mpp-library connected to apps.

## Contributing
All development (both new features and bug fixes) is performed in `develop` branch. This way `master` sources always contain sources of the most recently released version. Please send PRs with bug fixes to `develop` branch. Fixes to documentation in markdown files are an exception to this rule. They are updated directly in `master`.

The `develop` branch is pushed to `master` during release.

More detailed guide for contributers see in [contributing guide](CONTRIBUTING.md).

## License
        
    Copyright 2019 IceRock MAG Inc
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
       http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

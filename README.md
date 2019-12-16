![moko-permissions](img/logo.png)  
[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0) [![Download](https://api.bintray.com/packages/icerockdev/moko/moko-permissions/images/download.svg) ](https://bintray.com/icerockdev/moko/moko-permissions/_latestVersion) ![kotlin-version](https://img.shields.io/badge/kotlin-1.3.61-orange)

# Mobile Kotlin runtime permissions multiplatform controller
**moko-permissions** - Kotlin MultiPlatform library for providing runtime permissions on iOS & Android.

## Table of Contents
- [Features](#features)
- [Requirements](#requirements)
- [Versions](#versions)
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

## Requirements
- Gradle version 5.4.1+
- Android API 16+
- iOS version 9.0+

## Versions
- kotlin 1.3.50
  - 0.1.0
  - 0.2.0
- kotlin 1.3.61
  - 0.3.0

## Installation
root **build.gradle**
```groovy
allprojects {
    repositories {
        maven { url = "https://dl.bintray.com/icerockdev/moko" }
    }
}
```

project **build.gradle**
```groovy
dependencies {
    commonMainApi("dev.icerock.moko:permissions:0.3.0")
}
```

**settings.gradle**
```groovy
enableFeaturePreview("GRADLE_METADATA")
```

## List of supported permissions

The full list can be found in `dev.icerock.moko.permissions.Permission` enum.

* Camera: **Permission.CAMERA**
* Gallery: **Permission.GALLERY**
* Storage: **Permission.STORAGE**
* Fine location: **Permission.LOCATION**
* Coarse location: **Permission.COARSE_LOCATION**

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
    viewModel.permissionsController.bind(lifecycle, supportFragmentManager)
}
```

iOS:
```swift
// Just pass the platform implementation of the permission controller to a common code.
let viewModel = ViewModel(permissionsController: PermissionsController())
```

## Samples
More examples can be found in the [sample directory](sample).

## Set Up Locally 
- In [permissions directory](permissions) contains `permissions` library;
- In [sample directory](sample) contains samples on android, ios & mpp-library connected to apps;
- For test changes locally use `:permissions:publishToMavenLocal` gradle task, after it samples will use locally published version.

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
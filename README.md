[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)
[ ![Download](https://api.bintray.com/packages/icerockdev/moko/moko-permissions/images/download.svg) ](https://bintray.com/icerockdev/moko/moko-permissions/_latestVersion)

# Базовые компоненты для мультиплатформы
## Permissions
Мультиплатформенные пермиссии позволяют запрашивать разрешения с уровня `viewModel` с использованием
 `coroutines`. Выглядит это следующим образом:
```kotlin
class ViewModel(val permissionsController: PermissionsController): ViewModel() {
    fun onPhotoPressed() {
        launch {
            try {
                permissionsController.providePermission(Permission.GALLERY)
                // при выполнении кода дальше мы можем быть уверены что разрешение выдано.
            } catch(error: Throwable) {
                // ошибка может быть платформенная (отказ от выдачи разрешения, отказ навсегда)
            }
        }
    }
}
```
На android контроллер использует retain фрагмент для сохранения `continuation` при смене конфигурации,
 но *после уничтожения приложения системой из-за нехватки памяти - после выдачи разрешения код в
 корутине не выполнится (корутины в этом случае уже не будет запущенной)*.  
На iOS никаких заморочек с жизненным циклом и так нет, так что узких мест в реализации нет.

Создается контроллер следующим образом:  
android:
```kotlin
val viewModel = getViewModel {
    ViewModel(PermissionsController())
}

viewModel.permissionsController.bind(lifecycle, supportFragmentManager)
```
iOS:
```swift
let viewModel = ViewModel(permissionsController: PermissionsController())
```

Для добавления новых пермиссий требуется изменение только в kotlin коде:
* в enum Permission добавить новую пермиссию;
* в android sourceSet в PermissionsController прописать маппинг пермиссии в платформенную;
* в ios sourceSet  в PermissionsController реализовать фукнцию получения нужной пермиссии. 

# i-screen-sdk 샘플 프로젝트
i-screen SDK 샘플 프로젝트입니다.

## 0. 용어 정리

* 매체 : i-screen을 탑재하는 앱을 의미합니다.
* 매체 포인트 : 매체에서 사용하는 재화를 의미합니다. (체리, 다이아, 포인트, 골드 등 매체마다 다른 재화명을 가지고 있습니다.)
* i-screen 포인트 : i-screen에서 사용하는 재화를 의미합니다. 단위는 `원`을 사용하며, i-screen 내의 유저 활동에 따라 포인트를 적립 또는 사용할 수 있습니다.

## 1. 프로젝트 설정
### 1-1. 프로젝트의 gradle.properties 파일의 repositories에 다음의 maven 저장소를 추가합니다.
```groovy
maven { url "https://raw.githubusercontent.com/i-ve/iscreen-mvn-repo/master/releases" }
```

최종 적용된 코드는 다음과 비슷하게 될 것 입니다.
```groovy
allprojects {
    repositories {
        jcenter()
        maven { url "https://raw.githubusercontent.com/i-ve/iscreen-mvn-repo/master/releases" }
    }
}
```
주의 : 하나의 프로젝트에는 보통 2개 이상의 gradle.properties 파일이 있는데, 이 중 *모듈이 아닌 프로젝트*의 gradle.properties 파일을 수정해야합니다.

### 1-2. 모듈의 gradle.properties 파일의 dependencies에 다음 내용을 추가합니다.

```groovy
compile 'kr.ive:iscreen:1.0.3'
```
gradle 파일을 수정하게 되면 Android Studio에서 `Sync Now`버튼이 보이게 됩니다. 
Sync를 하게 되면 메이븐 저장소에서 오퍼월 SDK 라이브러리를 다운로드 받게 됩니다.(로컬 저장소에 다운받기 때문에 프로젝트에서는 볼 수 없습니다)

### 1-3. AndroidManifest.xml 파일의 application안에 다음 내용을 추가합니다.
```xml
<meta-data android:name="iscreen_sdk_appcode" android:value="TIdKKXBq9C" />
```
* `iscreen_sdk_appcode`의 값에는 발급받은 매체 코드를 삽입합니다. 위에서 사용하고 있는 값은 테스트를 위한 용도입니다.

  ​

## 2. i-screen SDK 사용하기
### 2-1. i-screen 열기

```java
IScreen.run(activity)
```

위 코드를 사용하면 i-screen을 열 수 있습니다.

### 2-2. 매체 포인트 전환 브로드캐스트 받기

i-screen에서 적립한 포인트를 매체의 포인트로 전환하게 되는 경우 `Local Broadcast Receiver`로 결과를 받을 수 있습니다.

매체의 포인트가 출력되는 화면에서 `Local Broadcast Receiver`를 등록해서 사용할 수 있습니다. 

#### 2-2-1. BroadcastReceiver 선언

```java
private BroadcastReceiver mAppPointChangeReceiver = new BroadcastReceiver() {
	@Override
	public void onReceive(Context context, Intent intent) {
        //변환한 매체 포인트 : 기존 포인트에서 아래 값을 더해주면 됩니다.
		int addingAppPoint = IScreen.getAddingAppPoint(context, intent);
    }
};
```

#### 2-2-2. Local BroadcastReceiver 등록

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
	LocalBroadcastManager.getInstance(this).registerReceiver(mAppPointChangeReceiver, new IntentFilter("kr.ive.iscreen.action.app_point_change_complete"));    
}
```
등록을 할 때 액션 값은 반드시 `"kr.ive.iscreen.action.app_point_change_complete"`을 사용해야 합니다.

#### 2-2-3. Local BroadcastReceiver 해제

```java
@Override
protected void onDestroy() {
	super.onDestroy();
	LocalBroadcastManager.getInstance(this).unregisterReceiver(mAppPointChangeReceiver);
}
```

## 3. Trouble Shooting

### 3-1. support 라이브러리를 찾을 수 없는 경우

```groovy
Failed to resolve: com.android.support:appcompat-v7:25.4.0
Install Repository and sync project
Show in File
Show in Project Structure dialog
```

gradle sync 중에 위와 비슷한 메시지를 보게 된다면, 프로젝트의 `build.gradle` 파일의 `repositories`에 다음 내용을 추가합니다.

```groovy
maven { url "https://maven.google.com" }
```

### 3-2. support 라이브러리의 버전 문제

프로젝트의 `build.gradle` 파일에 `dependencies` 부분에 특정 support 라이브러리에 빨간 밑줄이 뜨며, 마우스를 가져가면 다음과 같은 메시지가 뜨는 경우가 발생할 수 있습니다.

```groovy
All com.android.support libraries must use the exact same version specification (mixing versions can lead to runtime crashes). Found versions 27.0.0, 25.3.1. Examples include com.android.support:animated-vector-drawable:27.0.0 and com.android.support:design:25.3.1 less... (⌘F1) 
There are some combinations of libraries, or tools and libraries, that are incompatible, or can lead to bugs. One such incompatibility is compiling with a version of the Android support libraries that is not the latest version (or in particular, a version lower than your targetSdkVersion.)
```

이는 모든 support 라이브러리가 같은 버전을 사용해야 함을 의미합니다.

이 문제를 해결하기 위해서는 먼저 디펜던시에 대한 트리를 확인해야 합니다.

Android Studio에 있는 Terminal에서 명령을 입력해야하는데, Android Studio의 버전에 따라 약간 달라지게 됩니다.

* Android Studio 3.0 이전

```shell
./gradlew -q dependencies <module-name>:dependencies --configuration compile
```

* Android Studio 3.0 이후

```shell
./gradlew -q dependencies <module-name>:dependencies --configuration debugAndroidTestCompileClasspath
```

위 명령 중 `<module-name>`은 프로젝트의 모듈명으로 대체해야 합니다.

위 명령의 결과로 터미널 상에 디펜던시 트리를 볼 수 있고, 그 중 일부만 살펴보겠습니다.

```shell
 +--- com.android.support:appcompat-v7:25.3.1 -> 27.0.0 (*)
 \--- com.android.support:design:25.3.1
```

`com.android.support:appcompat-v7`의 경우는 `25.3.1` 버전이었는데 `27.0.0`을 사용하도록 자동 변경되었는데, `com.android.support:design`의 경우는 `25.3.1`을 그대로 사용하고 있습니다.

이로 인해서 2가지(`25.3.1`과 `27.0.0`) 버전의 support 라이브러리가 혼재돼 있는 문제가 발생한 것이므로 이를 해결해주기 위해서는 `build.gradle`의 `dependencies`에 위 결과 트리에서 낮은 버전으로 표기돼 있는 라이브러리들을 모두 추가해서 명시적으로 높은 버전을 적어 줍니다.

이 경우에는 다음 내용을 `dependencies`에 추가하면 됩니다.

```groovy
compile 'com.android.support:design:27.0.0'
```

필요한 디펜던시를 모두 명시적으로 추가한 뒤에 gradle sync를 수행하면 이 문제를 해결할 수 있습니다.

## 4. SDK 변경 이력

### v 1.0.3

* 최초 상용 버전
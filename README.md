# binder
no aidl

## 关于集成：
- **在项目的根目录的`build.gradle`添加：**
```
allprojects {
    repositories {
        ...
	maven { url 'https://jitpack.io' }
    }
}
```
- **在应用模块的`build.gradle`添加：**
```
dependencies {
        implementation 'com.github.zero615.Binder:binder:1.0.4'
}
```


## 使用示例：
```
    //server端
    IBinder binder = Binder.asBinder(new TestImpl(),ITest.class);
    ....
    //client端
    ITest test = Binder.asInterface(binder,ITest.class);
```

## 作为 sdk集成
```
 dependencies {
    ...
    classpath 'com.github.zero615.Binder:compiler:1.0.4'
 }
```
 
- **在应用模块的`build.gradle`添加：**
```
    apply plugin: 'SdkBinder'

    插件自动在BuildConfig同级目录下导入库
```





# 自定义imageview，支持圆角，指定某一个角圆角

## 最新版本为
[![](https://jitpack.io/v/alguojian/RoundImageView.svg)](https://jitpack.io/#alguojian/RoundImageView)

Step 1. Add the JitPack repository to your build file
```
allprojects {
          repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
```

Step 2. Add the dependency
```
dependencies {
        implementation 'com.github.alguojian:RoundImageView:1.1.1'
    }
```


## xml使用如下，单独设置角度支持java代码

```

 <com.alguojian.view.RoundImageView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:src="@mipmap/ic_launcher"
        app:img_aspectRatio="1.2"   //设置宽高比例
        app:img_type="round"     //设置圆角，默认圆形
        app:img_scale_type="centerCrop" //圆角图片显示中间部分
        app:img_leftTopRadius="12dp"
        app:img_leftBottomRadius="12dp"
        app:img_rightTopRadius="12dp"
        app:img_rightBottomRadius="12dp"
        app:img_radius="12dp" />

```
|img_type|作用|
|---|---|
|circle|默认值，显示圆形，一般用于显示头像等|
|round|显示圆角，搭配角度使用|


|img_scale_type|作用|
|---|---|
|avatar|显示图片中间靠上部分，一般用于显示头像等|
|centerCrop|默认值，显示图片中间部分，已放大和缩小|
|fitXY|全部显示图片，压缩或者放大|

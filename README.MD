# 自定义imageview，支持圆角，指定某一个角圆角



## xml使用如下，单独设置角度支持java代码

```

 <com.alguojian.view.RoundImageView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:src="@mipmap/ic_launcher"
        app:aspectRatio="1.2"   //设置宽高比例
        app:imgType="round"     //设置圆角，默认圆形
        app:leftTopRadius="12dp"
        app:leftBottomRadius="12dp"
        app:rightTopRadius="12dp"
        app:rightBottomRadius="12dp"
        app:radius="12dp" />

```
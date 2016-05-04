# PullCirceListView
  这是一个下拉刷新控件，随着手势下下拉，头像位置会出现两道光环，如果光环闭合，则代表下拉刷新成功，反之取消。
# Demo
![](https://raw.githubusercontent.com/CaesarZhao/PullCircleListView/master/screenshot/screen.gif)
## Usage

### Step 1

#### Gradle
```groovy
dependencies {
    	compile 'com.ford:pullcirclelibrary:1.0.0'
}
```

#### Maven

```xml
<dependency>
  <groupId>com.ford</groupId>
  <artifactId>pullcirclelibrary</artifactId>
  <version>1.0.0</version>
  <type>pom</type>
</dependency>
```

### Step 2
在你的布局添加控件：

```java
 <com.ford.pullcirclelibrary.PullToRefreshCircleView
        android:id="@+id/listview"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:divider="@null"
        android:footerDividersEnabled="false"
        android:headerDividersEnabled="false"   
        />
```
### Step3
在java文件中：

```java
是否支持加载更多
mListView.setPullLoadEnable(false);

mListView.setOnRefreshListener(new PullToRefreshCircleView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                刷新
            }

            @Override
            public void onLoadMore() {
                加载更多
            }
        });

```
====

## Thanks
- [CircleImageView](https://github.com/hdodenhof/CircleImageView)






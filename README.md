### DeMon-RxBus

>RxBus名字看起来像一个库，但它并不是一个库，而是一种模式，它的思想是使用RxJava来实现了EventBus ，而让你不再需要使用Otto或者GreenRobot的EventBus。——[给 Android开发者的RxJava详解](https://gank.io/post/560e15be2dca930e00da1083)

**1. 基于以上思想，通过封装RxJava2.x而实现的RxBus2.x的事件总线库。**    
**2. 用于解决进程或者组件间的通信问题。**

### 为什么要使用RxBus？
    如上所说，我们可以通过封装RxJava实现EventBus。 
    随着RxJava在Android项目中的普及, 我们完全可以使用RxBus代替EventBus，减少库的引入，增加系统的稳定性。 
    EventBus虽然使用方便，但是在事件的生命周期的处理上需要我们利用订阅者的生命周期去注册和取消注册，这个部分还是略有麻烦之处。 
    而我们可以结合使用RxLifecycle来配置，简化这一步骤。 


### 使用

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

```
implementation 'com.github.DeMonLiu623:DeMon-RxBus:1.2'

```
### RxBus详解
框架详解，请查看：[Android 打造RxBus2.x的全面详解](https://blog.csdn.net/DeMonliuhui/article/details/82532078)

#### 生命周期
使用Rxlifecycle控制生命周期解决RxJava引起的内存泄漏。 
 
```java
 AndroidLifecycle.createLifecycleProvider(LifecycleOwner owner).<T>bindUntilEvent(Lifecycle.Event event)
```

1. LifecycleOwner owner 生命周期所有者，传上下文this。
2. Lifecycle.Event event 在指定的生命周期事件中结束序列(RxJava)。

|Lifecycle.Event|对应生命周期|
|--|--|
|ON_CREATE|在onCreate之后|
|ON_START|在onStart之后|
|ON_RESUME|在onResume之后|
|ON_PAUSE|再onPause之前|
|ON_STOP|在onStop之前|
|ON_DESTROY|在onDestory之前|
|ON_ANY|match all events|


>PS：在之前的1.1版本中使用RxLifecycle.bind(lifecycle)去绑定生命周期，实际上使用时发现：    
 如果组件没有继承RxComponents(如RxActivity)则不会按照预期的相反的生命周期事件中结束序列。    
 因此从1.2版本开始使用provider.bindUntilEvent来指明在哪个生命周期事件中结束序列（默认Lifecycle.Event.ON_DESTROY）。

#### 消息事件实体

```java
class MsgEvent(var msg: String)
```

#### 普通消息事件

##### 接收消息事件
```java
//Lifecycle.Event.ON_DESTROY
RxBus.getInstance().toObservable(this, MsgEvent::class.java).subscribe { msg ->
            //处理消息
        }
```
或者
```java
RxBus.getInstance().toObservable(this, MsgEvent::class.java, Lifecycle.Event.ON_PAUSE).subscribe { msg ->
           //处理消息
        }
```

##### 发布消息事件

```java
RxBus.getInstance().post(MsgEvent("Java"));
```

#### 粘性消息事件（Sticky）
用于解决先发布事件，然后再订阅事件的情况。

#####  接收粘性消息事件
```java
//Lifecycle.Event.ON_DESTROY
RxBus.getInstance().toObservableSticky(this, MsgEvent::class.java).subscribe { msg ->
           //处理消息
        }
```
或者
```java
RxBus.getInstance().toObservableSticky(this, MsgEvent::class.java, Lifecycle.Event.ON_PAUSE).subscribe { msg ->
           //处理消息
        }
```

##### 发送粘性消息事件

```java
  RxBus.getInstance().postSticky(MsgEvent("Java"));
```

### 截图
<img src="https://raw.githubusercontent.com/DeMonLiu623/DeMon-RxBus/master/screen/ezgif.com-video-to-gif.gif" alt="这是一张网络图片" height="500" width="300">

![](https://raw.githubusercontent.com/DeMonLiu623/DeMon-RxBus/master/screen/20191113142401.png)

### 更多

请参考请参考demo app代码。

### BUG or 问题
请在issues留言，定期回复。


### MIT License

```
Copyright (c) 2018 DeMon

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

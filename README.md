### DeMon-RxBus

>RxBus名字看起来像一个库，但它并不是一个库，而是一种模式，它的思想是使用RxJava来实现了EventBus ，而让你不再需要使用Otto或者GreenRobot的EventBus。——[给 Android开发者的RxJava详解](https://gank.io/post/560e15be2dca930e00da1083)

**基于以上思想，通过封装RxJava2.x而实现的RxBus2.x的事件总线库。**    
**用于解决进程或者组件间的通信问题。**

### 为什么要使用RxBus？
    如上所说，我们可以通过封装RxJava实现EventBus。 
    随着RxJava在Android项目中的普及, 我们完全可以使用RxBus代替EventBus，减少库的引入，增加系统的稳定性。 
    EventBus虽然使用方便，但是在事件的生命周期的处理上需要我们利用订阅者的生命周期去注册和取消注册，这个部分还是略有麻烦之处。 
    而我们可以结合使用RxLifecycle来配置，简化这一步骤。 

### 使用
框架详解，请查看：[Android 打造RxBus2.x的全面详解](https://blog.csdn.net/DeMonliuhui/article/details/82532078)

#### 工程的Gradle

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
#### Moudle的Gradle

```
implementation 'com.github.DeMonLiu623:DeMon-RxBus:1.1'
```
#### 事件实体

```java
public class MsgEvent {
    private String msg;

    public MsgEvent(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
```

#### 订阅事件
使用Rxlifecycle解决RxJava引起的内存泄漏。  
通过上下文this绑定订阅者的生命周期即可。  

|在xxx绑定|在xxx销毁|
|--|--|
|onCreate|onDestory|
|onStart|onStop|
|onResum|onPause|
|onPause|onStop|
|onStop|onDestory|


```java

RxBus.getInstance().toObservable(this,MsgEvent.class).subscribe(new Consumer<MsgEvent>() {
            @Override
            public void accept(MsgEvent msgEvent) throws Exception {
                //处理事件
            }
        });
```

#### 发布事件

```java
RxBus.getInstance().post(new MsgEvent("Java"));
```

#### 粘性事件（Sticky）
用于解决先发布事件，然后再订阅事件的情况。

```java
RxBus.getInstance().toObservableSticky(this, MsgEvent.class).subscribe(new Consumer<MsgEvent>() {
            @Override
            public void accept(MsgEvent msgEvent) throws Exception {
                //处理事件
                text.setText(msgEvent.getMsg());
            }
        });
```

```java
  RxBus.getInstance().postSticky(new MsgEvent("Java"));
```
### BUG or 问题
请E-mail：757454343@qq.com 联系我。


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

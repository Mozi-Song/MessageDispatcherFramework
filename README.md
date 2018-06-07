# MessageDispatcherFramework 
### A small framework to dispatch messages to multi-level message handlers and thus avoid long swtich-cases
#### Code & Design credit: [Dailey Dai](https://github.com/daileyet)

Before this framework:

```java
switch(messageID){
  case 0x01: doSomething();  break;
  case 0x02: doAnotherThing(); break;
  case 0x03: 
  　　byte subID = getSubID();
    switch(subID)
    case 0x01: process1In3(); break;
    case 0x02: process2In3(); break;
    ...
    break;
  ...
}
```
Using this framework:

```java
  private HandlerDispatcher<String> handlerDispatcher;
  
  void init(){
        handlerDispatcher = new HandlerDispatcherImpl<>();
        handlerDispatcher.load(this); 
  }
  
  public void onReceivedMessage(String data) {
      handlerDispatcher.getHandler(data.charAt(0)+"").process(data); 
  }

  final Supplier<HandlerDispatcher<String>> supplier = () -> handlerDispatcher;

  @Mapped("1")
  final CustomizedHandler A = (data) -> { log("A"); };
  
  @Mapped("2") 
  final ParentHandler<String> B = ParentHandler.build(supplier, (data) -> (data.charAt(1)+""));
  
  @Parent("B")
  @Mapped("1") 
  final ParentHandler<String> B1 = ParentHandler.build(supplier, (data) -> (data.charAt(2)+""));
  
  @Parent("A")
  @Mapped("1") 
  final CustomizedHandler A1 = (data) -> { log("A1"); };
  
  @Parent("B1")
  @Mapped("1") 
  final CustomizedHandler B11 = (data) -> { log("B11"); };
```

A more detailed documentation see [here](https://www.cnblogs.com/mozi-song/p/9150442.html) (Chinese).

/*
 * Copyright (c) 2017, Robert Bosch (Suzhou) All Rights Reserved. This software is property of
 * Robert Bosch (Suzhou). Unauthorized duplication and disclosure to third parties is prohibited.
 */
package util.example;

import java.util.function.Consumer;
import java.util.function.Supplier;

import util.Handler;
import util.HandlerDispatcher;
import util.HandlerDispatcherImpl;
import util.ParentHandler;
import util.annotation.Mapped;
import util.annotation.Parent;
import static util.util.Logger.log;

/**
 * ClassName: HandlerExecutorTest <br>
 * date: May 29, 2018 1:49:38 PM <br>
 * 
 * @author dailey.dai@cn.bosch.com DAD2SZH
 * @since JDK 1.8
 */
public class HandlerExecutorCompositeAdvDemo {


	  public HandlerExecutorCompositeAdvDemo() {
	    handlerDispatcher = new HandlerDispatcherImpl<>();
	    handlerDispatcher.load(this);
	  }
  private Consumer<String> consumer;
	  
	  
  private HandlerDispatcher<String> handlerDispatcher;

  void init(){
	    handlerDispatcher = new HandlerDispatcherImpl<>();
	    handlerDispatcher.load(this);
  }
  
  public void onReceivedMessage(String data) {
	  handlerDispatcher.getHandler(data.charAt(0)+"").process(data);
  }

  final Supplier<HandlerDispatcher<String>> supplier = () -> handlerDispatcher;

  @Mapped("1") //第一层，处理消息ID为1的消息
  final CustomizedHandler A = (data) -> { log("A"); };
  
  @Mapped("2") //第一层，处理消息ID为2的消息
  final ParentHandler<String> B = ParentHandler.build(supplier, (data) -> (data.charAt(1)+""));
  
  @Parent("B")
  @Mapped("1") //第二层，处理消息ID为2，子消息ID为1的消息
  final ParentHandler<String> B1 = ParentHandler.build(supplier, (data) -> (data.charAt(2)+""));
  
  @Parent("A")
  @Mapped("1") //第二层，处理消息ID为1，子消息ID也为1的消息
  final CustomizedHandler A1 = (data) -> { log("A1"); };
  
  @Parent("B1")
  @Mapped("1") //第三层，处理消息ID为1，子消息ID为2，子子消息ID为1的消息
  final CustomizedHandler B11 = (data) -> { log("B11"); };
  
  @Parent("BCD")
  @Mapped("1")
  final CustomizedHandler NOT_FOUND = (data) -> { log("XX"); };
  
  public static void main(String[] args){
	  HandlerExecutorCompositeAdvDemo d = new HandlerExecutorCompositeAdvDemo();
	  d.onReceivedMessage("1111");
	  d.onReceivedMessage("2111");
  }
  
  public void setConsumer(Consumer<String> consumer) {
    this.consumer = consumer;
  }

  @FunctionalInterface
  interface CustomizedHandler extends Handler<String> {}

  void simpleProcess(String data) {
    /*if (consumer != null)
      consumer.accept(data);*/
	  log(this.getClass().getSimpleName() + " processing.");
  }
  
  
  
  
  
    //存放消息ID和对应的MessageHandler
    private Map<Byte, MessageHandler> handlersMap = null;
    void init() {
    	//读取注解，将下列定义的HANDLER_1,2,3加载到handlersMap中
		handlersMap = MessageHandlerUtil.loadMessageHandlers(this);
	}

    //接收消息的回调函数
	public void onDataReceived(CommData command) {
		byte commandID = getCommandId(command);
		handlersMap.get(commandID).process(command);//调用对应的Handler处理
	}

	//消息ID为01的Handler
	@Handle(messageID=0x01)
	private final MessageHandler HANDLER_1 = (command) -> {	doSomething(); };
	//消息ID为02的Handler
	@Handle(messageID=0x02)
	private final MessageHandler HANDLER_2 = (command) -> { doAnotherThing(); };
	//消息ID为03的Handler
	@Handle(messageID=0x03)
	private final MessageHandler HANDLER_3 = (command) -> { doSomeOtherThing(); };
	
	@SubHandle(messageID = ProductionSubId.DIAG_GPS_TURNON)
	private final MessageHandler GPS_TURNON_HANDLER = (command) -> {
		// send GPSCOORDINATE, GPSSNRPRN, GPSSATINUSE,
		// GPSSATINVIEW, GPSSATFIXINFO, GPSPDOP periodically
		LOGGER.verbose("gpsSenderFuture is " + gpsSenderFuture); //todo:remove
		if (gpsSenderFuture != null && !gpsSenderFuture.isDone())
			gpsSenderFuture.cancel(true);
		gpsSenderFuture = getContext().scheduleAtFixedRate(() -> {sendCoordinate();}, 
				0, 10, TimeUnit.SECONDS);
	};
	
  
}

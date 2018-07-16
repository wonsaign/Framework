package com.nec.zeusas.core.event;

import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * POSIX 信号注册
 * <p>
 *
 * 本方法仅用于Linux, MacOS X, Solaris, AIX 等支持POSIX信号规范的UNIX系统。 <br>
 * 
 * <li>1. 实现接口 com.nec.zeusas.core.event.BGSignalHandle
 * <li>2. 继承实现类：BGSignalRegister
 * <li>3. 调用{@code BGSignalRegister#register()}注册
 */
public abstract class BGSignalRegister implements Observer {
	static Logger logger = LoggerFactory.getLogger(BGSignalRegister.class);

	protected abstract BGSignalHandle getPosixSignal();

	public void register() {
		final SignalHandler handle = new SignalHandler(getPosixSignal());

		logger.debug("Registed: UNIX POSIX signal {}", "HUP,INT,TERM,ABRT");
		handle.addObserver(this);
		// 注册处理信号 
		handle.handleSignal("HUP");
		handle.handleSignal("INT");
		handle.handleSignal("TERM");
		handle.handleSignal("ABRT");
	}

	@SuppressWarnings("restriction")
	static class SignalHandler extends Observable implements sun.misc.SignalHandler {

		SignalHandler(BGSignalHandle posixSignal) {
			this.posixSignal = posixSignal;
		}

		final BGSignalHandle posixSignal;

		@Override
		public void handle(sun.misc.Signal signalName) {
			// EXEC: (1)
			logger.info("DO: Signal: {} (1) start...", signalName);
			this.posixSignal.catchSignal();
			logger.info("DO: Signal: {} (2) invoke handle()...", signalName);
			this.posixSignal.handle();
			super.setChanged();
			super.notifyObservers(signalName);
			// EXEC (3)
			logger.info("DO: Signal: {} (3) shutdown...", signalName);
			this.posixSignal.postHandle();
		}

		public void handleSignal(String signalName) {
			logger.info("DO: Register signal: {} (0) start...", signalName);
			sun.misc.Signal.handle(new sun.misc.Signal(signalName), this);
		}
	}

	@Override
	public void update(Observable observable, Object signal) {
		logger.info("recv: {} {}", observable, signal);
	}
}

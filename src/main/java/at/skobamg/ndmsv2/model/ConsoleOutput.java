package at.skobamg.ndmsv2.model;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import at.skobamg.ndmsv2.mediator.IEventMediator;

public class ConsoleOutput extends OutputStream {
	private StringProperty consoleText = new SimpleStringProperty("");
	public static long consoleWaitPeriod = 2000;
	
	
	public String getConsoleText() { return consoleText.get(); }
	public StringProperty consoleTextProperty() {
		return consoleText;
	}
	
	@Override
	public void write(int b) throws IOException {		
		consoleText.set(consoleText.get()+(char)b);
	}
	
	public void resetOutput() {
		consoleText.setValue("");
	}

}

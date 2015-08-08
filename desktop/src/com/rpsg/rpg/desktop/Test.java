package com.rpsg.rpg.desktop;

import java.lang.reflect.Method;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;

public class Test {

	public void postMessage(String... s) throws InterruptedException {
		System.out.println(s[0]);
	}

	V8 v8;

	public void start() throws InterruptedException {
		v8 = V8.createV8Runtime();
		v8.registerJavaMethod(this, "postMessage", "postMessage",
				new Class<?>[] { String[].class }, true);
		v8.registerJavaMethod(this, "tex", "tex", new Class<?>[] {}, false);
		V8Object obj = v8.executeObjectScript("tex");
		V8Object tex = obj.getRutime().executeObjectScript("tex.prototype");
		tex.registerJavaMethod(this, "postMessage", "postMessage",
				new Class<?>[] { String[].class }, true);
		obj.release();
		tex.release();

		v8.executeVoidScript("var tex=new tex();tex.postMessage('a');postMessage('b')");
	}

	public void tex() {
		System.out.println("a new 'tex' javascript object was created.");
	}

	public static void main(String[] args) throws InterruptedException {
		Test t = new Test();
		t.registerBridge(new Abc());

		//t.v8.executeVoidScript("var abc = new Abc();abc.a();");
	}

	public void registerBridge(Object obj) {
		v8 = V8.createV8Runtime();
		Class<?> c = obj.getClass();	
		Method[] methods = c.getDeclaredMethods();
		for (Method m : methods) {

			v8.registerJavaMethod(obj, m.getName(), m.getName(),
					m.getParameterTypes(), false);
		}	
	
		V8Object object = v8.executeObjectScript(c.getSimpleName());
		V8Object o = object.getRutime().executeObjectScript(c.getSimpleName()+".prototype");
		object.release();
		o.release();

	}

}

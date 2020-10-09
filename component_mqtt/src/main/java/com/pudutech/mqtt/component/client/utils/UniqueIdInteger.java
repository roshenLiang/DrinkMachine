
package com.pudutech.mqtt.component.client.utils;

import java.io.Serializable;

public class UniqueIdInteger implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int value = 1;

	private int addInc() {
		value = value + 1;
		if (value > 65535) {
			value = 1;
		}
		return value;
	}
	
	public int id() {
		return value;
	}
}

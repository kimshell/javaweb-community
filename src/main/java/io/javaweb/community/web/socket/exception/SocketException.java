package io.javaweb.community.web.socket.exception;

import javax.websocket.CloseReason;

public class SocketException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -144563234935414901L;
	
	private CloseReason closeReason;
	
	public SocketException() {}
	
	public SocketException(CloseReason closeReason) {
		this.closeReason = closeReason;
	}
	
	public SocketException(CloseReason.CloseCode code ,String reason) {
		this.closeReason = new CloseReason(code, reason);
	}
	
	public SocketException(String reason) {
		this.closeReason = new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, reason);
	}

	public CloseReason getCloseReason() {
		return closeReason;
	}

	public void setCloseReason(CloseReason closeReason) {
		this.closeReason = closeReason;
	}
}

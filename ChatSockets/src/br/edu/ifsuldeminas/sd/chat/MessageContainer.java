package br.edu.ifsuldeminas.sd.chat;

public interface MessageContainer {
	
	String FROM = "::FROM::";
	void newMessage(String message);
}
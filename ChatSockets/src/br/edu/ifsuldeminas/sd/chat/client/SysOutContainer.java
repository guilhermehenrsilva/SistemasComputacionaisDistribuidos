package br.edu.ifsuldeminas.sd.chat.client;

import br.edu.ifsuldeminas.sd.chat.MessageContainer;


public class SysOutContainer implements MessageContainer {
public void newMessage(String message) {
System.out.println(String.format(":> %s", message));
}
}
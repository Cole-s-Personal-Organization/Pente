package com.mycompany.app.WebServer.Models;

public class Message {
    private String from;
    private String to;      // defines person to send the message to, if null it will be assumed it is for everyone
    private String context; // describes which context to send the message to (e.g. to a specific game lobby) 
    private String content;

    
    public Message(String from, String to, String context, String content) {
        this.from = from;
        this.to = to;
        this.context = context;
        this.content = content;
    }


    public String getFrom() {
        return from;
    }
    public String getTo() {
        return to;
    }
    public String getContext() {
        return context;
    }
    public String getContent() {
        return content;
    }

    public void setFrom(String from) {
        this.from = from;
    }
    public void setTo(String to) {
        this.to = to;
    }
    public void setContext(String context) {
        this.context = context;
    }
    public void setContent(String content) {
        this.content = content;
    }
}

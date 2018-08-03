package com.bot.filter.words;

import java.awt.Color;
import java.util.List;
import java.util.Map;

import org.javacord.api.entity.message.Message;

import com.bot.response.Notification;
import com.bot.response.UserFilterNotification;

public class HighUrgencyWords extends AbstractWords {
	
	public HighUrgencyWords(Message message, List<Map<String, List<String>>> words) {
		super(message, words);
		List<String> blacklist = retrieveBlacklist(words);
		setBlacklist(blacklist);
	}
	
	private List<String> retrieveBlacklist(List<Map<String, List<String>>> words) {
		return words.get(0).get("high urgency");
	}

	public void sendNotification() {
		
		getMessage().delete();
		
		Notification notification = new UserFilterNotification(getMessage(), getFlaggedWords());
		notification.send(new Color(255,0,0));
	}
}

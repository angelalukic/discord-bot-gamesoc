package com.bot.subscription.discord;

import com.bot.discord.server.DiscordServer;
import com.bot.twitch.listener.TwitchListener;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiscordSubscription {
	
	private TwitchListener listener;
	private DiscordServer server;
	
	public DiscordSubscription() {
	}

	public DiscordSubscription(TwitchListener listener, DiscordServer server) {
		this.listener = listener;
		this.server = server;
	}
}

package com.bot.discord;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.bot.twitch.features.beans.TwitchChatMessage;
import com.bot.twitch.features.beans.TwitchStreamHost;
import com.bot.twitch.features.beans.TwitchStreamLive;

@FeignClient(name="discord-service", url="localhost:8080")
public interface DiscordServiceProxy {
	
	@PostMapping("twitch/embed/host/{server}")
	public ResponseEntity<Object> sendToDiscord(@RequestBody TwitchStreamHost event, @PathVariable("server") long server);
	
	@PostMapping("twitch/embed/stream/{server}")
	public ResponseEntity<Object> sendToDiscord(@RequestBody TwitchStreamLive event, @PathVariable("server") long server);
	
	@PostMapping("twitch/embed/chat/{server}")
	public ResponseEntity<Object> sendToDiscord(@RequestBody TwitchChatMessage message, @PathVariable("server") long server);
}
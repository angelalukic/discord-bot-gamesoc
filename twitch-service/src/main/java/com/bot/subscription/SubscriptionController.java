package com.bot.subscription;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.bot.twitch.beans.listener.TwitchListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class SubscriptionController {
	
	@Autowired private SubscriptionDAO service;
	
	@DeleteMapping("/twitch/discord/subscription/")
	public ResponseEntity<Object> deleteDiscordSubscription(@RequestBody Subscription subscription) {
		log.info("DELETE localhost:8082/discord/subscription");
		return service.deleteSubscription(subscription);
	}
	
	@PostMapping("/twitch/discord/subscription/")
	public ResponseEntity<Object> createDiscordSubscription(@RequestBody Subscription subscription) {
		log.info("POST localhost:8082/discord/subscription");
		TwitchListener listener = service.addSubscription(subscription);
		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(listener.getId()).toUri();
		return ResponseEntity.created(location).build();
	}
}

package com.bot.discord;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.javacord.api.event.server.member.ServerMemberJoinEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.bot.discord.beans.server.DiscordServer;
import com.bot.discord.beans.server.DiscordServerRepository;
import com.bot.discord.exception.ChannelNotFoundException;
import com.bot.discord.exception.ServerNotFoundException;
import com.bot.discord.exception.UserNotFoundException;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;

@Component
public class DiscordUtils {
	
	@Autowired private DiscordServerRepository discordServerRepository;
	
	private Integer retrieveGeneralChannelIndex(List<ServerTextChannel> channels) {
		for(int i = 0; i < channels.size(); i++) {
			ServerTextChannel channel = channels.get(i);
			if(channel.getName().equalsIgnoreCase("general") && channel.canYouWrite())
				return i;
		}
		return null;
	}
	
	public ServerTextChannel retrieveChannelById(Server server, long id) {
		Optional<ServerTextChannel> channel = server.getTextChannelById(id);
		if(channel.isPresent())
			return channel.get();
		return null;		
	}
	
	public Message sendMessageToServerOwner(TextChannel channel, EmbedBuilder embed) throws InterruptedException, ExecutionException {
		User serverOwner = retrieveServerOwnerFromTextChannel(channel);
		return serverOwner.sendMessage(embed).get();
	}
	
	public User retrieveServerOwnerFromTextChannel(TextChannel channel) {
		Optional<ServerTextChannel> optionalChannel = channel.asServerTextChannel();
		if(optionalChannel.isPresent()) {
			ServerTextChannel textChannel = optionalChannel.get();
			return textChannel.getServer().getOwner();
		}
		throw new ChannelNotFoundException("id="+ channel.getId());
	}
	
	public long retrieveWritableChannelId(Server server) {
		List<ServerTextChannel> channels = server.getTextChannels();
		Integer generalChannelIndex = retrieveGeneralChannelIndex(channels);
		
		if(generalChannelIndex != null)
			return channels.get(generalChannelIndex).getId();
	
		// If general channel does not exist or is not writable, return first writable text channel
		for(int i = 0; i < channels.size(); i++) {
			ServerTextChannel channel = channels.get(i);
			if(channel.canYouWrite())
				return channel.getId();
		}
		return 0L;
	}
	
	public DiscordServer getDiscordServerFromServerOptional(Optional<Server> serverOptional, long id) {
		Server server = getServerFromServerOptional(serverOptional, id);
		long serverId = server.getId();
		Optional<DiscordServer> discordOptional = discordServerRepository.findById(serverId);
		if(discordOptional.isPresent())
			return discordOptional.get();
		else
			throw new ServerNotFoundException("id=" + id);
	}
	
	public Server getServerFromServerOptional(Optional<Server> server, long id) {
		if(server.isPresent())
			return server.get();
		throw new ServerNotFoundException("id=" + id);
	}
	
	public User getUserFromUserOptional(Optional<User> user, long id) {
		if(user.isPresent())
			return user.get();
		throw new UserNotFoundException("id=" + id);
	}
	
	public void sendMessage(EmbedBuilder embed, MessageCreateEvent event) {
		Message message = event.getMessage();
		if(message.getChannel().canYouWrite())
			message.getChannel().sendMessage(embed);
		else {
			User user = getUserFromUserOptional(message.getUserAuthor(), message.getId());
			user.sendMessage(embed);
		}
	}
	
	public void sendMessageToUser(EmbedBuilder embed, MessageCreateEvent event) {
		User user = getUserFromUserOptional(event.getMessage().getUserAuthor(), event.getMessageId());
		user.sendMessage(embed);
	}
	
	public void sendMessageToUser(String reply, MessageCreateEvent event) {
		User user = getUserFromUserOptional(event.getMessage().getUserAuthor(), event.getMessageId());
		user.sendMessage(reply);
	}
	
	public void sendMessage(EmbedBuilder embed, ServerMemberJoinEvent event, ServerTextChannel channel) {
		if(channel.canYouWrite())
			channel.sendMessage(embed);
		else {
			User user = event.getUser();
			user.sendMessage(embed);
		}
	}
	
	public void sendMessage(EmbedBuilder embed, ReactionAddEvent event) {
		Optional<TextChannel> optionalChannel = event.getChannel().asTextChannel();
		if(optionalChannel.isPresent()) {
			TextChannel channel = optionalChannel.get();
			if(channel.canYouWrite())
				channel.sendMessage(embed);
			else {
				User user = retrieveServerOwnerFromTextChannel(channel);
				user.sendMessage(embed);
			}
		}
		else
			throw new ChannelNotFoundException("id=" + event.getMessageId());
	}

	public com.github.twitch4j.helix.domain.User getTwitchUserFromHelix(String username) {
		TwitchClient twitchClient = TwitchClientBuilder.builder()
	            .withEnableHelix(true)
	            .build();
		List<com.github.twitch4j.helix.domain.User> users = twitchClient.getHelix().getUsers(null, null, Arrays.asList(username)).execute().getUsers();
		return users.get(0);
	}
	
	public ResponseEntity<Object> getResponseEntity(Message savedEmbed) {
		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(savedEmbed.getId()).toUri();
		return ResponseEntity.created(location).build();
	}
}

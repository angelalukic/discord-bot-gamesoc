package com.bot.discord.beans.server;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscordServerRepository extends JpaRepository<DiscordServer, Long> {
}

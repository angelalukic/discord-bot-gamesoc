package com.filter.message.discord;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscordServerRepository extends JpaRepository<DiscordServer, Long> {
}

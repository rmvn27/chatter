package chatter.db

import chatter.ChannelEntity
import chatter.TeamEntity

// for logging purposes to not display too much data
fun TeamEntity.display() = "Team(${slug})"

fun ChannelEntity.display() = "Channel(${name})"

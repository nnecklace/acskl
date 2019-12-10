# Requirement Specification

## Purpose

The purpose of the application is for for registered users to be able to send messages to a common message server, much like something like slack or irc. There are only _normal users_, later on there might be admins or moderators. 

## Technical requirements

The message server should be able to be run on any unix based os that has java 11 or higher installed. Same applies for the client application. All data will be stored on a sqlite file handled by the message server.

## Users

As mentioned before, in the beginning there will only be _normal users_. A _normal user_ is anyone who creates a normal user account on the message server. Possibility to add _admins_ or _moderators_ who could have special rights, like deleting messages or banning users from the message server. _Admin users_ would have the possiblity to decide who is a _moderator_ and can revoke _moderator_ statuses.

## Interface

The application would have 2 _views_, login and chat. Users would be able to login to the message server from the login view. If users doesn't exist, the user would be promted to create one on the same view. Once login is successful -- users will be redirected to the chat view where they will see a list of messages and be able to participate in the chat. All messages will have authors name posted as well and a timestamp of when the message was sent.

<img src="https://raw.githubusercontent.com/nnecklace/acskl/master/documents/diagrams/acskl-flow.png" width="500px"/>

## Functionality

### Login view
- User can create users on the message server [Done]
  - Usernames are limited to 20 characters UTF-8 [Done]
- User can login to the message server if the username exists [Done]
  - No password required [Done]
  - If username doesn't exist, a message will appear indicating that the username needs to be created [Done]

### Chat view
- User can send a message to the chat [Done]
  - Message are limited to 1000 characters [Done]
  - Message will be added to the bottom of the list [Done]
- User can view messages on the chat [Done]
- User can delete their own messages
- User can slap other users with a trout
- User can scroll through the chat history [Done]
- User can disconnect from the chat [Done]

## Future ideas
- Have password authentication for users
- Have a username and password registration
- Have moderator roles
- Have admin roles
- Make messages editable by the author

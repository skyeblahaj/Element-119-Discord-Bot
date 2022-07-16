# Element-119-Discord-Bot
Revamped discord bot in development.

Documentation:

API:
	Building your server's .json file is an easy task. Open any text editor and create a .json file. Follow this example for your own ideas.

```json
{
    "commands" : {
        "ping" : {
            "message" : "pong"
        }
    }
}
```

    Result:
![](/api/resources/tutorial.png)

The "commands" object contains all commands you want to implement. The command is also an object which includes an action mapped to its action value. In this case, the action was to send a message, and the value is "pong".

Using the audio player is a feature too. The syntax looks like this:

```json
{
    "commands" : {
        "play" : {
            "sfx" : "[url]"
        }
    }
}
```

The action **"sfx"** is to use the audio player and has the same interface as the client interface (except for files). Provide a URL that the bot can validly queue within the map entry and call the command. Note that you must be in a voice channel to use it.
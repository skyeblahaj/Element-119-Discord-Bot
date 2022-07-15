# Element-119-Discord-Bot
Revamped discord bot in development.

Documentation:

API:
	Building your server's **.json** file is an easy task. Open any text editor and create a **.json** file. Follow this example for your own ideas.

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
# Minecraft Shell
Allows to put minecraft commands in a separate file and execute it

## usage:
* __/mish [--args] <script> [params]__
  
  executes __script.mish__ located in __.minecraft/scripts__ or __minecraft/scripts__ or __server_folder/scripts__

## syntax:
_Write your script the way you want. __/___ _are optional:_
* `say HI`
* `/say HI`

_Comment you code with __#__:_  
* `# hi there!`

_Manage variables:_  
* `${variable1=variable2=...=variablen=value}`  
* /say Value is: `${variable}`
  
_Pass parameters to scripts:_  
* `/mish initArcher ${player=John}`
  
_Save flexibility with escape sequences:_  
* use `\` to escape syntax symbols like `\${word\}` and `\\` to display `\`
  
_Use `if` statement to check variable value:_  
  ```mish
  if ${isServer}
      /say This script is run by a server
  else
      /say This script is run by ${player}
  ```
   
  It's important not to put __/__ before mish pseudo-commands. The __/command__'s are treated as minecraft commands for better compatibility.
   
_Use incrementing and condition testing syntax features_:  
* ${a`+=`b}
* ${${c}`==`Hello}
* ${${d}`!=`Hello}
* ${${e}`<=`10}
* ${${f}`>=`10}
* ${${g}`<`5}
* ${${h}`>`5}

```mish
if ${${a}==1}
    ...
else if ${${a}==2}
    ...
else
    ...
```

   
_Use `while` statement to create loops:_  
  ```mish
  ${i=0}
  while ${${i}<10}
      /say I = ${i}
      ${i+=1}
  ```
   
_Use `print` command to send message to the one who executed the command:_  
* `print This is my message`

_And `log` to send message to the server:_  
* `log ${player} has just executed the command`

## built-ins:
* __player__

  The name of the one who executed the command

* __isServer__

  True if the command has been executed within the physical the server
  
## server-side:
If server supports __mish__ then calling __/mish__ within the client side will execute 
scripts located in __server_folder/scripts__. This can be used to create rpg presets and so on.

If an operator calls __/mish script__ then the server will firstly search for __op_script.mish__ and if there's no such file the server will seach for __script.mish__. Non-op players are not able to execute __op\___ files.

Any command inside a script is executed by actual server game object, so take care of what you allow players to execute there.

## params:
* __--raw__

  executes script commands with mish syntax parsing disabled.
 
* __--max-loop-depth n__

  sets maximum amount of code repeats inside loops. That's because if you get an infinite loop somehow
  you won't be able to stop it via minecraft console
  
* __--noop__

  Forces mish to execute non-operator scripts if called by an operator.
  

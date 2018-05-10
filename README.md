# Minecraft Shell
Allows to put minecraft commands in a separate file and execute it

## usage:
* __/mish [--args] <script> [params]__
  
  executes __script.mish__ located in __.minecraft/scripts__ or __minecraft/scripts__ or __server_folder/scripts__

## syntax:
_Comment you code with __#__:_  
* `# hi there!`

_Manage variables:_  
* __${variable1=variable2=...=variablen=value}__  
* /say Value is: __${variable}__
  
_Pass parameters to scripts:_  
* __/mish initArcher ${player=John}__
  
_Save flexibility with escape sequences:_  
* use __\\__ to escape syntax symbols like __\\${word\\}__ and __\\\\__ to display __\\__
  
_Use __if__ statement to check variable value:_  
  ```mish
  if ${isServer}
       /say This script is run by a server
   else
       /say This script is run by ${player}
   ```


## params:
* __--raw__

  executes script commands with mish syntax parsing disabled.
 
* __--max-loop-depth n__

  sets maximum amount of code repeats inside loops. That's because if you get an infinite loop somehow
  you we won't be able to stop it via minecraft console
  
* __--noop__

  Forces mish to execute non-operator scripts if called by an operator.
  
_Benefits of using mish syntax_:

## server-side:
If server supports __mish__ then calling __/mish__ within the client side will execute 
scripts located in __server_folder/scripts__. This can be used to create rpg presets and so on.

## examples:
/give __${player}__ minecraft:bow

/give __${player} ${item} ${amount}__

/scoreboard players set @e[type=__${type}__] __${score}__ 10


__\#__ I don't know why you might want to do this but...

/say __${what=Something} ${what}__

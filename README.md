# Minecraft Shell
Allows to put minecraft commands in a separate file and execute it

## syntax:
* __/mish [--raw] <script> [args]__
  
  executes __script.mish__ located in __.minecraft/scripts__ or __minecraft/scripts__ or __server_folder/scripts__

## params:
* __--raw__

  executes script commands with mish syntax parsing disabled. If set the [args] at the end are ignored
  
_Benefits of using mish syntax_:
* use __#__ to define a comment
* use __${variable1=variable2=...=variablen=value}__ to define a variable
* use __${variable}__ to get value of it
* pass parameters to scripts via __/mish initArcher ${player=John}__
* use __\\__ to escape syntax symbols like __\\${word\\}__ and __\\\\__ to display __\\__

## server-side:
If server supports __mish__ then calling __/mish__ within the client side will execute 
scripts located in __server_folder/scripts__. This can be used to create rpg presets and so on.

## examples:
/give __${player}__ minecraft:bow

/give __${player} ${item} ${amount}__

/scoreboard players set @e[type=__${type}__] __${score}__ 10


__\#__ I don't know why you might want to do this but...

/say __${what=Something} ${what}__

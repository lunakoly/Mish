# Minecraft Shell
Allows to put minecraft commands in a separate file and execute it

## usage:
* __/mish [--params] <script> [args]__
  
  executes __script.mish__ located in __.minecraft/scripts__ or __minecraft/scripts__ or __server_folder/scripts__

## syntax:
_Mish introduces some brand new code features that can help you in writing scripts_:

First of all, you can define a comment with __#__:

`# hi there!`

* variables

  use __${variable1=variable2=...=variablen=value}__ to define a variable
  use __${variable}__ to get value of it
  pass parameters to scripts via __/mish initArcher ${player=John}__
  use __\\__ to escape syntax symbols like __\\${word\\}__ and __\\\\__ to display __\\__

_Mish also introduces some pseudo-commands for managing th code flow_:
To check some value use an if statement
if ${isServer}
    say Servers says HI
else
    print You are not a server/ right?

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

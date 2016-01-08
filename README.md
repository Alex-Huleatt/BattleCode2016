# BattleCode2016

## Signaling system

### comm.Comm
Has sendSignal and receiveSignal functions.
sendSignal takes a Comm.SignalInfo object and receiveSignal (hopefully) returns a SignalInfo object.

### comm.SignalInfo
Has a bunch of fields that get serialized and deserialized.
* SignalType is the type of signal (comm.SignalType is enum)
* targetLoc is the location the signal is referencing.

### comm.SignalType
Bunch of types of signals pretty straightforwards, names are descriptive.

###Usage.

You do 

``` Java
SignalInfo si = new SignalInfo();
si.type = SignalType.HALP;
si.targetLoc = me;

c.sendSignal(si, 10); //10 is radius
```



## Battle plan

* Send out scouts to look for supply caches.
    * I want numbers to be in our favor.

* Archons should initially spread in the hope of acquiring supply caches before enemy.

* Later we should group, zombies will be stronk

* Scouts report supply caches, archons go there.
    * For large ones, we could send force ahead of archon to defend location.

* Guards/Soldiers as escort for archons

* Create priority queue of supply locations
    * Priority is based on value/distance or some such thing; want nearby and valuable cells.

* In the meantime, construct a relatively stronk defensive location
	* Turrets and guards, maybe place rubble at strategic locations, e.g. right at the min range for turrets.

* I'd like our soldiers/guards to be able to swap between which archons to protect, farther the distance from defense loc, more soldiers/guards

* Need to def defensive loc

* Use lots of scouts for (hopefully) real(-ish)time metrics
    * Location of enemy units/archons
    * Dens
    * Zombies

* Larger radio signals cost more core delay
    * Global signals maybe not ideal.
    * If we extend range by 30 --> (.05 + .03 * 30) = 

##Apocalypse strat

* Send vipers and scouts towards enemies.
* Infect scouts
* ???
* Profit.

* Zombies choose nearest location, we'd need to guarantee that our viper or scouts are not the nearest.
    * Alternatively, we use a scout to strafe the enemies with a following zombie.
    * Our weapon is a pair of scout/fast zombie.
    * We could have multiple zombies per scout.
    * Scout just cruises past enemies, no one cares if he dies.
    * Let zombies do rest.

* Move delay for scout is 1.4
* Move delay for fast zombie is 2

* Attack range for zombie is 2

* **Scouts can kite fast zombies**

* Keep Scout *Exactly* the proper distance away.
    * Have "Bait" mood.
    * run towards enemies
        * If he sees other scouts, we need to rush as close as possible.
        * Any other unit we could potentially strafe around, make another unit have closer euclidean distance.

* We can have scouts **save** units by getting closer to zombies and running away.
	* I **like** this idea. 
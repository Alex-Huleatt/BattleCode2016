# BC-Frameworks

## Inspiration

I've seen many people decide not to do BattleCode due to inexperience unwillingness to deal with the boilerplate code. I think that BattleCode is one of the neatest things to do for fun in Computer Science, and I'd like to see if I can't spread the joy a bit.

This is not meant to give people a free pass to victory, but rather to let people maybe focus more on the strategy part of BattleCode. 

## What am I looking at?

Each of the packages has some code to help solve some of the common problems in BattleCode. Mostly I think that good design is pretty key to a good submission, just because once you get ~2k LOC, in my experience, it becomes unbearable to program any new features if you haven't put in the effort for a good design. 

Some of this code is optimized for bytecode, and a lot of it is designed to be as pleasant to use and modify.
The only thing I put a lot of time into optimizing is the A* implementation in the movement package. I like to think I did okay.

After 2015 BC, I decided to move away from 2D arrays for grid representations, and started using a lot of hash structures. It costs more bytecode, but keeps the code simple. If you find yourself running out of bytecode, that's probably one of the first places I would look into optimizing. 

We have:

* Path planning
	* A*
	* Tangent Bugging

* Communication system
	* Signed messages
	* Much prettier read/write

* A pretty neat little state machine/Turing machine inspired architecture for controlling all the robots.

* A little util package for little functions that I always end up wanting

## BTW
This was tested entirely in the 2013 distribution of BattleCode, I'll try to update it as soon as the 2016 version comes out.

## For anyone and everyone

Go Bobcats.



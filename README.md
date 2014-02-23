jMonkey-M7002E
==============

This is a program developed for the cource M7002E Computer graphics with virtual environments.

It contains the basic features: 
 - Enclosed enviroment with water and a skybox.
 - Model creation using basic primitives.
 - Instancing where meshes are reused, cloned. (The only clear example is the mast of the ship, I realize now more meshed could be shared. )
 - Atleast three objects can be moved smoothly.

The extra features i've implemented:
 - Textures i've found at http://texturelib.com/. And a model of a palm found at http://www.the3dmodelstore.com/free.php.
 - Advanced mesh made of vertex arrays. 
 - Saveable classes used as user data on a node?
 - That ship is impressive!?

Ive used code from 
 - http://jmonkeyengine.googlecode.com/svn/branches/stable-alpha4/engine/src/test/jme3test/model/shape/TestCustomMesh.java
 - https://code.google.com/p/jmonkeyengine/source/browse/trunk/engine/src/test/jme3test/water/TestPostWater.java

Controlls
=========
 - a,s,d,w,q,z moves the camera. 
 - r toggles the ships movement.
 - x,c turns the ship (only when it moves).
 - space sinks the big ship.
 - e toggles the earthquke.
 - b toggles waterbobbing.

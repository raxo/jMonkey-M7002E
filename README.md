jMonkey-M7002E
==============

This is a program developed for the cource M7002E Computer graphics with virtual environments.

It contains the basic features: 
 - Move around the world. 
 - Selectable objects. 
 - command pallette of tools in form of CANNONS!
 - You can change the camera view when selecting a ship. 
 - Somewhat realistic!

The extra features i've implemented:
 - Proximity based interaction when a ship gets close to the island!
 - Heads up display with instructions when you select a ship. 
 - Ships spawn in at intervals. 
 - Scaleable collision solution
 - Cannons!
 - Super intense pirate edventure music from: http://www.youtube.com/watch?v=PJUmKF0w1fw and http://www.youtube.com/watch?v=pMhfbLRoGEw randomly
 - You can ram ships
 - Ships can sink
 - Ships can go COLORS with the correct cannon!
 - Stuff gets removed when they die. 
 - 

Ive used code from 
 - http://jmonkeyengine.googlecode.com/svn/branches/stable-alpha4/engine/src/test/jme3test/model/shape/TestCustomMesh.java
 - https://code.google.com/p/jmonkeyengine/source/browse/trunk/engine/src/test/jme3test/water/TestPostWater.java
 - http://hub.jmonkeyengine.org/wiki/doku.php/jme3:beginner:hello_picking
 - http://hub.jmonkeyengine.org/wiki/doku.php/jme3:advanced:mouse_picking
 - http://hub.jmonkeyengine.org/wiki/doku.php/jme3:advanced:making_the_camera_follow_a_character
 - http://www.jmonkeyengine.org/doc/constant-values.html#com.jme.input.KeyInput.KEY_0

Controlls
=========
 - a,s,d,w,q,z moves the camera. 
 - r toggles the ships movement.
 - a,s,d,w navigates the ship when it is selected.
 - Select ships by clicking on them
 - Fire cannons by clicking on them.
 - Fire cannons while the ship is selected with 0,1,2,...,9 or left, right arrows
 - Escape the ship by clicking on it or another
 - Different cannons do different stuff. destroy, scale up, scale down, repaint, and activates onClick. 

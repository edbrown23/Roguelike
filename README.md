Roguelike
=========

This is largely a project which served to experiment with various ideas, rather than create any sort of full experience.
I implemented the following features:
  - An essentially infinite world divided into chunks, which are saved for returning to later
  - The world is three dimensional, though the viewport is orthogonal from above, and thus it appears 2D
  - A Chunk Loading system which silently loads and unloads chunks in the background
  - Perlin noise provided the landscape, though separate chunks are not matched on their edges
  - A basic date and time system, with sun rises and sets which change the lighting in the world
  - A water and erosion simulation, where water flows down inclines taking a bit of land with it
  - An incredibly basic cellular automaton for trees, with the intention of allowing forests to grow naturally

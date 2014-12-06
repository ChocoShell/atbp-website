Adventure Time Battle Party
=========

This is a website containing information about [Adventure Time Battle Party], a browser MOBA game by Cartoon Network.

Technology
--------
* [Scala] - An object-functional programming language
* [Play] - A scalable web framework in Scala
* [AngularJS] - A structural framework for dynamic web apps
* [Bootstrap] - UI Boilerplay for modern web apps


Development
----
To run the website locally for development
* Download [sbt]

```sh
git clone https://github.com/ChocoShell/atbp-website.git
cd atbp-website
sbt ~ run
```
then press Enter to start automatically compiling on file changes.

You can access the site by going to localhost:9000, the default port.


The project is divided into two parts:
1. Play Scala - Backend
2. AngularJS - Frontend

###Back End
The Play Backend reads the json file and parses them into classes defined in the models folder, this will most likely be ported to angular later on.

The Backend should also be able to receive guide posts and put them in a datastore.

###Front End

The Frontend handles all the views for the atbp.json file.

There should also be a page that sends the data for Guide Submissions to the backend.

[Adventure Time Battle Party]:http://www.cartoonnetwork.com/games/adventuretime/adventure-time-battle-party/
[sbt]:http://www.scala-sbt.org/
[Scala]:http://www.scala-lang.org/
[Play]:https://www.playframework.com/
[AngularJS]:https://angularjs.org/
[Bootstrap]:http://twitter.github.com/bootstrap/

## General Description

Tired of deleting some suspicious-looking comments from "your" code before commit? If so, then this plugin is just for you.

Comment Cleaner is a tool that helps developers in managing all comments in source files of their projects in an easy and convenient way.
It allows you to review every single comment in your code so that you can decide which one is "safe" and which should be deleted.

Or you can simply just delete all the comments at once. It's better to be safe than sorry.

**Tip:** If you accidentally deleted some (or all) of your comments you can simply undo these changes by pressing: Ctrl + z. 

## Plugin Showcase

There's gonna be a GIF or YouTube link presenting usage of our plugin

## Installation Guide

Follow these steps to build and install our plugin in your own IntelliJ IDEA instance!

### 1. Clone repository

1. Open IntelliJ IDEA and select **File → New → Project from Version Control...**
2. As *Version Control* Select **Git** and in *URL* field paste link to this repository: https://github.com/micdom4/comment_cleaner.git  
3. Once the project has been fetched and loaded you can verify the contents of the source files (if you want to).

### 2. Build the plugin

1. Select **Gradle** tool window (the one with elephant icon) from your right sidebar.
(If you don't see this icon on your right sidebar select **Gradle** tool window from that "three dot" menu on the left sidebar)
2. In Gradle window navigate to: **comment_cleaner → Tasks → intellij platform → buildPlugin**.
3. Run **buildPlugin** task by double-clicking on it.
4. After this task expand **build** and then **distributions** directory. 
If the Gradle build of the plugin was successful you should see ready-to-install plugin file called: **comment_cleaner-1.0.0.zip**

### 3. Install plugin in IntelliJ

1. Go to the **Settings** window: **File → Settings...** (Or just click **Ctrl + Alt + s**)
2. On the right pane select **Plugins** section.
3. In this section click on the **gear** icon on the right side of **Installed** tab and select **Install Plugin from Disk**.
4. Now select the path to .zip file (comment_cleaner-1.0.0.zip) you have built in the previous step.
The IntelliJ may warn you about potential danger of installing the so-called "third party plugins" but don't worry, our plugin is completely safe ;)
5. Once the plugin was successfully installed to your IntelliJ (you may have to restart IntelliJ after installation) you should see the *Comment Cleaner* plugin in **Installed** tab in **Plugins** section.

To verify that the plugin has been properly installed in your IntelliJ expand the **Code** tab.
Then go with your cursor at the very bottom of this tab. 
There you should see **Comment Cleaner** field.
After clicking it, dialog windows of the plugin should pop up allowing you to fully use our plugin in your project!

**Congratulations!**
Now you can safely use various "popular programming tools" without a worry that some overlooked suspicious comments will expose true source of "your" code ;).

## Authors

251505 micdom4

258681 Mxkyp

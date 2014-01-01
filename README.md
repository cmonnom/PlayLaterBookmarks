PlayLaterBookmarks
==================

Add a search bookmark feature to PlayLater. Searches queries persist after reboot and are stored locally.

The application is available as an executable jar here:  PlayLaterBookmarks / PlayLaterBookmarks2 / exec-jar

I made this application for people who need to have search queries persist when they close PlayLater and/or reboot their computer. The search terms are bookmarked, not the results in PlayLater.

The software consists of a list of search queries. Each query is clickable and, when doing so, the text is copied to the PlayLater window (if it exists) and the query is submitted.
Search queries are saved in a local file next to the jar. It can be manually edited (one search item per line) with notepad or anything fancier, or new items can be added directly in the application.

The current limitation is sending the String to the text area of PlayLater. I have not figured out a way to do that in a 100% automated way yet. So far I can only bring the PlayLater window to the front and activate it. So, in order for the "paste and submit" to work, the user has to first click on the search box of PlayLater then click on a bookmark.

Since I used JNA, copy-paste-and-submit onyl works on Windows for now.

Hopefully this application is a temporary hack until guys at PlayLater add a bookmark functionality.

In the meantime, enjoy!

For developers who know a way to pass the limitation described above, let me know!

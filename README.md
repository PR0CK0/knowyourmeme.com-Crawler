# knowyourmeme.com-Crawler
This app crawls the website knowyourmeme.com's confirmed memes and stores the meme: title, content origin year, content origin, meme origin year, meme year, tags and category(ies). It uses the jsoup API.

The initial meme information is taken from a table on the right-hand side of each meme on a specific knowyourmeme.com page. A second pass for more specific information is done by parsing the user-written text for the meme. This allows the app to collect content/meme origin and year, if these are all available.

Make sure to include the jsoup.jar to your buildpath.

everyConfirmedMeme.html is the html file containing links to every confirmed meme (as of February 2019) on knowyourmeme.com.

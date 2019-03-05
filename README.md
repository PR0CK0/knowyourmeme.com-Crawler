# knowyourmeme.com Meme Crawler and CSV/RDF converter
This app allows the user to crawl the website knowyourmeme.com's confirmed memes and stores the meme: title, content origin year, content origin, meme origin year, meme origin, tags and category(ies). Additionally, the user can crawl any specific meme (confirmed or not) by simply entering the meme's URL. It uses the jsoup API. Written in Java. A lot of HTML parsing; the code in these classes is as decoupled and cohesive as possible, but some of it is inherently hardcoded.

#Basic Info
The initial meme information is taken from a table on the right-hand side of each meme on a specific knowyourmeme.com page. A second pass for more specific information is done by parsing the user-written text for the meme. This allows the app to collect content/meme origin and year, if these are all available.

In addition, the app converts the plaintext results of each meme parse into CSV and RDF (for ontologies) form. All of these can be exported, and multiple memes can be crawled at once.

Make sure to include the jsoup.jar to your buildpath.

everyConfirmedMeme.html is the html file containing links to every confirmed meme (as of February 2019) on knowyourmeme.com.

# A Word of Caution
It should be noted that if this project is intended to be used some time well beyond February 2019, the authors cannot guarantee that the HTML-parsing code is correct. The website structure may have changed in that time. Refer to classes MemeCrawler, MemeURLGrabber and MemeBODYCrawlerUpdated to see the HTML-parsing code.

Author: NicoM
Date: 2019-04-24

In order to guarantee best practicability the scripts to add all needed configurations to the Postgresql database are found in this folder.
Please execute them in ascending order ( 1000 -> 1010 -> 1020 )

The first script (1000_CreateUser.sql) will create the user "banking_user" with the needed rights to perform the tasks of this application.

In the second script (1010_CreateTableSpace.sql) the tablespace dbo will be created with a mapping to a local path in the filesystem.
This will make sure to make the data persistent, however I am unsure if this script will work like this in a "dockerized" postgres server.

The third script (1020_CreateTable.sql) will create the table "banking_sys" and set the encoding to "UTF-8".

I would prefer to be able to use the collation "Latin1_General_CS_AS" but I couldn't select this in my installation.
Additionally I was not able to use other values besides "German_Germany.1252", since my installation would also
throw exceptions.

The reason for preferring the above mentioned collation results in the wide safety it guarantees since it is case sensitive
which could be important in some applications and use cases.
Additionally the accent sensitivity could also be of importance to correctly sort.
The other two cases might be more specific and "rare" in real use but still should be considered since the kanatype-insensitivity
will not distinguish between Hiragana and Katakana which is considered to make sorting japanese characters more intuitive. (https://japanese.stackexchange.com/questions/29612/what-do-you-need-kanatype-sensitivity-for)
And it becomes more and more common to see japanese characters in western phone books for example.
Last but not least the width-insensitivity will consider the one byte and two byte representation the same in the context of sorting.
It is arguable if the last one is the preferred method but I personally didn't have to take this into consideration in any real-world
application I wrote for my current company.

Disclaimer:
Since this is a test application to showcase my abilities in programming and application/architecture design, I decided to
not further investigate this and use the working constellation. In a more "serious" setting this would get changed, but my
limited time would better be used to finish the project.

Author: NicoM
Date: 2019-05-06

In order to guarantee best practicability the scripts to add all needed master data to the Postgresql database are found in this folder.
Please execute them in ascending order ( 1000 -> 1010 -> 1020 )

The first script (1000_user_import.sql) will create two users to enable loggin in to the application.
The password will be in the following pattern for the imported users:

username+current_year+exclamation_mark

so for admin it will be 'admin2019!'

The second script (1010_update_hibernate_sequence.sql) will update the hibernate sequence of the database to start at 1000.
This is done to prevent ID clashes for data which got imported by a masterdata script and data created by the application.

Remark:
In a real project the above solution is risky and not adviced at all!

A system to define import scripts as csv files and let the system import them automatically upon startup is a nice solution
which could be done. ( "Startup" services which hook to the start of the webserver and perform actions before the application itself is started )
With the packaging of the application done as EAR this could be done by collecting all csv import files, which are in a specific folder, and putting them in a
generated-masterdata.jar for example and the startup service then would extract the files of this jar and perform the import.

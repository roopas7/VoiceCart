VoiceCart
=========

voice cart android app


Copyright (C) 2014 Roopa Satyanarayan

Overview
--------

Voice cart is an android app which serves as a handy grocery shopping cart. Apart from being able to save your shopping cart, it has other handy features including one click email or cloud save options. One of the major issues when shopping is to remember what to purchase. Voice Cart tries to solve that problem by having an option to auto generate your shopping cart based on previous purchase history and the frequency of purchase of specific items. In addition to these, it has custom voice commands to ease data entry. 

Usage
-----
The APP support data entry through text or voice. The microphone button prompts the user to speak.
The spinner menu has four options 
 - Save
     This option saves the shopping cart to memory. The data is indexed by the date you set through the calendar option.
 - Restore
     This option restores previously saved data from memory to the table. As before, the data to be retrieved is indexed by the date set on the calendar
 - Email
     This option allows the user to email the shopping cart or save it in google drive. 
 - AutoFill
     This uses built in intelligence in the APP and knowledge of purchase history to auto generate the shopping cart. The items to be auto generated depends on the frequency of purchase of the item. The frequency is compared to the duration between previous purchase date of the item and current set date to evaluate whether or not to auto fill. 

More information is present in the documentation folder. 

References
----------

Speech to Text:-
http://developer.android.com/reference/android/speech/package-summary.html
http://www.jameselsey.co.uk/blogs/techblog/android-how-to-implement-voice-recognition-a-nice-easy-tutorial/

Email and account management:-
http://stackoverflow.com/questions/2197741/how-to-send-email-from-my-android-application

Android widget integration and other general information:-
http://developer.android.com/
http://stackoverflow.com/

Source Code
-----------

All the source code is hosted here https://github.com/roopas7/VoiceCart

Build Information
-----------------

THe link to the APK file is https://github.com/roopas7/VoiceCart/blob/master/bin/VoiceCart.apk
In addition you can download the entire source code in your hard disk and set the projects workspace in Eclipse as the download location. To install the APP on your phone from eclipse, right-click on the project name, choose run As-> Android Application. Make sure the phone is connected through USB and that you have developer options enabled. 
To clone the data into your local disk, do git clone https://github.com/roopas7/VoiceCart.git
You can also install the APP through google play store (https://play.google.com/store/apps/details?id=com.roopa.VoiceCart&hl=en)



Copyright information
---------------------

https://github.com/roopas7/VoiceCart/blob/master/LICENSE

Contributors
------------

Roopa Satyanarayan

Contact Information
-------------------

Roopa Satyanarayan : rs7@pdx.edu (or) roopasiyer26@gmail.com



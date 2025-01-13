# Introduction

Welcome to **LE/EECS 4443 (Section M, Labs 01-02, Winter 2025)**! 

My name is [Shogo](https://ca.linkedin.com/in/shogo-toyonaga) and I will be your teaching assistant. I look forward to working and learning with you over the next four months 😊.
The goal of this course shell is to share and disseminate supplementary materials (recitations) that re-inforce important course learning objectives. Furthermore, as the course progresses, commonly asked questions and hints about labs will be released here!

**Note:** If you prefer to view and access these resources in a web-based format, use this link: [https://shogz-labs.github.io/EECS4443_W25_Assets/](https://shogz-labs.github.io/EECS4443_W25_Assets/). 


## Labs 

### Lab 1 (Demo_Android)
1. I keep getting errors when importing **Demo_Android**! The app won't event run.... 

    **TA Response:** Firstly, please make sure that you have downloaded the lab from [here](https://github.com/yorku-ease/EECS4443-Demos). The older source is known to have problems with newer versions of Android Studio.

    If the bugs persist, please do the following and let me know if you are still facing difficulties:

    - Upgrade Gradle to the recommended version
    - Upgrade the dependencies as suggested by the Android SDK Update Assistant
    - Sync Project with Gradle Files 
    - Rebuild the Project 

2. I see that Task 5 asks for source-code comments. To what extent should I be documenting my code?

    **TA Response:** You should use **in-line** comments for any small modifications that you make to the existing Java code (e.g., New Variables, Connecting an Event Listener). 
    If you are adding new methods (e.g., implementing an interface and overriding method(s)), then you should use **Javadoc**. 

    Any modifications that you make to the layout or resources (e.g., String) should be documented using in-line style. 
    XML uses the same commenting style as HTML. See below:
    
    ```xml
        <!-- This is how we add comments in XML :) -->
    ```

    It would also be appreciated if you include a brief summary of what you implemented and how you did it in the header block comment which includes your name, student id, etc., 

3. Should I submit my Lab once it is checked off or is it okay to submit it prematurely?

    **TA Response:** If the application passes the demo and you have sufficiently documented your code, feel free to submit it before talking to me!
    However, some students in the past have had to make changes to their work in the lab sessions and forgot to re-submit the zip file. In such cases, you will lose marks for this. I recommend submitting your work once we have gone through it together to avoid this.

4. I'm completely lost. I've spent hours trying to do this lab and nothing is working...

    **TA Response:** Please reach out to the teaching team over Slack ASAP. You should post your questions in the general chat section so that everyone benefits! I'm sure other people are feeling the same way as you and have similar questions. **Please let us know what you have tried first.**

    Lastly, you can refer to [Demo_Elections.zip](https://github.com/Shogz-Labs/EECS4443_W25_Assets/blob/main/ta_recitations/demos/DemoElections.zip). Look at what I've done and use the documentation to understand how the code and xml work together.

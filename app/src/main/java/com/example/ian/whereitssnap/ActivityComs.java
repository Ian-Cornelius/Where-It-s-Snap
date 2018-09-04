package com.example.ian.whereitssnap;

/**
 * Created by Ian on 3/21/2018.
 */

//Handle communication between various fragments (classes on their own). Implemented by mainActivity

public interface ActivityComs {

    void onTitlesListItemSelected(int pos);
    void onTagsListItemSelected(String tag);

    /*
    Remember, we only declare the methods to be used, but not define them. Defined only where implemented

    We add the specific codes to the specific classes or activities that will use them. We do this using an instance of the interface

    The central point of communication implements this interface, so as to talk/communicate to all connected classes/activities

    Finally, gotten a hang of this shit. This is how it goes.

    The communicating activities, have an instance of this interface. They use this instance to invoke its methods.

    The central point of communication (where all messages head), implements this interface. This therefore means, that invocation
    of the interface's methods is implemented there, handling the message sent to it. Thus, placing it as the recepient.(Where all shit
    happens). This central point can then disburse messages elsewhere using normal object to object communication.

    Can choose access modifiers and different argument identifiers at implementer. But argument data types and name of methods
    CANNOT be changed
     */

}

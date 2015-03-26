# evend
Run your event from your mobile phone.

Take the stress out of your event with the evend app. Participants of an event can sign up and view details of your event using this application.

#### Notes

1. In order to successfully test your application, you will need to set up a backend server which is currently written in node.js (with mongodb backend) and is available [here] (https://github.com/raghavneesh/hh-appserver/) (_instructions to run the server can be found in the server repository_.)

2. Once the server is hosted you will need to change the value of ``` server_url ``` in ``` android/res/values/strings.xml ``` to point the host correctly, before you complie the application.

3. The unit tests are present the ``` test/ ``` folder, which can be opened up as a separate project and will needed to be compiled independent of the main project.
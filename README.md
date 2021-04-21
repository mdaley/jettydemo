# NOTES

Can't get latest Spring Boot to work with Jetty 11. Read that they support Jetty 10 but that 9.4 is the default. No mention
of Jetty 11 though.

Well it works with Jetty 10 if you have the web socket dependencies excluded. But when they aren't excluded, the web server
fails to run because of missing classes.

So, Spring Boot 2.4.5 can't run with anything beyond latest Jetty 9 at the moment, i.e. 9.
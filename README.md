# NOTES

Can't get latest Spring Boot to work with Jetty 11. Read that they support Jetty 10 but that 9.4 is the default. No mention
of Jetty 11 though.

Well it works with Jetty 10 if you have the web socket dependencies excluded. But when they aren't excluded, the web server
fails to run because of missing classes.

So, Spring Boot 2.4.5 can't run with anything beyond latest Jetty 9 at the moment, i.e. 9.

# ENVIRONMENT VARIABLES

SERVER_PORT - port on which the server runs (default 8080 if not set)
TRUSTSTORE - path to the truststore containing root certificates required to establish trust of remote parties
TRUSTSTORE_TYPE - the type of the truststore (e.g. jks, pkcs12, ...)
TRUSTSTORE_PASSWORD - the password for the truststore
TRUSTSTORE_PASSFILE - path to a file containing the truststore password (alternative to using TRUSTSTORE_PASSWORD)

KEYSTORE - path to the keystore containing the server certificate
KEYSTORE_TYPE - the type of the keystore (e.g. pkcs12, jks, ...)
KEYSTORE_PASSWORD - the password for the keystore
KEYSTORE_PASSFILE - path to a file containing the keystore password (alternative to using KEYSTORE_PASSWORD)

USE_LOGSTASH - if this equals 'true' write log files in Logstash Json format
APP_LOGGING_LEVEL - the level of logging at which the system should run from: trace, debug, info, warn, error. (default: info)


# Configuration used by the scripts
# LOCATION:scripts/configuration.sh
#

# Freeciv-web database
DB_NAME=freeciv_web
DB_USER=andreas
DB_PASSWORD=changeme

# If you are sharing tomcat with other apps, you may want the stop scripts
# to only stop freeciv-web and not the whole tomcat instance. Setup
# tomcat-manager and change TOMCATMANAGER to Y, adding proper user and password
# info. If the password is empty, it will be asked every time a start or stop
# script is run.
TOMCATMANAGER=N
TOMCATMANAGER_USER=tomcat-user
TOMCATMANAGER_PASSWORD=

# If the user running Freeciv-web don't have access to sudo set this to N
# so shutting down Freeciv-web won't cause stop-freeciv-web.sh to try to
# disable Freeciv-web in NGINX by deleting it from /etc/nginx/sites-enabled/
NGINX_DISABLE_ON_SHUTDOW=Y

# Use a lightweight Apache Tomcat image
FROM tomcat:9.0-jdk17-openjdk

# Remove the default Tomcat root web apps
RUN rm -rf /usr/local/tomcat/webapps/ROOT

# Copy your pre-built .war file directly into Tomcat's webapps directory as ROOT
COPY target/bloodlink.war /usr/local/tomcat/webapps/ROOT.war

# Open up port 8080 for web traffic
EXPOSE 8080

# Launch the Tomcat server instance
CMD ["catalina.sh", "run"]

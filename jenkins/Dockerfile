FROM jenkins/jenkins:lts-jdk17

ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false -Dhudson.model.DirectoryBrowserSupport.CSP=''"
COPY security.groovy /usr/share/jenkins/ref/init.groovy.d/security.groovy
COPY jobs/ /jobs/
COPY run.sh /run.sh
COPY plugins.txt /usr/share/jenkins/ref/

USER root
#RUN xargs /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt && \
RUN jenkins-plugin-cli -f /usr/share/jenkins/ref/plugins.txt && \
apt-get -y update && \
apt-get -y install sudo && \
apt-get -y install jq && \
sudo usermod -aG staff jenkins && \
echo "jenkins ALL=(ALL) NOPASSWD: ALL" >> /etc/sudoers
ENV PATH "$PATH:/usr/local/bin/dind/"

ENTRYPOINT ["bash","-x","/run.sh"]

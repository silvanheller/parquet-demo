FROM ubuntu:16.04

# utilities
RUN apt-get -y update; apt-get -y install nano apt-utils software-properties-common #apt-transport-https curl vim #git

ENV WORKING_DIR /workshop
#Create dir to create files in
RUN mkdir -p ${WORKING_DIR}; chmod 777 ${WORKING_DIR}


# updating JDK to version 8
RUN add-apt-repository -y ppa:webupd8team/java && apt-get update; \
    echo debconf shared/accepted-oracle-license-v1-1 select true | debconf-set-selections && echo debconf shared/accepted-oracle-license-v1-1 seen true | debconf-set-selections; \
    apt-get -y install oracle-java8-installer; \
    cd /usr/lib && ln -s jvm/java-8-oracle java
ENV JAVA_HOME /usr/lib/java/jre

# updating scala to 2.11.8
#RUN mkdir -p /usr/lib/scala-2.11.8 && curl 'http://www.scala-lang.org/files/archive/scala-2.11.8.tgz' | tar -xz -C /usr/lib/; \
#    ln -s /usr/lib/scala-2.11.8 /usr/lib/scala
#ENV SCALA_HOME /usr/lib/scala
#ENV PATH "$PATH:$SCALA_HOME/bin"

# sbt
#RUN echo "deb https://dl.bintray.com/sbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list; \
#    apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823 && apt-get update; \
#    apt-get -y install sbt; \
#    apt-get -y remove openjdk-9-*

# spark
RUN curl http://d3kbcqa49mib13.cloudfront.net/spark-2.1.0-bin-hadoop2.7.tgz | tar -xz -C /usr/lib/; \
    cd /usr/lib && ln -s spark-2.1.0-bin-hadoop2.7 spark
ENV SPARK_HOME /usr/lib/spark

# port for spark UI
EXPOSE 4040

#Startscript
COPY bootstrap.sh ${WORKING_DIR}/bootstrap.sh
RUN chown root.root ${WORKING_DIR}/bootstrap.sh; chmod 700 ${WORKING_DIR}/bootstrap.sh

ENTRYPOINT ["/workshop/bootstrap.sh"]
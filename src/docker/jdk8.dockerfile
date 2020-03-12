FROM centos:7

RUN yum -y update && yum clean all
RUN yum -y install java-1.8.0

COPY addressapi*.jar /home/app.jar
COPY entrypoint.sh /home

RUN chmod +x /home/entrypoint.sh
RUN mkdir /home/config

EXPOSE 8080

ENTRYPOINT ["/home/entrypoint.sh"]

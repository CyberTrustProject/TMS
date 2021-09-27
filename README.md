# Trust Management
Trust management is structured as listener service (TMS-listener-module project) and a REST service (TMS-rest project). Both projects should be built and run.

The listener service (TMS-listener-module project) and the REST service (TMS-rest project) are standard MVN projects, so standard MVN build procedures apply. Both projects support the "install" target which builds a "fat jar" with all dependencies integrated. The "fat jar" can run with a "java -jar..." command.


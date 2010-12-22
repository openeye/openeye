
OpenEye - Business Process Management Integration Platform
==========================================================
version 2.0.0.Alpha1, December 2010

This software is distributed under the terms of the FSF Lesser Gnu
Public License (see lgpl-3.0.txt). 

Introduction
------------

Open Eye is a web based Business Process Management and Business Intelligence platform licensed under the LGPL v3.

The main objective of Open Eye is to provide a high-end BPM and BI solution including reporting and portfolio management, for any companies at a minimal cost of acquisition and ownership.

The Open Eye platform reuses Open Source components to deliver an integrated stack and off-the-shelf BPM and BI solution.

Components
- Process modeling (Signavio's Activiti Modeler)
- Process design, development and testing (Eclipse and Activiti Eclipse Plugin)
- Process runtime engine (Activiti)
- Process monitoring, reporting and analytics (Birt)
- Rich web interface for process management, process execution and process monitoring (Richfaces)
- Framework for component and service integration (Seam)

Content
- Process templates based on best practices and industry standards
- Report templates based on best practices and industry standards


Get Up And Running
------------------

Here follows instructions on how to setup, build and run Open Eye from the source code. 

1. How to build and run Open Eye from sources

1.1 Download

The source files can be downloaded from github located at the following url: git@github.com:openeye/openeye.git. The runtime environment can be downloaded from sourceforge located at the following url: http://sourceforge.net/projects/open-eye/files/openeye-server/releases/OpenEye-2.0.X.zip

1.2 Tools

The following tools are recommended for building Open Eye from the source files: Java JDK SE 6 (Java development toolkit), Ant (automates the software building process), Git (source code management), Eclipse (integrated development environment).

1.3 Setup

Install Java JDK , Ant, Git, Eclipse and the runtime environment (the runtime environment will be referenced as <openeye.home>). Import the downloaded Eclipse project into Eclipse. Open the build.properties file and change the value of <jboss.home> to point to <openeye.home>/setup/files in your runtime environment. 

1.4 Build

Select project/clean to build the distribution.

1.5 Verify

Make sure that the openeye.ear and openeye-ds.xml is present in <openeye.home>/setup/files.

1.6 Run
Change to <openeye.home>/setup and type ant openeye.setup.


2. How to deploy process definitions

2.1 Login

Open your point your web browser to http://localhost:8080/openeye and enter username: admin and password: admin to login.

2.2 Deploy

Change to the Definitions tab and click on the Create Deployment button. Select any of the process definitions available from <openeye.home>/setup/files/processes. Upload the process definition archive.

2.3 Verify

Change to the Tasks tab and click on the New button. Select a process from the drop-down list. Fill in the start form and start the process. A new task should now be visible in the Unassigned task list. Right click on the task and select Claim task from the drop-down list. The task should now be moved to the Assigned Tasks list. Select the task to view the associated task form. Fill in the data and click on the Complete button.

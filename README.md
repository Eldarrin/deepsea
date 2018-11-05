# deepsea

## take note :) : This is an experimental project, so many things do not work!

## README

This is a simple (at the moment) project to recreate an Open Source Insurance application based on Vertx and Openshift. A long way to go yet :)

And special thanks to Clement Escoffier and Eric Zhao for the initial tutorials that pointed me this way.

Thanks, Andy

### Currently it is using Redis for Pub/sub, mongo for audit writes, mysql for reviewable data, infinispan for clustering. Also need to write tests for everything.

## Initial Layout
### Deepsea Insurance
###	Sales
		Payment Processing
		Telesales
		Retention
		Optimisation
		Sales Portal
###	Administration
		Cash Settlement
		Policy Management
			Enrolment
		Risk Management
		Claims Adjudication
		Loss Adjustment
		Fraud Investigation
		Billing
		Deductibles
		Self Service Portal
		Tele Assisted Service
###	Underwriting
		Actuarial
			Bordereau
		Book Management
		Re/Co-Insurance
		Settlements
###	Shared 
		Document Management
		Document Fulfilment
		Regulatory Compliance
		Complaints Management
		Finance
		Reporting
		Tax

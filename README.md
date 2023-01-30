# CMPE 172 Starbucks Project

This is the end-to-end project for CMPE 172 (Enterprise Software Development) at the San Jose State University, Spring 2022. This a multi-tear end-to-end project that consists of the following components:
    - A web application that allows cashiers to manage their customers' orders (Cashier's app)
    - A mobile application that allows customers to pay for their orders (Starbucks app)
    - A Starbucks API that handles the requests from the Cashier's app and the Starbucks app
    - A database that stores the information of the orders and cards

Here is a video demonstration of the project:

[![Watch the video](images/thumbnail.png)](https://youtu.be/WPwfxN7hsjU)

<br>

## Architecture

While the Starbucks mobile app is in the user's phone, Cashier's app is deployed to the Google Cloud, where it is scaled by a proxy server. The Starbucks API itself is also deployed to the Google Cloud as a separate deployment unit, wrapped and secured with Kong API Gateway proxy. Only this API has the access to the starbucks database, which is also on the Google Cloud. Both Cashier's app and Starbucks API are Spring Boot applications, while the Starbucks mobile app is a Java based application.

Here is the architecture diagram of this project:

![](images/diagram.png)

The Cashier app and especially Starbucks API are scalable, have multiple pods. They can handle million user requests, and load balancer helps to distribute the requests to the pods (external load balancer for the cashiers and Kong's internal load balancer for all requests that go to Starbucks API). However, there is a limitation to the number of active orders. There can only be one active order for one register at the time, and if a request for placing a new order into register is sent, it will be rejected and will not be processed, and it does not depend on the pods, since all API pods get data only from the database and do NOT have static data (I removed the activeOrders hashmap and udpated the code).

This can be improved further if RabbitMQ is added to the play. For instance, we could send place order requests to the RabbitMQ queue first, and orders will be there on hold, awaiting for their turn. Then, the API will get the next order from the queue for the registry and make it active, until there are no more orders in the queue. Unfortunately, due to time constraints, I had no time to implement RabbitMQ.

All other necessary tech stack required for the project was used accordingly, and below, in the journal, you can find more about how the project was built.

<br>

# Project Journal

This is a weekly/daily journal that I have been writing about the project. Most of the work on the project was started a week before the it was due, mostly due to having other class projects and assignments, and also labs for this class. While the journal itself was committed to this repo right before the due date, the screenshots were taken earlier (some of them had to be taken again as some of key screenshots of progress were lost or missing at the time). The project was completed on the due date, and the journal contains 7 days of work.

## Week 1 - Pre-project work

Week before I even started the project, I experimented with some of the given started files for the project: Starbucks Mobile app and Starbucks API. The Starbucks API was mostly the same from the previous labs, so I retested it again with Postman just to make sure it works locally, with local MySQL also, and it works.

![](images/screenshot1.png)

It was already tested before for the lab, so API was OK. However, running the mobile app was quite difficult. It was quite difficult to build the jar file with Gradle. I had to install gradle 4.9 and switch to jdk8, and both took me some time too. That was not enough, unfortunately. After couple days of trying, I figured out that I had to change dependency versions of juint:

![](images/screenshot2.png)

I also created a custom batch file to quickly switch to jdk8 in console when needed:

![](images/screenshot3.png)

As a result, I was able to finally have a jar file ready.

Then, I spent time on rewatching some lectures, thoroughly analyzing the requirements, and trying to make a decent architecture in my mind. I was not able to spend more time that week due to this class's lab 10, and other class assignments and projects.

<br>

## Week 2 - Main project work

Commits for this day:
https://github.com/Ahrorus/starbucks/commit/6c5a725fb67c0dba1d3955d5970841f969d03b1a
https://github.com/Ahrorus/starbucks/commit/6dbd06d34cdd95d2c25b0d51b5698f854e040b97
https://github.com/Ahrorus/starbucks/commit/ae00c6f1032f8fb7686ca8d9112901285ecee105
https://github.com/Ahrorus/starbucks/commit/19bd1be05650520d95268ddcd1ea1da5d8ac6da5

This is the week where I did the main work for the project (development, configurations, troubleshooting, deploying, and testing). I split the logs below into days and what I mostly did in those days.

### Day 1 - Starbucks API with Local Cloud SQL

Commits for this day:
https://github.com/Ahrorus/starbucks/commit/c7ad14f4d7f0b7a9cbf7f667924388b77bfa59b1

This was when I spent hours of configuring the MySQL on Google Cloud properly and configuring environment variables on the Starbucks API. Of course, first I had to create MySQL instance on the cloud, and these are steps.

![](images/cloudsql/screenshot2.png)

Then, I configured a new user into the mysql. I also made a database for the project in there.

![](images/cloudsql/screenshot3.png)

While mysql was editing on the cloud, I was configuring the properties for the starbucks api.

![](images/cloudsql/screenshot8.png)

I built a new 3.1 image for starbucks api locally and pushed to my repo. I also ran the container image with the environment variables that for Cloud MySQL. The MYSQL_HOST was targeting the public IP address of Cloud SQL (For testing purposes, I had it publicly accessible and had to add my home IP address to list of allowed addresses).

![](images/cloudsql/screenshot9.png)

Nothing much was changed for the starbucks api, mostly the same from the lab 6. I tested it locally, and it worked. I made some testes with Postman (created card and did get cards), and then I connected to the cloud sql on Google Console shell, and tested the table there, and it was working - my local starbucks api image was connected to the cloud sql.

I had various issues on this part, and was stuck sometimes, but I figured it out by googling and paying close attention to my environment variables, eventually.

![](images/cloudsql/screenshot5.png)

![](images/cloudsql/screenshot10.png)

![](images/cloudsql/screenshot11.png)

<br>

### Day 2 - Deploy Starbucks to GKE

Commits for this day:
https://github.com/Ahrorus/starbucks/commit/b723d4d0c280ed037c4d09bb9b623cd1c440273f
https://github.com/Ahrorus/starbucks/commit/535f9535f591a7e2a96df91d83390e6eb04ecca8

Now that I am sure that local image of starbucks api was working properly and connected to MySQL on cloud, I had to deploy it, and deploying it in first try was not easy.

First, I updated yaml file, pushed the image, and deployed it. I also created service and jumpbox.

![](images/cloudsql/screenshot12.png)

![](images/cloudsql/screenshot13.png)

![](images/cloudsql/screenshot14.png)

Unfortunately, deployment was unhealthy (it had errors, could not connect to the mysql instance). I was stuck in this part for almost a day, could not figure out why.

![](images/cloudsql/screenshot15.png)

![](images/cloudsql/screenshot16.png)

![](images/cloudsql/screenshot17.png)

![](images/cloudsql/screenshot18.png)

I checked the Logs on the cloud multiple times. I realized it could not connect to the database, but I could not comprehend why. I though my environment variables were problematic, so I changed them multiple times and redeployed multiple times and tried researching, enabling APIs, and doing various things. Evidently, I realized what happened.

After stopping local testing of api, I switched MySQL instance to Private IP address and removed public connection. But the private IP address is different from the public one, and my deployment.yaml was still pointing to public one. Once I changed that, it was all good.

![](images/cloudsql/screenshot22.png)

![](images/cloudsql/screenshot20.png)

![](images/cloudsql/screenshot19.png)

Then, I tested the reachability of the API on the cloud with jumpbox, and it was working. I also started preparing screenshots and a journal this day.

<br>

### Day 3 - Starbucks API with Kong API Gateway

Commits for this day:
https://github.com/Ahrorus/starbucks/commit/b723d4d0c280ed037c4d09bb9b623cd1c440273f
https://github.com/Ahrorus/starbucks/commit/535f9535f591a7e2a96df91d83390e6eb04ecca8

I spent some time troubleshooting here too as I had some issues while wrapping the API with Kong. Nonetheless, I did not spend too much time like I did on setting up Cloud SQL connection and deploying the api. I first got the Kong running and saved the IP.

![](images/kong/screenshot2.png)

Then, I configured the Kong API Gateway with my service and tested reachability with jumpbox, and it worked.

![](images/kong/screenshot3.png)

Then, I added a key authentication and generated client key.

![](images/kong/screenshot4.png)

![](images/kong/screenshot5.png)

![](images/kong/screenshot6.png)

Then, I tested the reachability of the API on the cloud with jumpbox, while providing the api key and Kong IP, and it worked.

![](images/kong/screenshot7.png)

I imported the Kong IP and api key into Postman and ran some tests, and it worked, created some cards on the API.

![](images/kong/screenshot8.png)

![](images/kong/screenshot9.png)

![](images/kong/screenshot10.png)

To make sure that the deployed API is connected with the Cloud SQL, I had to connect to the Cloud SQL and check the table there, but I could not connect as usually. I spent the rest of the time troubleshooting and trying to find a way to connect to the Cloud SQL through console shell.

![](images/kong/screenshot11.png)

Apaprently, since I turned off public IP address for cloud mysql, I could not connect through google console shell as usually, so I had to follow tutorial (got stuck multiple times) and create a VM from where I could connect to cloud sql.

![](images/kong/screenshot12.png)

![](images/kong/screenshot13.png)

![](images/kong/screenshot14.png)

![](images/kong/screenshot15.png)

![](images/kong/screenshot16.png)

Finally, I was able to connect to the Cloud SQL from the VM. I realized later that I could have simply used jumpbox... In any case, here are those two cards I created previously. Finally, the Starbucks API was connected to the Cloud SQL, deployed to GKE with 4 pods, load balanced with Kong's internal load balancer, and proxied through Kong API Gateway.

![](images/kong/screenshot18.png)

![](images/kong/screenshot19.png)

I also made some screenshots that day.

<br>

### Day 4 - Cashier App

Commits for this part of this day:
https://github.com/Ahrorus/starbucks/commit/546e47a00cb6d16f951ea0a9d89ebf306052ae26

Since backend is done (mostly), I started working on the frontend, Cashier App. The first thing I did, I used the provided example, Rest Client, and changed it to my needs, and I also added login/sign up feature (admin login) from lab 4, spring-security. 

![](images/cashier/screenshot1.png)

![](images/cashier/screenshot2.png)

Made some simpple design: added Log out button on top left, added options to select drink, size, and milk (not all options work together), and three buttons - Get Order, Place Order, and Clear Order.

![](images/cashier/screenshot3.png)

![](images/cashier/screenshot4.png)

It was running good, with some bugs, at first. Later, when I added extra features (like error handling) and did refactoring, I could not use it no more. Unfortunately, I had trouble connecting to the API from here, and I spent even more time troubleshooting this. For example, when I tried to placing a new order, got error, and no order was placed in the API.

![](images/cashier/screenshot5.png)

![](images/cashier/screenshot6.png)

![](images/cashier/screenshot7.png)

![](images/cashier/screenshot8.png)

![](images/cashier/screenshot9.png)

I was stuck with this problem, and tried various ways to fix it. Next day, I fixed it.

<br>

### Day 5 Cont - Mobile App

Commits for this part:
https://github.com/Ahrorus/starbucks/commit/56106c49c35ba7798e0641f220630ef0e4b08014

Also, I was able to build the jar file for mobile app and run it. I made an order in Postman and payed for it in the mobile app. I connected to the API from mobile app using Kong credentials.

![](images/screenshot4.png)

![](images/screenshot5.png)

![](images/screenshot6.png)

<br>

### Day 5 Cont - Upgrading Starbucks API

Commits for this part:
https://github.com/Ahrorus/starbucks/commit/85963a83446d9ef60372c7d7dd04001c4ecd13c3

Another big update I made was the Starbucks API. Apparently, it tracked the active orders using a HashMap object that is in memory of the API. Problem is, there are 4 API pods, and that object is not synced. I needed to make the Starbucks API completely dependent on the Database. That way data is synced and it's more easily and safely scalable. I updated all the order requests, especially the get active order request to use the database instead of the hashmap.

![](images/screenshot7.png)

**Scalability and Active Orders**

Here are the new rules of the API after my update:

1. There can only be one active order per registry
2. Order cannot be placed into the registry if there is already an active order
3. If active order is payed (processed), its status is changed, and new orders can be placed now
4. If active order is cleared (discarded), it is deleted

Though we have one database, we have multiple pods that are covered with Kong's load balancer. Thus, our API is scalable! It can handle many active users: both cashiers and customers. The only problem is, there can be many registries at the same time, the active orders are limited - one per registry, and if user tries to place an order, he has to wait until the active order is cleared/processed. This can be fixed if there was a queue of waiting orders where once the active order was processed / cleared, the next order can be placed from the queue. Here is where we can implement RabbitMQ solution. Unfortunately, I was not able to implement it due to time constraints.

Nontheless, here are some tests I made to show the updates:

I created new orders in Postman, across different registries.

![](images/screenshot8.png)

I pay for one of the orders.

![](images/screenshot9.png)

![](images/screenshot10.png)

If I try to place a new order for that registry, it works, but not for the other one that already has an active order pending.

![](images/screenshot11.png)

![](images/screenshot12.png)

I clear the active order on one of the registries and try adding a new order for that registry again, and it works.

![](images/screenshot13.png)

![](images/screenshot14.png)

Now I have 3 orders when I get all orders: one registry with one active order, and other registry with active and payed order.

![](images/screenshot15.png)

That's it! My Starbucks API is now fully dependent on the database, and it's scalable. I also just proved that each pod can handle many active registries, but each registry can have one active user, no matter what api pod is being used because database is still the same. As I said already, I did not have time to implement RabbitMQ, but if I did, I would have stored awaiting orders there and get new active orders from that queue.

![](images/screenshot16.png)

<br>

### Day 6 - Upgrading Cashier App

Commits for this day:
https://github.com/Ahrorus/starbucks/commit/f7aed5230ef7fd707bf0ba0f52c021d6975d8263
https://github.com/Ahrorus/starbucks/commit/5bae66d902ed94cdab6d7fd3a1c0eb311c06e291

I fixed my Cashier Web App, and added capability of choosing stores, where each store represents a registry in the API. Finally, it was all working. I also added some error handling to avoid it crashing.

![](images/cashier/screenshot10.png)

I did some testing of the functionality. I placed 2 new orders for 2 separate registries, and checked them in the API.

![](images/cashier/screenshot11.png)

![](images/cashier/screenshot12.png)

![](images/cashier/screenshot13.png)

Then, I tested Get Order for the other regisrty that has no active orders. And then I tested same function for the registry that has active order.

![](images/cashier/screenshot14.png)

![](images/cashier/screenshot15.png)

Then, I payed for the order in registry C through the API and checked it in the Cashier App. Results were accurate - no active order for that registry anymore.

![](images/cashier/screenshot16.png)

![](images/cashier/screenshot17.png)

![](images/cashier/screenshot18.png)

I also tested Clear order twice for the same registry and it works as it should.

![](images/cashier/screenshot19.png)

![](images/cashier/screenshot20.png)

<br>

### Day 6 Cont - Deploying Cashier App

Commits for this day:
https://github.com/Ahrorus/starbucks/commit/33db6fdbaf8096b24a3ca94c7642ff1e328653f9
https://github.com/Ahrorus/starbucks/commit/c415b400626b7466b1ddf2108b96e5e5a1f17dc7

Now that all functionalities work and error handled, I can deploy the Cashier App to GKE.

First, I had to build the image for the Cashier App, and push it on Dockerhub. 

![](images/cashiercloud/screenshot1.png)

Then, I had to make deployment and service. At some point, I had to redeploy again, because it would not work for some reason (deployment would be unhealthy, according to the external load balancer).

![](images/cashiercloud/screenshot2.png)

![](images/cashiercloud/screenshot3.png)

![](images/cashiercloud/screenshot4.png)

Then, I made an external load balancer for the Cashier App.

![](images/cashiercloud/screenshot5.png)

![](images/cashiercloud/screenshot6.png)

It all worked, but I had an issue that the Cashier App made me relogin too frequently. That was because I needed to update load balancer, to not switch to different pods during a session (added session affinity for Client IP).

![](images/cashiercloud/screenshot7.png)

![](images/cashiercloud/screenshot8.png)

Now it's done. Cashier App is deployed, load balanced, and connected to the API.

![](images/cashiercloud/screenshot9.png)

<br>

### Day 6 Cont - Testing it all

I prepared the database fro the Mobile app and Cashier app, cleared everthing, and then I tested main functionalities of Cashier app and Starbucks Mobile App.

Launching Mobile App.

![](images/screenshot16.png)

![](images/screenshot17.png)

Getting active orders for the registry and then placing new order.

![](images/screenshot18.png)

![](images/screenshot19.png)

Paying for the order and confirming the order and confirming that it's not active no more.

![](images/screenshot20.png)

![](images/screenshot21.png)

Confirming the orders and cards in Postman and database.

![](images/screenshot22.png)

![](images/screenshot23.png)

![](images/screenshot24.png)

That's it! Everything works. I did not screenshot every single Postman requests since they already work since the lab 5 that I did. I'm proud of my work, and if you want to watch a short demo, scroll above to the top of the readme.

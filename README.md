<a name="readme-top"></a>

<!-- PROJECT LOGO -->
<br />
<div align="center">
	<a href="https://amazon.com/">
		<img 
			src="https://images.crowdspring.com/blog/wp-content/uploads/2023/07/03162944/amazon-logo-1.png" 
			alt="Amazon logo" >
	</a>
	<h2 align="center">
		Amazon E-commerce Application Clon
  	</h2>
</div>

<!-- ABOUT THE PROJECT -->

## 1. About The Project

This project develops an event-driven microservices E-commerce system. 


#### 1.1. Features
This system currently consists of the following developed services.
<ul>
	<li>
		An API gateway that also acts as an authentication point to authorize and route client's requests to corresponding services. 
    This service handles authentication, registration, password recovery, account verification, deactivation and deletion.
	</li>
	<li>
		A user service that handles more fine-grained data such as personal user information, addresses and banking.
	</li>
  <li>
    A mailing service that handles account verification, password recovery, order confirmation by notifying users via email.
  </li>
	<li>
		A functional interface that is a replica of the current Amazon website displaying E-commerce services to end users.
	</li>
</ul>

#### 1.2. Built With

The primary tools that are used to develop this application are:
- Spring Boot
- Node.js
- Express.js
- React
- Apache Kafka
- Docker
- PostgreSQL
